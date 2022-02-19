/*
  User: Cloudy
  Date: 30/01/2022
  Time: 17:28
*/

package cz.cloudy.minecraft.core.scoreboard.logics;

import cz.cloudy.minecraft.core.scoreboard.ScoreboardLogic;

/**
 * @author Cloudy
 */
public abstract class ChangeBasedScoreboardLogic
        extends ScoreboardLogic {
    private long oldHash;

    /**
     * Default constructor.
     */
    public ChangeBasedScoreboardLogic() {
    }

    /**
     * Calculates current hash which is used for update notification.
     *
     * @return Current hash
     */
    public abstract long calculateHash();

    @Override
    public boolean checkUpdate() {
        long newHash = calculateHash();
        boolean update = newHash != oldHash;
        oldHash = newHash;
        return update;
    }
}
