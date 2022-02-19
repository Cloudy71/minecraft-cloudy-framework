/*
  User: Cloudy
  Date: 08/01/2022
  Time: 01:49
*/

package cz.cloudy.minecraft.core.database.queries;

import cz.cloudy.minecraft.core.database.Query;
import cz.cloudy.minecraft.core.database.enums.QueryType;

import java.sql.Statement;

/**
 * @author Cloudy
 */
public class RawDMLOrDDLQuery
        extends Query {
    private   String queryString;
    /**
     * Statement type.
     */
    protected int    statementType = Statement.NO_GENERATED_KEYS;

    /**
     * Default constructor.
     */
    public RawDMLOrDDLQuery() {
        super(QueryType.RawDMLOrDDL);
    }

    /**
     * Setter for query string.
     *
     * @param queryString Query string
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    /**
     * Getter for statement type.
     *
     * @return Statement type
     */
    public int getStatementType() {
        return statementType;
    }

    /**
     * Setter for statement type.
     *
     * @param statementType Statement type
     */
    public void setStatementType(int statementType) {
        this.statementType = statementType;
    }

    @Override
    public String provideQueryString() {
        return queryString;
    }
}
