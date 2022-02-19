/*
  User: Cloudy
  Date: 20/01/2022
  Time: 23:58
*/

package cz.cloudy.minecraft.core.componentsystem.types;

import com.cronutils.model.time.ExecutionTime;
import cz.cloudy.minecraft.core.componentsystem.annotations.Cron;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;

/**
 * @author Cloudy
 */
public class CronData {
    private final Cron                     cron;
    private final Object                   component;
    private final Method                   method;
    private final com.cronutils.model.Cron cronObject;

    private ZonedDateTime nextRun;

    /**
     * Default constructor
     *
     * @param cron       Cron
     * @param component  Component
     * @param method     Method
     * @param cronObject Cron object
     */
    public CronData(Cron cron, Object component, Method method, com.cronutils.model.Cron cronObject) {
        this.cron = cron;
        this.component = component;
        this.method = method;
        this.cronObject = cronObject;
    }

    /**
     * Getter for cron
     *
     * @return Cron
     */
    public Cron cron() {
        return cron;
    }

    /**
     * Getter for component
     *
     * @return Component
     */
    public Object component() {
        return component;
    }

    /**
     * Getter for method
     *
     * @return Method
     */
    public Method method() {
        return method;
    }

    /**
     * Getter for cron object
     *
     * @return Cron object
     */
    public com.cronutils.model.Cron cronObject() {
        return cronObject;
    }

    // ============================================

    /**
     * Calculates next run and stores it.
     */
    public void calculateNextRun() {
        ExecutionTime executionTime = ExecutionTime.forCron(cronObject);
        nextRun = executionTime.nextExecution(ZonedDateTime.now()).orElse(null);
    }

    /**
     * Checks if cron job can be invoked.
     * This means current date is compared with next run date.
     *
     * @return True if cron job can be invoked
     */
    public boolean canRun() {
        if (nextRun == null)
            return false;

        ZonedDateTime now = ZonedDateTime.now();
        return now.isEqual(nextRun) || now.isAfter(nextRun);
    }
}
