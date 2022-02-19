/*
  User: Cloudy
  Date: 07/01/2022
  Time: 03:38
*/

package cz.cloudy.minecraft.core.database;

import cz.cloudy.minecraft.core.database.enums.QueryType;

/**
 * @author Cloudy
 */
public abstract class Query {
    /**
     * Query type.
     */
    protected QueryType queryType;
    /**
     * Lazy fetch.
     */
    protected boolean   lazyFetch = true;

    /**
     * Creates new query builder instance.
     *
     * @return Query builder
     */
    public static QueryBuilder builder() {
        return new QueryBuilder();
    }

    /**
     * Default constructor.
     *
     * @param queryType Query type
     */
    protected Query(QueryType queryType) {
        this.queryType = queryType;
    }

    /**
     * Creates query string.
     *
     * @return Query string
     */
    public abstract String provideQueryString();

    /**
     * Getter for query type.
     *
     * @return Query type
     */
    public QueryType getQueryType() {
        return queryType;
    }

    /**
     * If query is lazy fetched.
     *
     * @return Lazy fetch flag
     */
    public boolean isLazyFetch() {
        return lazyFetch;
    }
}
