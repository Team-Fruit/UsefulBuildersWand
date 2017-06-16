package net.teamfruit.usefulbuilderswand;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreDataFormat;

public class WandData {
	public static final String META_PREFIX = "itemdata.prefix";
	public static final String META_VALUE_PREFIX = "itemdata.value.prefix";
	public static final String META_VALUE_SUFFIX = "itemdata.value.suffix";
	public static final String META_FORMAT = "itemdata.format";

	public static final String FEATURE = "feature";
	public static final String FEATURE_META_SIZE = "feature.meta.size";
	public static final String FEATURE_META_VERTICALMODE = "feature.meta.verticalmode";
	// public static final String FEATURE_META_EXP = "feature.meta.exp.data";
	// public static final String FEATURE_META_EXP_MAX = "feature.meta.exp.max";
	public static final String FEATURE_META_DURABILITY = "feature.meta.durability.data";
	public static final String FEATURE_META_DURABILITY_MAX = "feature.meta.durability.max";
	public static final String FEATURE_META_COUNT_PLACE = "feature.meta.count.place";
	public static final String FEATURE_META_COUNT_USE = "feature.meta.count.use";
	// public static final String FEATURE_META_PARTICLE = "feature.meta.particle";
	public static final String FEATURE_META_PARTICLE_COLOR_R = "feature.meta.particle.color.r";
	public static final String FEATURE_META_PARTICLE_COLOR_G = "feature.meta.particle.color.g";
	public static final String FEATURE_META_PARTICLE_COLOR_B = "feature.meta.particle.color.b";
	public static final String FEATURE_DISPLAY_UNBREAKABLE = "feature.display.unbreakable";
	// public static final String FEATURE_DISPLAY_LEVEL = "feature.display.level";
	// public static final String FEATURE_DISPLAY_PARTICLE = "feature.display.particle";

	private FileConfiguration cfg;

	public WandData() {
	}

	public void initConfig(final FileConfiguration cfg) {
		this.cfg = cfg;

		cfg.addDefault(META_PREFIX, "§3§e§8§r");
		cfg.addDefault(META_VALUE_PREFIX, "§5§2§c§r");
		cfg.addDefault(META_VALUE_SUFFIX, "§a§6§3§r");

		final Map<String, Object> features = Maps.newHashMap();
		features.put(FEATURE_META_SIZE, "§2§f§c§r");
		features.put(FEATURE_META_VERTICALMODE, "§1§d§3§r");
		// features.put(FEATURE_META_EXP, "§e§9§8§r");
		// features.put(FEATURE_META_EXP_MAX, "§d§3§8§r");
		features.put(FEATURE_META_DURABILITY, "§5§b§4§r");
		features.put(FEATURE_META_DURABILITY_MAX, "§7§f§c§r");
		features.put(FEATURE_META_COUNT_PLACE, "§6§8§e§r");
		features.put(FEATURE_META_COUNT_USE, "§2§7§b§r");
		// features.put(FEATURE_META_PARTICLE, "§3§4§b§r");
		features.put(FEATURE_META_PARTICLE_COLOR_R, "§4§3§4§r");
		features.put(FEATURE_META_PARTICLE_COLOR_G, "§c§a§d§r");
		features.put(FEATURE_META_PARTICLE_COLOR_B, "§f§8§e§r");
		features.put(FEATURE_DISPLAY_UNBREAKABLE, "§3§a§c§r");
		// features.put(FEATURE_DISPLAY_LEVEL, "§2§a§b§r");
		// features.put(FEATURE_DISPLAY_PARTICLE, "§4§4§f§r");

		final List<String> format = Lists.newArrayList(new String[] {
				"§eBuilder's Wand §7x${i:"+ft(FEATURE_META_SIZE)+"=0} §7[${b:"+ft(FEATURE_META_VERTICALMODE)+"=┃:━}§7](${i:"+ft(FEATURE_META_DURABILITY)+"=0}/${i:"+ft(FEATURE_META_DURABILITY_MAX)+"=0}${b:"+ft(FEATURE_DISPLAY_UNBREAKABLE)+"= (Infinity):}§7)",
				"§3 - Mode §7: ${B:"+ft(FEATURE_META_VERTICALMODE)+"=Vertical:Horizonal}",
				"§3 - Durability §7: ${I:"+ft(FEATURE_META_DURABILITY)+"=0} of ${I:"+ft(FEATURE_META_DURABILITY_MAX)+"=0} ${B:"+ft(FEATURE_DISPLAY_UNBREAKABLE)+"=(Infinity):}",
				"§3 - Size §7: ${I:"+ft(FEATURE_META_SIZE)+"=0}${I:"+ft(FEATURE_META_PARTICLE_COLOR_R)+"=§255}${I:"+ft(FEATURE_META_PARTICLE_COLOR_G)+"=§255}${I:"+ft(FEATURE_META_PARTICLE_COLOR_B)+"=§255}",
				"§3 - UseCount §7: ${I:"+ft(FEATURE_META_COUNT_USE)+"=0}",
				"§3 - PlaceCount §7: ${I:"+ft(FEATURE_META_COUNT_PLACE)+"=0}",
				// "§3 - Level §7: ${I:"+ft(FEATURE_DISPLAY_LEVEL)+"=0}",
				// "§e - Exp : ${I:"+ft(FEATURE_META_EXP+")=0} of ${I:"+ft(FEATURE_META_EXP_MAX)+"=0}",
				// "§e - Particle   : ${I:"+ft(FEATURE_META_PARTICLE)+"=§}${S:"+ft(FEATURE_DISPLAY_PARTICLE)+"}",
		});

		cfg.addDefaults(features);

		cfg.addDefault(META_FORMAT, format);
	}

	private String ft(final String feature) {
		return StringUtils.substringAfter(feature, FEATURE+".");
	}

	public FileConfiguration getConfig() {
		return this.cfg;
	}

	private ItemLoreDataFormat cachevalue;

	public ItemLoreDataFormat getFormat() {
		if (this.cachevalue==null) {
			final FileConfiguration config = getConfig();
			final List<String> format = config.getStringList(META_FORMAT);
			final ConfigurationSection featuresec = config.getConfigurationSection(FEATURE);
			final Map<String, Object> features = featuresec.getValues(true);
			for (final ListIterator<String> itr = format.listIterator(); itr.hasNext();) {
				String line = itr.next();
				for (final Entry<String, Object> entry : features.entrySet()) {
					final String key = entry.getKey();
					final Object value = entry.getValue();
					if (value instanceof String)
						line = StringUtils.replace(line, key, (String) value);
				}
				itr.set(line);
			}
			this.cachevalue = new ItemLoreDataFormat(key(META_PREFIX), key(META_VALUE_PREFIX), key(META_VALUE_SUFFIX), format);
		}
		return this.cachevalue;
	}

	public String key(final String path) {
		final Object key = getConfig().get(path);
		if (key instanceof String) {
			final String keystr = (String) key;
			if (!StringUtils.isEmpty(keystr))
				return keystr;
		}
		return null;
	}

	public String keyData(final String path) {
		final String key = key(path);
		if (key==null)
			return key(path+".data");
		return key;
	}
}