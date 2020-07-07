package net.teamfruit.ubw;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;

public class WandData {
    public static final WandData INSTANCE = new WandData();

    public static final String SETTING_LANG = "setting.lang";
    public static final String SETTING_EFFECT_RANGE = "setting.effect.range";
    public static final String SETTING_MAX_BLOCKS = "setting.block.max";

    public static final String PERMISSION_WAND_USE = "wand.use";
    public static final String PERMISSION_WAND_GRANT = "wand.grant";

    public static final String SCOREBOARD_WAND_SIZE = "wand_size";
    public static final String SCOREBOARD_WAND_VERTICAL = "wand_vertical";
    public static final String SCOREBOARD_WAND_EFFECT_RADIUS = "wand_effect_radius";
    public static final String SCOREBOARD_WAND_EFFECT_COLOR = "wand_effect_color";

    public final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

    public Objective getOrNewObjective(String name, String type, String displayName) {
        Objective objective = board.getObjective(name);
        if (objective == null)
            objective = board.registerNewObjective(name, type, displayName);
        return objective;
    }

    public int getScoreOrDefault(Objective objective, Player player, int def) {
        Score score = objective.getScore(player);
        if (score.isScoreSet())
            return score.getScore();
        return def;
    }

    private static final Map<String, Object> configInit = Maps.newHashMap();

    static {
        configInit.put(SETTING_LANG, "en_US.lang");
        configInit.put(SETTING_EFFECT_RANGE, 48);
        configInit.put(SETTING_MAX_BLOCKS, 64*64);
    }

    @Deprecated
    public static Map<String, Object> getConfigInit() {
        return configInit;
    }

    public static Object getInitDefault(final String path) {
        return configInit.get(path);
    }

    private FileConfiguration cfg;

    private WandData() {
    }

    public void initConfig(final FileConfiguration cfg) {
        this.cfg = cfg;

        cfg.addDefaults(configInit);
    }

    public FileConfiguration getConfig() {
        if (this.cfg == null)
            throw new IllegalStateException("WandData config data is not initialized. call after initialized.");
        return this.cfg;
    }
}