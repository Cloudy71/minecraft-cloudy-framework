/*
  User: Cloudy
  Date: 08/01/2022
  Time: 01:06
*/

package cz.cloudy.minecraft.core.database.queries;

import cz.cloudy.minecraft.core.database.Query;
import cz.cloudy.minecraft.core.database.enums.QueryType;

/**
 * @author Cloudy
 */
public class RawDQLQuery
        extends Query {

    private String queryString;

    /**
     * Default constructor.
     */
    public RawDQLQuery() {
        super(QueryType.RawDQL);
    }

    /**
     * Setter for query string.
     *
     * @param queryString Query string
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public String provideQueryString() {
        return queryString;
    }
}
