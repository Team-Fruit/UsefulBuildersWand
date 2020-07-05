package net.teamfruit.ubw;

import com.google.common.collect.Maps;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class WandData {
    public static final WandData INSTANCE = new WandData();

    public static final String SETTING_LANG = "setting.lang";
    public static final String SETTING_EFFECT_RANGE = "setting.effect.range";
    public static final String SETTING_MAX_BLOCKS = "setting.block.max";

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