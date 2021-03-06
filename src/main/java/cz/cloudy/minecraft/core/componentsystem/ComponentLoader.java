/*
  User: Cloudy
  Date: 06/01/2022
  Time: 20:39
*/

package cz.cloudy.minecraft.core.componentsystem;

import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import com.google.common.base.Preconditions;
import cz.cloudy.minecraft.core.CorePlugin;
import cz.cloudy.minecraft.core.CoreRunnerPlugin;
import cz.cloudy.minecraft.core.LoggerFactory;
import cz.cloudy.minecraft.core.componentsystem.annotations.*;
import cz.cloudy.minecraft.core.componentsystem.interfaces.IClassScanListener;
import cz.cloudy.minecraft.core.componentsystem.interfaces.ICommandResponseResolvable;
import cz.cloudy.minecraft.core.componentsystem.interfaces.IComponent;
import cz.cloudy.minecraft.core.componentsystem.interfaces.IComponentLoadListener;
import cz.cloudy.minecraft.core.componentsystem.types.ActionListenerData;
import cz.cloudy.minecraft.core.componentsystem.types.CommandData;
import cz.cloudy.minecraft.core.componentsystem.types.ComponentData;
import cz.cloudy.minecraft.core.componentsystem.types.CronData;
import cz.cloudy.minecraft.core.componentsystem.types.command_responses.ErrorCommandResponse;
import cz.cloudy.minecraft.core.componentsystem.types.command_responses.ParameterCountErrorCommandResponse;
import cz.cloudy.minecraft.core.componentsystem.types.command_responses.PermissionErrorCommandResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Cloudy
 */
public class ComponentLoader {
    private static final Logger                                logger            = LoggerFactory.getLogger(ComponentLoader.class);
    private static final Map<Class<?>, ComponentData>          componentMap      = new HashMap<>();
    private static final List<CronData>                        cronListeners     = new ArrayList<>();
    private static final Map<String, List<ActionListenerData>> actionListeners   = new HashMap<>();
    private static final List<Class<?>>                        stashedComponents = new ArrayList<>();

    private static class ComponentLoadObject {
        public Class<?>  clazz;
        public Component data;

        /**
         * Default constructor
         *
         * @param clazz Component type
         * @param data  Component data
         */
        public ComponentLoadObject(Class<?> clazz, Component data) {
            this.clazz = clazz;
            this.data = data;
        }
    }

    private static final List<ComponentScan> oldComponentScans = new ArrayList<>();
    private final        List<ComponentScan> componentScans;

    private static final List<IClassScanListener>     classScanListeners     = new ArrayList<>();
    private static final List<IComponentLoadListener> componentLoadListeners = new ArrayList<>();

    /**
     * Default constructor
     */
    public ComponentLoader() {
        componentScans = new ArrayList<>();
    }

    /**
     * Adds class scan listener
     *
     * @param listener Listener
     */
    public void addClassScanListener(IClassScanListener listener) {
        classScanListeners.add(listener);
    }

    /**
     * Adds component load listener
     *
     * @param listener Listener
     */
    public void addComponentLoadListener(IComponentLoadListener listener) {
        componentLoadListeners.add(listener);
    }


    private void getComponentScansFromClass(Class<?> clazz, List<ComponentScan> scanList) {
        if (clazz == Object.class)
            return;

        ComponentScans scans = clazz.getDeclaredAnnotation(ComponentScans.class);
        if (scans == null) {
            ComponentScan scan = clazz.getDeclaredAnnotation(ComponentScan.class);
            if (scan != null)
                scanList.add(scan);
        } else {
            scanList.addAll(Arrays.stream(scans.value()).toList());
        }

        getComponentScansFromClass(clazz.getSuperclass(), scanList);
    }

    /**
     * Reads all component scans from class
     *
     * @param clazz Class
     */
    public void readComponentScansFromClass(Class<?> clazz) {
        List<ComponentScan> scanList = new ArrayList<>();
        getComponentScansFromClass(clazz, scanList);
        if (scanList.isEmpty())
            return;

        for (ComponentScan componentScan : scanList) {
            componentScans.add(componentScan);
        }
    }

