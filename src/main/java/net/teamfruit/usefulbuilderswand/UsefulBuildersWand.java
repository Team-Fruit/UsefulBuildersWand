package net.teamfruit.usefulbuilderswand;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreDataFormat;

public class UsefulBuildersWand extends JavaPlugin {

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		return false;
	}

	@Override
	public void onDisable() {

	}

	private WandData data = new WandData();

	@Override
	public void onLoad() {
		final FileConfiguration cfg = getConfig();
		this.data.initConfig(cfg);
		saveConfig();
	}

	@Override
	public void onEnable() {
		final WandListener listener = new WandListener(this, this.data);
		getServer().getPluginManager().registerEvents(listener, this);
		getCommand("usefulbuilderswand").setExecutor(listener);
	}

	public static class WandData {
		public static final String META_PREFIX = "itemdata.prefix";
		public static final String META_VALUE_PREFIX = "itemdata.value.prefix";
		public static final String META_VALUE_SUFFIX = "itemdata.value.suffix";
		public static final String META_VALUE_FORMAT = "itemdata.value.format";
		public static final String FEATURE_DATA_SIZE = "feature.data.size";
		public static final String FEATURE_DATA_VERTICALMODE = "§2§f§c§r";
		public static final String FEATURE_DATA_EXP = "feature.data.exp";
		public static final String FEATURE_DATA_EXP_NEXT = "feature.data.exp.next";
		public static final String FEATURE_DATA_DURABILITY = "feature.data.durability";
		public static final String FEATURE_DATA_DURABILITY_MAX = "feature.data.durability.max";
		// public static final String FEATURE_DATA_PARTICLE = "feature.data.particle";
		public static final String FEATURE_DISPLAY_INFINITY = "feature.display.infinity";
		public static final String FEATURE_DISPLAY_LEVEL = "feature.display.level";
		// public static final String FEATURE_DISPLAY_PARTICLE = "feature.display.particle";

		private FileConfiguration cfg;

		public void initConfig(final FileConfiguration cfg) {
			this.cfg = cfg;

			cfg.addDefault(META_PREFIX, "§3§e§8§r");
			cfg.addDefault(META_VALUE_PREFIX, "§5§2§c§r");
			cfg.addDefault(META_VALUE_SUFFIX, "§a§6§3§r");
			cfg.addDefault(META_VALUE_FORMAT, Lists.newArrayList(
					"§eBuilder's Wand (${B:§1§d§3§r=Vertical:Horizonal})",
					"§e - Durability : ${I:§5§b§4§r=0} of ${I:§7§f§c§r=0} ${B:§3§a§c§r=(Infinity):}",
					"§e - Size : ${I:§2§f§c§r=0}",
					"§e - Level : ${I:§2§a§b§r=0}",
					"§e - Exp : ${I:§e§9§8§r=0} of ${I:§d§3§8§r=0}"));
			//		"§e - Particle   : ${I:§3§4§b§r=§}${S:§4§4§f§r}",
			cfg.addDefault(FEATURE_DATA_SIZE, "§2§f§c§r");
			cfg.addDefault(FEATURE_DATA_VERTICALMODE, "§1§d§3§r");
			cfg.addDefault(FEATURE_DATA_EXP, "§e§9§8§r");
			cfg.addDefault(FEATURE_DATA_EXP_NEXT, "§d§3§8§r");
			cfg.addDefault(FEATURE_DATA_DURABILITY, "§5§b§4§r");
			cfg.addDefault(FEATURE_DATA_DURABILITY_MAX, "§7§f§c§r");
			// cfg.addDefault(FEATURE_DATA_PARTICLE, "§3§4§b§r");
			cfg.addDefault(FEATURE_DISPLAY_INFINITY, "§3§a§c§r");
			cfg.addDefault(FEATURE_DISPLAY_LEVEL, "§2§a§b§r");
			// cfg.addDefault(FEATURE_DISPLAY_PARTICLE, "§4§4§f§r");
		}

		public FileConfiguration getConfig() {
			return this.cfg;
		}

		private ItemLoreDataFormat cachevalue;

		public ItemLoreDataFormat getFormat() {
			if (this.cachevalue==null)
				this.cachevalue = new ItemLoreDataFormat(key(META_PREFIX), key(META_VALUE_PREFIX), key(META_VALUE_SUFFIX), getConfig().getStringList(META_VALUE_FORMAT));
			return this.cachevalue;
		}

		public String key(final String path) {
			final String key = getConfig().getString(path);
			if (!StringUtils.isEmpty(key))
				return key;
			return null;
		}
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
		return null;
	}
}
