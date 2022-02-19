/*
  User: Cloudy
  Date: 30/01/2022
  Time: 14:43
*/

package cz.cloudy.minecraft.core.scoreboard;

import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

/**
 * @author Cloudy
 */
public abstract class ScoreboardField {
    /**
     * Created.
     */
    protected boolean created;
    /**
     * Index.
     */
    protected int     index;

    /**
     * Creates field and sets scoreboard field as created.
     *
     * @param scoreboardObject Scoreboard object
     */
    protected void create(ScoreboardObject scoreboardObject) {
        if (created)
            return;
        createField(scoreboardObject);
        created = true;
    }

    /**
     * Creates field.
     *
     * @param scoreboardObject Scoreboard object
     */
    protected abstract void createField(ScoreboardObject scoreboardObject);

    /**
     * Updates field.
     *
     * @param scoreboardObject Scoreboard object
     */
    protected abstract void updateField(ScoreboardObject scoreboardObject);

    /**
     * Getter for scoreboard instance.
     *
     * @param scoreboardObject Scoreboard object
     * @return Scoreboard instance
     */
    protected Scoreboard getScoreboard(ScoreboardObject scoreboardObject) {
        return scoreboardObject.scoreboard;
    }

    /**
     * Gets fields of scoreboard object.
     *
     * @param scoreboardObject Scoreboard object
     * @return Field list
     */
    protected List<ScoreboardField> getFields(ScoreboardObject scoreboardObject) {
        return scoreboardObject.fields;
    }

    /**
     * If field is created.
     *
     * @return True if is created
     */
    public boolean isCreated() {
        return created;
    }

    /**
     * Gets field's index in field list.
     *
     * @return Index
     */
    public int getIndex() {
        return index;
    }
}