    /**
     * Loads all components for plugin.
     *
     * @param caller Plugin
     */
    public void loadAllComponents(CorePlugin caller) {
        logger.info("Reading all {} component scans for components", componentScans.size());
        List<ComponentLoadObject> classList = new ArrayList<>();
        List<Map.Entry<ComponentScan, Class<?>[]>> packageList = new ArrayList<>();

        List<Class<?>> removeList = new ArrayList<>();
        for (Class<?> stashedComponent : stashedComponents) {
            if (!checkConfiguration(caller, stashedComponent))
                continue;
            removeList.add(stashedComponent);
            classList.add(new ComponentLoadObject(stashedComponent, stashedComponent.getAnnotation(Component.class)));
        }
        stashedComponents.removeAll(removeList);

        ClassLoader classLoader = caller.getClass().getClassLoader();
        for (ComponentScan componentScan : componentScans) {
            ConfigurationBuilder builder = new ConfigurationBuilder()
                    .setExpandSuperTypes(true)
                    .addScanners(Scanners.SubTypes.filterResultsBy(s -> true));
            String filterBy = null;
            if (!componentScan.value().isEmpty()) {
                builder.forPackage(componentScan.value(), classLoader);
                filterBy = componentScan.value();
            }
            if (componentScan.classes().length > 0) {
                String commonPackageName = null;
                for (Class<?> clazz : componentScan.classes()) {
                    builder.addUrls(ClasspathHelper.forClass(clazz, classLoader));
                    String packageName = clazz.getPackageName();
                    if (commonPackageName == null)
                        commonPackageName = packageName;
                    else {
                        if (commonPackageName.contains(packageName)) {
                            commonPackageName = packageName;
                        } else if (!packageName.contains(commonPackageName)) {
                            commonPackageName = "";
                        }
                    }
                }
                if (!commonPackageName.isEmpty()) {
                    filterBy = commonPackageName;
                }
            }

            final String finalFilterBy = filterBy;
            Reflections reflections = new Reflections(builder);
            Set<Class<?>> classes = reflections.getAll(Scanners.SubTypes).stream()
                                               .filter(s -> finalFilterBy == null || s.startsWith(finalFilterBy))
                                               .map(s -> {
                                                   try {
                                                       return Class.forName(s);
                                                   } catch (ClassNotFoundException e) {
                                                       throw new RuntimeException(e);
                                                   }
                                               })
                                               .collect(Collectors.toSet());
            for (Class<?> clazz : classes) {
                if (componentMap.containsKey(clazz) || classList.stream().anyMatch(componentLoadObject -> componentLoadObject.clazz == clazz))
                    continue;
                Component component = clazz.getAnnotation(Component.class);
                if (component == null)
                    continue;

                if (!checkConfiguration(caller, clazz)) {
                    stashedComponents.add(clazz);
                    continue;
                }
                classList.add(new ComponentLoadObject(clazz, component));
            }
            packageList.add(new AbstractMap.SimpleEntry<>(componentScan, classes.toArray(new Class[0])));
        }

        // Component creating
        for (ComponentLoadObject componentLoadObject : classList) {
            logger.info("Loading \"{}\" component", componentLoadObject.clazz.getSimpleName());
            Constructor<?> constructor;
            try {
                constructor = componentLoadObject.clazz.getConstructor();
            } catch (NoSuchMethodException e) {
                logger.error("Component has no empty constructor. Skipping");
                continue;
            }
            try {
                Object obj = constructor.newInstance();

                registerComponentAnnotations(caller, obj);
                componentMap.put(componentLoadObject.clazz, new ComponentData(obj, componentLoadObject.data, caller));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logger.error("Failed to create component", e);
            }
        }

        // Dependency filling
        for (ComponentLoadObject componentLoadObject : classList) {
            Object component = componentMap.get(componentLoadObject.clazz).component();
            Preconditions.checkNotNull(component);
            List<Field> fields = ReflectionUtils.getAllClassFields(componentLoadObject.clazz);
            for (Field field : fields) {
                Component annotation;
                if ((annotation = field.getAnnotation(Component.class)) == null)
                    continue;

                Class<?> componentClass = field.getType();
                Preconditions.checkState(
                        componentMap.containsKey(componentClass),
                        "Component " + componentLoadObject.clazz.getSimpleName() + " could not find its dependency " + componentClass.getSimpleName()
                );
                Object dependencyComponent = componentMap.get(componentClass).component();
                Preconditions.checkState(
                        (annotation.wireType() == Component.WireType.All || annotation.wireType() == Component.WireType.AutoWireOnly) &&
                        (annotation.wiredFrom().length == 0 ||
                         Arrays.stream(annotation.wiredFrom())
                               .anyMatch(type -> type.isAssignableFrom(componentLoadObject.clazz))),
                        "Component " + componentLoadObject.clazz.getSimpleName() + " tried to wire forbidden dependency " + componentClass.getSimpleName()
                );

                field.setAccessible(true);
                try {
                    field.set(component, dependencyComponent);
                } catch (IllegalAccessException e) {
                    logger.error("Error setting component dependency object", e);
                }
                field.setAccessible(false);
            }

            for (IComponentLoadListener loadListener : componentLoadListeners) {
                loadListener.load(component);
            }

            if (component instanceof IComponent componentObject) {
                componentObject.onLoad();
            }
        }

        for (Map.Entry<ComponentScan, Class<?>[]> entry : packageList) {
            for (IClassScanListener classScanListener : classScanListeners) {
                classScanListener.scan(entry.getValue());
            }
            for (ComponentData value : componentMap.values()) {
                if (!(value.component() instanceof IComponent c))
                    continue;

                c.onClassScan(caller, entry.getValue());
            }

            for (Class<?> type : entry.getValue()) {
                registerWorldFilters(type);
            }
        }

        oldComponentScans.addAll(componentScans);
        componentScans.clear();
    }

