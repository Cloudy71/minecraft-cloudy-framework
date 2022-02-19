/*
  User: Cloudy
  Date: 08/01/2022
  Time: 19:38
*/

package cz.cloudy.minecraft.core.database.fetch_data;

import cz.cloudy.minecraft.core.database.interfaces.IFetchData;

import java.util.List;
import java.util.Map;

/**
 * @author Cloudy
 */
public record MysqlFetchData(String selectQuery, String fromQuery, List<String> joinQuery,
                             Map<String, String> translationMap)
        implements IFetchData {
    /**
     * Default constructor.
     *
     * @param selectQuery    Select query
     * @param fromQuery      From query
     * @param joinQuery      Join query
     * @param translationMap Translation map
     */
    public MysqlFetchData {
    }
}
