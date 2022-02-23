/*
  User: Cloudy
  Date: 09/02/2022
  Time: 04:11
*/

package cz.cloudy.minecraft.core.componentsystem;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import cz.cloudy.minecraft.core.LoggerFactory;
import cz.cloudy.minecraft.core.componentsystem.annotations.*;
import cz.cloudy.minecraft.core.componentsystem.interfaces.ICommandResponseResolvable;
import cz.cloudy.minecraft.core.componentsystem.interfaces.IComponent;
import cz.cloudy.minecraft.core.componentsystem.types.CommandData;
import cz.cloudy.minecraft.core.types.Pair;
import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;

/**
 * @author Cloudy
 */
@Aspect
public class ComponentAspect {
    private static final Logger logger = LoggerFactory.getLogger(ComponentAspect.class);

    /**
     *
     */
    protected static final Map<Class<? extends Event>, Function<Event, World>> worldOnlyEventAccessors = new HashMap<>();

    /**
     *
     */
    protected static final Map<Method, Map<Integer, Object>> cachedCache      = new HashMap<>(); // Method => ArrayHashCode => Value
    /**
     *
     */
    protected static final Map<String, List<Method>>         cacheIdMap       = new HashMap<>();
    /**
     *
     */
    protected static final Map<Method, Pair<Long, Object>>   cooldownCacheMap = new HashMap<>();

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public static <T extends Event> void addWorldOnlyEventAccessor(Class<T> type, Function<T, World> function) {
        Preconditions.checkState(!worldOnlyEventAccessors.containsKey(type), type.getSimpleName() + " WorldOnly event accessor already exists.");
        worldOnlyEventAccessors.put(type, (Function<Event, World>) function);
    }

    /**
     * -
     *
     * @param joinPoint -
     * @return -
     */
    @Around("@annotation(cz.cloudy.minecraft.core.componentsystem.annotations.Async)")
    public Object asyncExecution(ProceedingJoinPoint joinPoint) {
        Object thisObject = joinPoint.getTarget();
        Preconditions.checkState(!thisObject.getClass().isAnnotationPresent(Component.class), "Async can be used only on components");
        IComponent component = (IComponent) thisObject;
        Bukkit.getScheduler().runTaskAsynchronously(
                component.getPlugin(),
                () -> {
                    try {
                        Object returnValue = joinPoint.proceed(joinPoint.getArgs());
                        if (returnValue instanceof ICommandResponseResolvable response && joinPoint.getArgs().length == 1 &&
                            joinPoint.getArgs()[0] instanceof CommandData commandData) {
                            commandData.sender().sendMessage(response.getComponent(commandData));
                        }
                    } catch (Throwable e) {
                        logger.error("Async method exception: ", e);
                    }
                }
        );
        return null;
    }

    /**
     * -
     *
     * @param joinPoint -
     * @return -
     * @throws Throwable -
     */
    @Around("execution(@cz.cloudy.minecraft.core.componentsystem.annotations.Cached(informative=false) * *(..))")
    public Object aroundCached(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Preconditions.checkState(methodSignature.getReturnType() != Void.class, "Cached cannot be used on void return types");
        Cached cached = method.getAnnotation(Cached.class);

        int hash = Arrays.deepHashCode(joinPoint.getArgs());
        Map<Integer, Object> map = cachedCache.computeIfAbsent(method, m -> new HashMap<>());
        if (map.containsKey(hash))
            return map.get(hash);
        Object value = joinPoint.proceed(joinPoint.getArgs());
        if (value == null && !cached.saveNull())
            return null;
        cacheIdMap.computeIfAbsent(cached.id(), s -> new ArrayList<>()).add(method);
        map.put(hash, value);
        return value;
    }