    /**
     * Registers cron executor for core's runner plugin.
     *
     * @param coreRunnerPlugin Core's runner plugin
     */
    public void registerCronExecutor(CoreRunnerPlugin coreRunnerPlugin) {
        coreRunnerPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(
                coreRunnerPlugin,
                () -> {
                    for (CronData cronListener : cronListeners) {
                        if (!cronListener.canRun())
                            continue;

                        cronListener.calculateNextRun();
                        ReflectionUtils.invoke(cronListener.method(), cronListener.component());
                        logger.info("Cron " + cronListener.component().getClass().getSimpleName() + ".\"" + cronListener.cron().value() + "\" done.");
                    }
                },
                0,
                30 * 20
        );
    }

    /**
     * Starts all components
     *
     * @param caller Plugin
     */
    public void startComponents(CorePlugin caller) {
        ComponentStaticDataInitializer.init();
        componentMap.values().stream()
                    .filter(componentData -> componentData.plugin() == caller && componentData.component() instanceof IComponent)
                    .forEach(componentData -> ((IComponent) componentData.component()).onStart());
    }

    /**
     * Checks configuration conditions for class.
     * This is used in case component's existence is conditioned.
     * Same could be used for database entity class.
     *
     * @param caller Plugin
     * @param clazz  type
     * @return True if configuration condition passed
     */
    public boolean checkConfiguration(CorePlugin caller, Class<?> clazz) {
        CheckConfiguration checkConfiguration = clazz.getAnnotation(CheckConfiguration.class);
        if (checkConfiguration == null)
            return true;

        String[] parts = checkConfiguration.value().split("=");
        String name = parts[0];
        String value = parts[1];

        Object configValue = caller.getConfig().get(name, null);
        if (configValue == null)
            return false;

        return configValue.toString().equals(value);
    }

    private void registerComponentAnnotations(CorePlugin caller, Object component) {
        if (component instanceof Listener listener)
            caller.getServer().getPluginManager().registerEvents(listener, caller);

        for (Method method : component.getClass().getDeclaredMethods()) {
            CommandListener commandListener = method.getAnnotation(CommandListener.class);
            if (commandListener != null)
                registerCommandListener(caller, component, method, commandListener);

            Cron cron = method.getAnnotation(Cron.class);
            if (cron != null)
                registerCronListener(caller, component, method, cron);

            ActionListener actionListener = method.getAnnotation(ActionListener.class);
            if (actionListener != null)
                registerActionListener(caller, component, method, actionListener);
        }
    }

