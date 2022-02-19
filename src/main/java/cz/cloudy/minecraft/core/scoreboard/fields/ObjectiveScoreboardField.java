/*
  User: Cloudy
  Date: 30/01/2022
  Time: 14:58
*/

package cz.cloudy.minecraft.core.scoreboard.fields;

import cz.cloudy.minecraft.core.scoreboard.ScoreboardField;
import cz.cloudy.minecraft.core.scoreboard.ScoreboardObject;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

/**
 * @author Cloudy
 */
public class ObjectiveScoreboardField
        extends ScoreboardField {
    /**
     * Name.
     */
    protected String    name;
    /**
     * Display text.
     */
    protected String    displayText;
    /**
     * Objective.
     */
    protected Objective objective;

    /**
     * Default constructor.
     *
     * @param name        Name
     * @param displayText Display text
     */
    public ObjectiveScoreboardField(String name, String displayText) {
        this.name = name;
        this.displayText = displayText;
    }

    @Override
    protected void createField(ScoreboardObject scoreboardObject) {
//        objective = getScoreboard(scoreboardObject).registerNewObjective(name, "dummy",
//                                                                         Component.text(scoreboardObject.parse(
//                                                                                 displayText)/*, Style.style(TextDecoration.OBFUSCATED)*/));
        objective = getScoreboard(scoreboardObject).registerNewObjective(name, "dummy",
                                                                         scoreboardObject.transform(displayText));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @Override
    protected void updateField(ScoreboardObject scoreboardObject) {
//        objective.displayName(Component.text(scoreboardObject.parse(displayText)/*, Style.style(TextDecoration.OBFUSCATED)*/));
        objective.setDisplayName(scoreboardObject.transform(displayText)/*, Style.style(TextDecoration.OBFUSCATED)*/);
    }
}
