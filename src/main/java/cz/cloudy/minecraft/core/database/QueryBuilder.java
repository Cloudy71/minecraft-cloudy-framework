/*
  User: Cloudy
  Date: 07/01/2022
  Time: 03:42
*/

package cz.cloudy.minecraft.core.database;

import com.google.common.base.Preconditions;
import cz.cloudy.minecraft.core.componentsystem.ComponentLoader;
import cz.cloudy.minecraft.core.database.enums.QueryType;
import cz.cloudy.minecraft.core.database.queries.RawDMLOrDDLQuery;
import cz.cloudy.minecraft.core.database.queries.RawDQLQuery;
import cz.cloudy.minecraft.core.database.queries.SelectQuery;
import cz.cloudy.minecraft.core.database.types.FieldScan;

import java.util.List;

/**
 * @author Cloudy
 */
public class QueryBuilder {
    private Query query;

    /**
     * Default constructor.
     */
    protected QueryBuilder() {
    }

    private void setQueryType(QueryType queryType) {
        Preconditions.checkState(query == null || query.queryType == queryType, "Query is already defined");

        if (query != null)
            return;

        switch (queryType) {
            case RawDQL -> query = new RawDQLQuery();
            case RawDMLOrDDL -> query = new RawDMLOrDDLQuery();
            case Select -> query = new SelectQuery();
        }
    }

    private String getFieldNames(Class<? extends DatabaseEntity> clazz, String fields) {
        String fieldPrefix = clazz.getSimpleName();
        List<FieldScan> list = ComponentLoader.get(DatabaseEntityMapper.class).getFieldScansForEntityType(clazz);
        if (fields != null) {
            for (FieldScan fieldScan : list) {
                fields = fields.replaceAll("\\$" + fieldScan.column().value(), fieldPrefix + "." + fieldScan.column().value());
            }
        } else {
            StringBuilder builder = new StringBuilder();
            for (FieldScan fieldScan : list) {
                builder.append(builder.length() > 0 ? "," : "").append(fieldPrefix).append(".").append(fieldScan.column().value());
            }
            fields = builder.toString();
        }

        return fields;
    }

    /**
     * Creates select query.
     *
     * @param clazz  Entity type
     * @param fields Fields to select
     * @return Self
     * @deprecated Not implemented yet
     */
    @Deprecated
    public QueryBuilder select(Class<? extends DatabaseEntity> clazz, String fields) {
        setQueryType(QueryType.Select);
        ComponentLoader.get(DatabaseEntityMapper.class).mapEntityClass(clazz);
        fields = getFieldNames(clazz, fields);
        SelectQuery selectQuery = (SelectQuery) query;
        selectQuery.getSelectBuilder().append(selectQuery.getSelectBuilder().length() > 0 ? "," : "").append(fields);
        return this;
    }

    /**
     * Creates select query.
     *
     * @param clazz Entity type
     * @return Self
     * @deprecated Not implemented yet
     */
    @Deprecated
    public QueryBuilder select(Class<? extends DatabaseEntity> clazz) {
        return select(clazz, null);
    }

    /**
     * Adds where condition to the query.
     *
     * @param entityName Entity name
     * @param condition  Condition
     * @return Self
     * @deprecated Not implemented yet
     */
    @Deprecated
    public QueryBuilder where(String entityName, String condition) {
        return this;
    }

    /**
     * Adds where condition to the query.
     *
     * @param clazz     Entity type
     * @param condition Condition
     * @return Self
     * @deprecated Not implemented yet
     */
    @Deprecated
    public QueryBuilder where(Class<? extends DatabaseEntity> clazz, String condition) {
        return where(clazz.getSimpleName(), condition);
    }

    /**
     * Adds join to the query.
     *
     * @param clazz     Entity type
     * @param condition Condition
     * @return Self
     * @deprecated Not implemented yet
     */
    @Deprecated
    public QueryBuilder join(Class<? extends DatabaseEntity> clazz, String condition) {

        return this;
    }

    /**
     * Creates raw DQL query.
     *
     * @param queryString Query string
     * @return Self
     */
    public QueryBuilder rawDQL(String queryString) {
        setQueryType(QueryType.RawDQL);
        ((RawDQLQuery) query).setQueryString(queryString);
        return this;
    }

    /**
     * Creates raw DML or DDL query.
     *
     * @param queryString Query string
     * @return Self
     */
    public QueryBuilder rawDMLOrDDL(String queryString) {
        setQueryType(QueryType.RawDMLOrDDL);
        ((RawDMLOrDDLQuery) query).setQueryString(queryString);
        return this;
    }

    /**
     * Sets statement type of the query.
     *
     * @param statementType Statement type
     * @return Self
     */
    public QueryBuilder statementType(int statementType) {
        if (!(query instanceof RawDMLOrDDLQuery rawDMLOrDDLQuery))
            return this;
        rawDMLOrDDLQuery.setStatementType(statementType);
        return this;
    }

    /**
     * Sets query to be full fetch.
     *
     * @return Self
     * @deprecated Not implemented yet
     */
    @Deprecated
    public QueryBuilder fullFetch() {
        query.lazyFetch = false;
        return this;
    }

    /**
     * Builds query.
     *
     * @return Built query
     */
    public Query build() {
        return query;
    }

}