    /**
     * -
     *
     * @param joinPoint -
     * @return -
     * @throws Throwable -
     */
    @Around("execution(@cz.cloudy.minecraft.core.componentsystem.annotations.Benchmarked * *(..))")
    public Object aroundBenchmarked(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String name = method.getName();
        Benchmarked benchmarked = method.getAnnotation(Benchmarked.class);
        Stopwatch stopwatch = Stopwatch.createStarted();
        Object value;
        try {
            value = joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable e) {
            stopwatch.stop();
            logger.info("Banchmark-throw [{}]: {} ms", name, decimalFormat.format(stopwatch.elapsed(benchmarked.unit())));
            throw e;
        }
        stopwatch.stop();
        logger.info("Banchmark [{}]: {} ms", name, decimalFormat.format(stopwatch.elapsed(benchmarked.unit())));
        return value;
    }

    /**
     * -
     *
     * @param joinPoint -
     * @return -
     * @throws Throwable -
     */
    @Around("execution(@cz.cloudy.minecraft.core.componentsystem.annotations.Cooldown * *(..))")
    public Object aroundCooldown(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Cooldown cooldown = method.getAnnotation(Cooldown.class);
        long current = System.currentTimeMillis();

        logger.info("COOLDOWN {}: {}", method.getName(), current);
        Pair<Long, Object> pair = cooldownCacheMap.get(method);
        Object value;
        if (pair != null) {
            if (current < pair.getKey() + cooldown.value()) {
                return pair.getValue();
            }

            logger.info("REFRESH");
            pair.setKey(current);
            value = joinPoint.proceed(joinPoint.getArgs());
            pair.setValue(cooldown.returnLastValue() ? value : null);
            return value;
        }

        logger.info("NEW");
        value = joinPoint.proceed(joinPoint.getArgs());
        cooldownCacheMap.put(method, new Pair<>(current, cooldown.returnLastValue() ? value : null));
        return value;
    }

    /**
     * -
     *
     * @param joinPoint -
     * @return -
     * @throws Throwable -
     */
    @Around("(within(@cz.cloudy.minecraft.core.componentsystem.annotations.WorldOnly *) && (execution(@org.bukkit.event.EventHandler * *(..)) || execution(@cz.cloudy.minecraft.core.componentsystem.annotations.CommandListener * *(..)))) || " +
            "execution(@cz.cloudy.minecraft.core.componentsystem.annotations.WorldOnly * *(..))")
    public Object aroundWorldOnly(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        WorldOnly worldOnly = method.getAnnotation(WorldOnly.class);
        if (worldOnly == null)
            worldOnly = method.getDeclaringClass().getAnnotation(WorldOnly.class);
        boolean canRun = false;
        for (Object arg : joinPoint.getArgs()) {
            String worldName = null;
            if (Event.class.isAssignableFrom(arg.getClass())) {
                Function<Event, World> handler = worldOnlyEventAccessors.entrySet().stream()
                                                                        .filter(entry -> entry.getKey().isAssignableFrom(arg.getClass()))
                                                                        .map(Map.Entry::getValue)
                                                                        .findFirst()
                                                                        .orElseThrow(() -> new RuntimeException(
                                                                                "No WorldOnly event accessor found for " + arg.getClass().getSimpleName()));
                worldName = handler.apply((Event) arg).getName();
            } else if (CommandData.class.isAssignableFrom(arg.getClass())) {
                CommandData commandData = (CommandData) arg;
                worldName = commandData.getPlayer().getWorld().getName();
            }

            if (worldName == null)
                continue;

            List<String> worldFilter;
            canRun = (!worldOnly.filter().isEmpty() && (worldFilter = ComponentStaticDataInitializer.worldFilters.get(worldOnly.filter())) != null &&
                      worldFilter.contains(worldName)) ||
                     ArrayUtils.contains(worldOnly.worldNames(), worldName);
            break;
        }

        if (!canRun)
            return null;

        return joinPoint.proceed(joinPoint.getArgs());
    }

    /**
     * -
     *
     * @param joinPoint -
     * @return -
     * @throws Throwable -
     */
    @Around("execution(@org.bukkit.event.EventHandler * *(..))")
    public Object aroundEventHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Object thisObject = joinPoint.getTarget();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (!thisObject.getClass().isAnnotationPresent(Component.class) &&
            !method.isAnnotationPresent(ComponentUnbound.class) &&
            !thisObject.getClass().isAnnotationPresent(ComponentUnbound.class))
            return null;
        return joinPoint.proceed(joinPoint.getArgs());
    }
}