    private void registerCommandListener(CorePlugin caller, Object component, Method method, CommandListener commandListener) {
        if (caller.getServer().getCommandMap().getCommand(commandListener.value()) != null) {
            logger.warn("Command handler for {} already exists.", commandListener.value());
            return;
        }

        if (method.getParameterCount() != 1 || method.getParameterTypes()[0] != CommandData.class)
            return;

        CheckPermission[] checkPermissions = new CheckPermission[0];
        CheckCondition[] checkConditions = new CheckCondition[0];
        CheckPermissions checkPermissionsAnnotation = method.getAnnotation(CheckPermissions.class);
        CheckConditions checkConditionsAnnotation = method.getAnnotation(CheckConditions.class);
        CheckPermission checkPermissionAnnotation = method.getAnnotation(CheckPermission.class);
        CheckCondition checkConditionAnnotation = method.getAnnotation(CheckCondition.class);

        if (checkPermissionsAnnotation != null) {
            checkPermissions = checkPermissionsAnnotation.value();
        } else if (checkPermissionAnnotation != null) {
            checkPermissions = new CheckPermission[] {checkPermissionAnnotation};
        }

        if (checkConditionsAnnotation != null) {
            checkConditions = checkConditionsAnnotation.value();
        } else if (checkConditionAnnotation != null) {
            checkConditions = new CheckCondition[] {checkConditionAnnotation};
        }

        method.setAccessible(true);
        final CheckPermission[] finalCheckPermissions = checkPermissions;
        final CheckCondition[] finalCheckConditions = checkConditions;
        caller.getServer().getCommandMap().register(commandListener.value(), new Command(commandListener.value()) {

            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel,
                                   @NotNull String[] args) {
                boolean canExecute = true;
                Object returnValue = null;
                for (CheckPermission checkPermission : finalCheckPermissions) {
                    String permName = checkPermission.value();
                    if (permName.equals(CheckPermission.OP)) {
                        canExecute = sender.isOp();
                    } else {
                        // TODO: Check for permission
                    }

                    if (!canExecute) {
                        returnValue = new PermissionErrorCommandResponse();
                        break;
                    }
                }
                if (canExecute) {
                    for (CheckCondition checkCondition : finalCheckConditions) {
                        String condition = checkCondition.value();
                        if (condition.equals(CheckCondition.SENDER_IS_PLAYER)) {
                            canExecute = sender instanceof Player;
                            returnValue = new ErrorCommandResponse("Command can be used only by player.");
                        } else if (condition.startsWith("args_is_")) {
                            int count = Integer.parseInt(condition.substring(8));
                            canExecute = args.length == count;
                            if (!canExecute)
                                returnValue = new ParameterCountErrorCommandResponse(count);
                        }

                        if (!canExecute) {
                            break;
                        }
                    }
                }

                CommandData commandData = new CommandData(sender, this, args);
                if (canExecute)
                    returnValue = ReflectionUtils.invoke(method, component, commandData);

                if (returnValue instanceof ICommandResponseResolvable response) {
                    sender.sendMessage(response.getComponent(commandData));
                    return true;
                }
                if (returnValue instanceof Boolean bool) {
                    return bool;
                }
                return true;
            }
        });
        logger.info("Command handler for {} has been registered.", commandListener.value());
    }

    private void registerCronListener(CorePlugin caller, Object component, Method method, Cron cron) {
        String expression = cron.value();
        CronDefinition cronDefinition = CronDefinitionBuilder
                .defineCron()
                .withSeconds().withValidRange(0, 59).and()
                .withMinutes().withValidRange(0, 59).and()
                .withHours().withValidRange(0, 23).and()
                .withDayOfMonth().withValidRange(1, 31).supportsL().supportsW().supportsLW().supportsQuestionMark()
                .and()
                .withMonth().withValidRange(1, 12).and()
                .withDayOfWeek().withValidRange(1, 7).withMondayDoWValue(2).supportsHash().supportsL()
                .supportsQuestionMark().and()
                .withYear().withValidRange(1970, 2099).withStrictRange().optional().and()
                .instance();
        CronParser parser = new CronParser(cronDefinition);
        com.cronutils.model.Cron cronObject;
        try {
            cronObject = parser.parse(expression);
        } catch (IllegalArgumentException e) {
            logger.warn("Cron " + component.getClass().getSimpleName() + ".\"" + expression + "\" is invalid", e);
            return;
        }
        logger.info("Cron " + component.getClass().getSimpleName() + ".\"" + expression + "\" has been registered.");
        CronData cronData = new CronData(cron, component, method, cronObject);
        cronData.calculateNextRun();
        cronListeners.add(cronData);
    }

    private void registerActionListener(CorePlugin caller, Object component, Method method, ActionListener actionListener) {
        String name = actionListener.value();
        List<ActionListenerData> actions = actionListeners.computeIfAbsent(name, s -> new ArrayList<>());
        actions.add(new ActionListenerData(component, actionListener, method));
        actions.sort((o1, o2) -> Integer.compare(o2.actionListener().priority(), o1.actionListener().priority()));
        logger.info("Registered action listener on " + component.getClass().getSimpleName() + ".\"" + name + "\".");
    }

    private void registerWorldFilters(Class<?> type) {
        ReflectionUtils.getAllClassFields(type).stream()
                       .filter(field -> Modifier.isStatic(field.getModifiers()) && String[].class.isAssignableFrom(field.getType()) &&
                                        field.getAnnotation(WorldFilter.class) != null)
                       .forEach(field -> {
                           WorldFilter worldFilter = field.getAnnotation(WorldFilter.class);
                           boolean exists = ComponentStaticDataInitializer.worldFilters.containsKey(worldFilter.name());
                           String[] worlds = (String[]) ReflectionUtils.getValueOpt(field, null).orElse(new String[0]);
                           ComponentStaticDataInitializer.worldFilters.computeIfAbsent(worldFilter.name(), s -> new ArrayList<>())
                                                                      .addAll(Arrays.asList(worlds));
                           logger.info("World filter was {} with worlds: {}", exists ? "modified" : "added", String.join(", ", worlds));
                       });
    }

    /**
     * Get the owner of component object
     *
     * @param component Component object
     * @return Component's plugin
     */
    public static Plugin getComponentOwner(Object component) {
        return componentMap.get(component.getClass()).plugin();
    }

    /**
     * Notifies all methods annotated with {@link ActionListener}.
     *
     * @param componentClass Component class
     * @param name           Action name
     * @param data           Action data
     */
    public static void notifyActionListeners(Class<?> componentClass, String name, Object[] data) {
        String actionName = componentMap.get(componentClass).plugin().getName() + "." + name;
        if (!actionListeners.containsKey(actionName))
            return;

        for (ActionListenerData actionListenerData : actionListeners.get(actionName)) {
            ReflectionUtils.invoke(actionListenerData.method(), actionListenerData.component(), data);
        }
    }

    /**
     * Gets component without checking its existence.
     *
     * @param clazz Component type
     * @param <T>   Component type generic
     * @return Component or null if component does not exist or wire type is unsupported
     */
    @Nullable
    public static <T> T getNullable(@NotNull Class<T> clazz) {
        Preconditions.checkNotNull(clazz);
        if (!componentMap.containsKey(clazz))
            return null;

        ComponentData data = componentMap.get(clazz);
        return data.annotation().wireType() == Component.WireType.All || data.annotation().wireType() == Component.WireType.ManualOnly
                ? (T) data.component()
                : null;
    }

    /**
     * Gets component with existence checking.
     * If no component exists or wire type is unsupported {@link NullPointerException} is thrown.
     *
     * @param clazz Component type
     * @param <T>   Component type generic
     * @return Component
     */
    public static <T> T get(@NotNull Class<T> clazz) {
        return Preconditions.checkNotNull(getNullable(clazz));
    }

    /**
     * Gets component without existence checking.
     * If no component exists or wire type is unsupported runnable is not invoked.
     *
     * @param clazz    Component type
     * @param runnable Runnable
     * @param <T>      Component type generic
     */
    public static <T> void getAndRun(@NotNull Class<T> clazz, Consumer<T> runnable) {
        T component = getNullable(clazz);
        if (component == null)
            return;

        runnable.accept(component);
    }
}
