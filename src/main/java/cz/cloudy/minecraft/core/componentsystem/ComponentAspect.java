/*
  User: Cloudy
  Date: 09/02/2022
  Time: 04:11
*/

package cz.cloudy.minecraft.core.componentsystem;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import cz.cloudy.minecraft.core.LoggerFactory;
import cz.cloudy.minecraft.core.componentsystem.annotations.Cached;
import cz.cloudy.minecraft.core.componentsystem.interfaces.ICommandResponseResolvable;
import cz.cloudy.minecraft.core.componentsystem.interfaces.IComponent;
import cz.cloudy.minecraft.core.componentsystem.types.CommandData;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.bukkit.Bukkit;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Cloudy
 */
@Aspect
public class ComponentAspect {
    private static final Logger logger = LoggerFactory.getLogger(ComponentAspect.class);

    /**
     *
     */
    protected static final Map<Method, Map<Integer, Object>> cachedCache = new HashMap<>(); // Method => ArrayHashCode => Value
    /**
     *
     */
    protected static final Map<String, List<Method>>         cacheIdMap  = new HashMap<>();

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    /**
     * -
     *
     * @param joinPoint -
     * @return -
     */
    @Around("@annotation(cz.cloudy.minecraft.core.componentsystem.annotations.Async)")
    public Object asyncExecution(ProceedingJoinPoint joinPoint) {
        Object thisObject = joinPoint.getTarget();
        Preconditions.checkState(thisObject instanceof IComponent, "Async can be used only on components");
        IComponent component = (IComponent) thisObject;
        System.out.println("ASYNC!");
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
        logger.info("CACHED: {}", hash);
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
        Stopwatch stopwatch = Stopwatch.createStarted();
        Object value;
        try {
            value = joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable e) {
            stopwatch.stop();
            logger.info("Banchmark-throw [{}]: {} ms", name, decimalFormat.format(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
            throw e;
        }
        stopwatch.stop();
        logger.info("Banchmark [{}]: {} ms", name, decimalFormat.format(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
        return value;
    }
}
