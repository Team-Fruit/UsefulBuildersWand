package net.teamfruit.usefulbuilderswand;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WandData {
	public static final String SETTING_EFFECT_RANGE = "setting.effect.range";

	public static final String FEATURE_META_SIZE = "feature.meta.size";
	public static final String FEATURE_META_MODE = "feature.meta.mode";
	public static final String FEATURE_META_DURABILITY = "feature.meta.durability.data";
	public static final String FEATURE_META_DURABILITY_MAX = "feature.meta.durability.max";
	public static final String FEATURE_META_DURABILITY_BLOCKCOUNT = "feature.meta.durability.blockcount";
	public static final String FEATURE_META_DURABILITY_UNBREAKABLE = "feature.meta.durability.unbreakable";
	public static final String FEATURE_META_COUNT_PLACE = "feature.meta.count.place";
	public static final String FEATURE_META_COUNT_USE = "feature.meta.count.use";
	public static final String FEATURE_META_PARTICLE_COLOR_R = "feature.meta.particle.color.r";
	public static final String FEATURE_META_PARTICLE_COLOR_G = "feature.meta.particle.color.g";
	public static final String FEATURE_META_PARTICLE_COLOR_B = "feature.meta.particle.color.b";
	public static final String FEATURE_META_PARTICLE_SHARE = "feature.meta.particle.share";
	public static final String FEATURE_META_OWNER = "feature.meta.owner.data";
	public static final String FEATURE_META_OWNER_ID = "feature.meta.owner.id";
	public static final String FEATURE_META_OWNER_NAME = "feature.meta.owner.name";

	private FileConfiguration cfg;

	public WandData() {
	}

	public void initConfig(final FileConfiguration cfg) {
		this.cfg = cfg;

		cfg.addDefault(SETTING_EFFECT_RANGE, 48);

		final Map<String, Object> it = Maps.newHashMap();

		it.put(FEATURE_META_SIZE, 9);
		it.put(FEATURE_META_MODE, false);
		it.put(FEATURE_META_DURABILITY, 27);
		it.put(FEATURE_META_DURABILITY_MAX, 27);
		it.put(FEATURE_META_DURABILITY_BLOCKCOUNT, false);
		it.put(FEATURE_META_DURABILITY_UNBREAKABLE, false);
		it.put(FEATURE_META_COUNT_PLACE, 0);
		it.put(FEATURE_META_COUNT_USE, 0);
		it.put(FEATURE_META_PARTICLE_COLOR_R, 255);
		it.put(FEATURE_META_PARTICLE_COLOR_G, 255);
		it.put(FEATURE_META_PARTICLE_COLOR_B, 255);
		it.put(FEATURE_META_PARTICLE_SHARE, true);
		it.put(FEATURE_META_OWNER, false);
		it.put(FEATURE_META_OWNER_ID, "");
		it.put(FEATURE_META_OWNER_NAME, "");

		it.put("custom.title.mode.if", "${"+FEATURE_META_MODE+"}");
		it.put("custom.title.mode.true", "┃");
		it.put("custom.title.mode.false", "━");
		it.put("custom.title.durability.if", "${"+FEATURE_META_DURABILITY_UNBREAKABLE+"}");
		it.put("custom.title.durability.true", "Infinity");
		it.put("custom.title.durability.false", "${"+FEATURE_META_DURABILITY+"}/${"+FEATURE_META_DURABILITY_MAX+"}");
		it.put("custom.lore.mode.if", "${"+FEATURE_META_MODE+"}");
		it.put("custom.lore.mode.true", "Vertical");
		it.put("custom.lore.mode.false", "Horizonal");
		it.put("custom.lore.blockcount.if", "${"+FEATURE_META_DURABILITY_BLOCKCOUNT+"}");
		it.put("custom.lore.blockcount.true", " blocks");
		it.put("custom.lore.blockcount.false", " times");
		it.put("custom.lore.durability.if", "${"+FEATURE_META_DURABILITY_UNBREAKABLE+"}");
		it.put("custom.lore.durability.true", "(Infinity)");
		it.put("custom.lore.durability.false", "${"+FEATURE_META_DURABILITY+"} of ${"+FEATURE_META_DURABILITY_MAX+"}${custom.lore.blockcount}");
		it.put("custom.lore.owner.if", "${"+FEATURE_META_OWNER+"}");
		it.put("custom.lore.owner.true", "§3 - Owner §7: ${"+FEATURE_META_OWNER_NAME+"}${"+FEATURE_META_OWNER_ID+"}");

		it.put("item.title", "§eBuilder's Wand §7x${"+FEATURE_META_SIZE+"} §7[${custom.title.mode}§7] (${custom.title.durability}§7)");
		it.put("item.lore", Lists.newArrayList(new String[] {
				"§3 - Mode §7: ${custom.lore.mode}",
				"§3 - Durability §7: ${custom.lore.durability}",
				"§3 - Size §7: ${"+FEATURE_META_SIZE+"}",
				"§3 - UseCount §7: ${"+FEATURE_META_COUNT_USE+"}",
				"§3 - PlaceCount §7: ${"+FEATURE_META_COUNT_PLACE+"}",
				"${custom.lore.owner}",
		}));

		cfg.addDefaults(it);
	}

	public FileConfiguration getConfig() {
		return this.cfg;
	}

	public String resolve(final String str) {
		final Pattern p = Pattern.compile("\\$\\{(.*?)\\}");
		final Matcher m = p.matcher(str);

		final StringBuffer sb = new StringBuffer();
		while (m.find()) {
			final String key = m.group();
			final String value = get(key);
			String res = null;
			c: {
				if (value==null) {
					final String valueif = get(key+".if");
					if (valueif!=null) {
						final String resif = resolve(valueif);
						final String valuetrue = get(key+(BooleanUtils.toBooleanObject(resif) ? ".true" : ".false"));
						if (valuetrue!=null)
							res = resolve(valueif);
						else
							res = "";
						break c;
					}
				} else {
					res = resolve(value);
					break c;
				}
			}
			if (res!=null)
				m.appendReplacement(sb, res);
			else
				m.appendReplacement(sb, "${"+key+"}");
		}
		m.appendTail(sb);

		return sb.toString();
	}

	public String get(final String path) {
		final Object key = getConfig().get(path);
		if (key instanceof String) {
			final String keystr = (String) key;
			if (!StringUtils.isEmpty(keystr))
				return keystr;
		}
		return null;
	}
}