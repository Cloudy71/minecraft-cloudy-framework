/*
  User: Cloudy
  Date: 08/01/2022
  Time: 01:05
*/

package cz.cloudy.minecraft.core.database;

import java.util.List;
import java.util.Map;

/**
 * @author Cloudy
 */
public abstract class QueryResult {
    /**
     * Returns row count of processed query.
     *
     * @return Row count
     */
    public abstract int getRowCount();

    /**
     * Results column names of processed query.
     *
     * @return Column names
     */
    public abstract List<String> getColumns();

    /**
     * Returns column un-assigned query data.
     *
     * @return Query data
     */
    public abstract List<List<Object>> getDataTable();

    /**
     * Returns column un-assigned query data by row index.
     *
     * @param rowIndex Row index
     * @return Data or null if non-existing row index
     */
    public abstract List<Object> getData(int rowIndex);

    /**
     * Returns column assigned query data by row index.
     *
     * @param rowIndex Row index
     * @return Data or null if non-existing row index
     */
    public abstract Map<String, Object> getDataMap(int rowIndex);

    /**
     * Returns column assigned query data.
     *
     * @return Query data
     */
    public abstract List<Map<String, Object>> getDataMapTable();
}
