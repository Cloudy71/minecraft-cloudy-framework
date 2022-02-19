/*
  User: Cloudy
  Date: 07/01/2022
  Time: 21:26
*/

package cz.cloudy.minecraft.core.database.enums;

import cz.cloudy.minecraft.core.database.interfaces.IDatabaseProcessor;
import cz.cloudy.minecraft.core.database.processors.MysqlDatabaseProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cloudy
 */
public class DatabaseEngine {
    private static final Map<String, DatabaseEngine> engineMap = new HashMap<>();

    /**
     * MySQL database engine.
     */
    public static final DatabaseEngine MySQL = new DatabaseEngine("mysql", "jdbc:mysql://%host:%port/%db?autoReconnect=true", MysqlDatabaseProcessor.class);

    private final String                                 name;
    private final String                                 expression;
    private final Class<? extends IDatabaseProcessor<?>> engineClass;

    /**
     * Default constructor.
     *
     * @param name        Name
     * @param resolver    Resolver
     * @param engineClass Engine type
     */
    protected DatabaseEngine(String name, String resolver, Class<? extends IDatabaseProcessor<?>> engineClass) {
        this.name = name;
        this.expression = resolver;
        this.engineClass = engineClass;
        engineMap.put(name, this);
    }

    /**
     * Getter for name.
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for expression.
     *
     * @return Expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Getter for engine type.
     *
     * @return Engine type
     */
    public Class<? extends IDatabaseProcessor<?>> getEngineClass() {
        return engineClass;
    }

    /**
     * Resolves url by database data.
     *
     * @param host Host
     * @param port Port
     * @param db   Database
     * @return Resolved url
     */
    public String resolveUrl(String host, int port, String db) {
        return getExpression()
                .replaceAll("%host", host)
                .replaceAll("%port", Integer.toString(port))
                .replaceAll("%db", db);
    }

    /**
     * Resolves engine by its name.
     *
     * @param name Name
     * @return Engine by name or null
     */
    public static DatabaseEngine resolveEngine(String name) {
        return engineMap.get(name);
    }
}
