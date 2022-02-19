/*
  User: Cloudy
  Date: 30/01/2022
  Time: 17:26
*/

package cz.cloudy.minecraft.core.scoreboard;

import java.util.Collections;
import java.util.List;

/**
 * @author Cloudy
 */
public abstract class ScoreboardLogic {
    /**
     * Scoreboard Object
     */
    protected ScoreboardObject scoreboardObject;

    /**
     * Checks if there is pending update.
     *
     * @return True if there is pending update
     */
    public abstract boolean checkUpdate();

    /**
     * Data list used for replacing scoreboard text placeholders into values.
     *
     * @return List of data
     */
    public List<String> getDataList() {
        return Collections.emptyList();
    }
}
