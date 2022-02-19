/*
  User: Cloudy
  Date: 30/01/2022
  Time: 14:31
*/

package cz.cloudy.minecraft.core.scoreboard;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Cloudy
 */
public class ScoreboardObject {
    /**
     * Name.
     */
    protected String                name;
    /**
     * Logic.
     */
    protected ScoreboardLogic       logic;
    /**
     * Player.
     */
    protected Player                player;
    /**
     * Scoreboard.
     */
    protected Scoreboard            scoreboard;
    /**
     * Fields.
     */
    protected List<ScoreboardField> fields;
    /**
     * Created.
     */
    protected boolean               created;

    private Pattern      parsePattern;
    private boolean      firstUpdate    = true;
    private List<String> cachedDataList = null;

    /**
     * Default constructor.
     *
     * @param name  Name
     * @param logic Logic
     */
    public ScoreboardObject(String name, ScoreboardLogic logic) {
        this.parsePattern = Pattern.compile("\\{(\\d+)}");
        this.fields = new ArrayList<>();
        this.name = name;
        this.logic = logic;

        if (logic != null) {
            logic.checkUpdate();
            cachedDataList = logic.getDataList();
        }
    }

    /**
     * Default constructor.
     *
     * @param logic Logic
     */
    public ScoreboardObject(ScoreboardLogic logic) {
        this(null, logic);
    }

    /**
     * Default constructor.
     *
     * @param name Name
     */
    public ScoreboardObject(String name) {
        this(name, null);
    }

    /**
     * Default constructor.
     */
    public ScoreboardObject() {
        this(null, null);
    }

    /**
     * Creates new scoreboard for the player.
     *
     * @param player Player
     */
    protected void create(Player player) {
        Preconditions.checkState(scoreboard == null, "Scoreboard is already created");
        if (logic != null)
            logic.scoreboardObject = this;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        createFields();
        player.setScoreboard(scoreboard);
        created = true;
    }

    /**
     * Updates current scoreboard.
     */
    public void update() {
        if (logic == null || !logic.checkUpdate())
            return;

        cachedDataList = logic.getDataList();
        updateFields();
    }

    /**
     * Creates fields.
     */
    protected void createFields() {
        Preconditions.checkNotNull(scoreboard, "Scoreboard is not created");
        for (ScoreboardField field : fields) {
            field.create(this);
        }
    }

    /**
     * Updates fields.
     */
    protected void updateFields() {
        for (ScoreboardField field : fields) {
            field.updateField(this);
        }
    }

    /**
     * Adds field to the scoreboard.
     *
     * @param field Field
     * @return Self
     */
    public ScoreboardObject add(ScoreboardField field) {
        field.index = fields.size();
        fields.add(field);
        if (isCreated())
            update();
        return this;
    }

    /**
     * Transforms text and inserts data from data list.
     *
     * @param text Text
     * @return Transformed text
     */
    public String transform(String text) {
        Matcher matcher = parsePattern.matcher(text);
        while (matcher.find()) {
            int num = Integer.parseInt(matcher.group(1));
            text = text.substring(0, matcher.start()) +
                   cachedDataList.get(num) +
                   text.substring(matcher.end());
        }
        return text;
    }

//    protected abstract Scoreboard createScoreboard();
//
//    protected abstract void updateScoreboard();

    /**
     * Is scoreboard created flag.
     *
     * @return Created flag
     */
    public boolean isCreated() {
        return created;
    }
}
