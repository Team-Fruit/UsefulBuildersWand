package net.teamfruit.usefulbuilderswand;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi.NBTCompound;
import net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi.NBTItem;
import net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi.NBTType;

public class WandData {
	public static final String SETTING_EFFECT_RANGE = "setting.effect.range";

	public static final String USEFUL_BUILDERS_WAND_NBT = "ubwand";

	public static final String FEATURE_META = "feature.meta";

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

	public static final String ITEM_TITLE = "item.title";
	public static final String ITEM_LORE = "item.lore";

	public static final Map<String, Object> it = Maps.newHashMap();
	static {
		it.put(SETTING_EFFECT_RANGE, 48);

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
		it.put("custom.lore.owner.true", "§3 - Owner §7: ${"+FEATURE_META_OWNER_NAME+"}");

		it.put(ITEM_TITLE, "§eBuilder's Wand §7x${"+FEATURE_META_SIZE+"} [${custom.title.mode}] (${custom.title.durability})");
		it.put(ITEM_LORE, Lists.newArrayList(new String[] {
				"§3 - Mode §7: ${custom.lore.mode}",
				"§3 - Durability §7: ${custom.lore.durability}",
				"§3 - Size §7: ${"+FEATURE_META_SIZE+"}",
				"§3 - UseCount §7: ${"+FEATURE_META_COUNT_USE+"}",
				"§3 - PlaceCount §7: ${"+FEATURE_META_COUNT_PLACE+"}",
				"${custom.lore.owner}",
		}));
	}

	private FileConfiguration cfg;

	public WandData() {
	}

	public void initConfig(final FileConfiguration cfg) {
		this.cfg = cfg;

		cfg.addDefaults(it);
	}

	public FileConfiguration getConfig() {
		return this.cfg;
	}

	public void updateItem(final ItemStack item, final NBTCompound nbt) {
		final ItemMeta meta = item.getItemMeta();
		final FileConfiguration cfg = getConfig();
		final AbstractData data = new AbstractData.ItemData(nbt);
		final AbstractSettings settings = new AbstractSettings.ConfigSettings(cfg);

		Object itemtitle = cfg.get(ITEM_TITLE);
		if (itemtitle==null)
			itemtitle = it.get(ITEM_TITLE);
		if (itemtitle instanceof String)
			meta.setDisplayName(settings.resolve(data, (String) itemtitle));

		Object itemlore = cfg.get(ITEM_LORE);
		if (itemlore==null)
			itemlore = it.get(ITEM_LORE);
		if (itemlore instanceof List<?>) {
			final List<String> newlore = Lists.newArrayList();
			for (final Object obj : (List<?>) itemlore)
				if (obj instanceof String)
					newlore.add(settings.resolve(data, (String) obj));
			meta.setLore(newlore);
		}
	}

	public NBTCompound getNBT(final ItemStack itemStack) {
		return new NBTItem(itemStack).getCompound(USEFUL_BUILDERS_WAND_NBT);
	}

	public static abstract class AbstractSettings {
		protected String resolve(final Deque<String> stack, final AbstractData data, final String str) {
			if (stack.contains(str))
				return null;
			stack.push(str);

			final Pattern p = Pattern.compile("\\$\\{(.*?)\\}");
			final Matcher m = p.matcher(str);

			final StringBuffer sb = new StringBuffer();
			while (m.find()) {
				final String wrappedkey = m.group();
				final String key = StringUtils.substringBetween(wrappedkey, "${", "}");
				final String value = getKeyFromDataOrSetting(data, key);
				String res = null;
				c: {
					if (value==null) {
						final String valueif = getKeyFromDataOrSetting(data, key+".if");
						if (valueif!=null) {
							final String resif = resolve(stack, data, valueif);
							final Boolean resifbool = BooleanUtils.toBooleanObject(resif);
							if (resifbool==null) {
								res = "(if:"+valueif+")";
								break c;
							}
							final String valuebool = getKeyFromDataOrSetting(data, key+(resifbool ? ".true" : ".false"));
							if (valuebool!=null)
								res = resolve(stack, data, valuebool);
							else
								res = "";
							break c;
						}
					} else {
						res = resolve(stack, data, value);
						break c;
					}
				}
				if (res==null)
					res = "(err:"+key+")";
				m.appendReplacement(sb, Matcher.quoteReplacement(res));
			}
			m.appendTail(sb);

			stack.pop();
			return sb.toString();
		}

		public String resolve(final AbstractData data, final String str) {
			return resolve(new ArrayDeque<String>(), data, str);
		}

		@Deprecated
		public String get(final AbstractData data, final String path) {
			final String key = getKeyFromDataOrSetting(data, path);
			if (key==null)
				return null;
			return resolve(data, key);
		}

		protected String getKeyFromDataOrSetting(final AbstractData data, final String path) {
			String key = data.get(path);
			if (key==null)
				key = getKeyFromSetting(path);
			return key;
		}

		protected abstract String getKeyFromSetting(String path);

		public static class ConfigSettings extends AbstractSettings {
			private final FileConfiguration cfg;

			public ConfigSettings(final FileConfiguration cfg) {
				this.cfg = cfg;
			}

			@Override
			protected String getKeyFromSetting(final String path) {
				final String key = this.cfg.getString(path);
				if (key!=null)
					if (!StringUtils.isEmpty(key))
						return key;
				return null;
			}
		}

		public static class TestSettings extends AbstractSettings {
			private final Map<String, Object> map;

			public TestSettings(final Map<String, Object> map) {
				this.map = map;
			}

			@Override
			protected String getKeyFromSetting(final String path) {
				//Log.log.info(path);
				final Object key = this.map.get(path);
				if (key!=null) {
					final String keystr = String.valueOf(key);
					if (!StringUtils.isEmpty(keystr))
						return keystr;
				}
				return null;
			}
		}
	}

	public static abstract class AbstractData {
		public abstract String get(String path);

		public static class ItemData extends AbstractData {
			private final NBTCompound nbt;

			public ItemData(final NBTCompound nbt) {
				this.nbt = nbt;
			}

			@Override
			public String get(final String path) {
				final NBTType type = this.nbt.getType(path);
				switch (type) {
					case NBTTagString:
						return this.nbt.getString(path);
					case NBTTagInt:
						return String.valueOf(this.nbt.getInteger(path));
					case NBTTagFloat:
						return String.valueOf(this.nbt.getFloat(path));
					case NBTTagDouble:
						return String.valueOf(this.nbt.getDouble(path));
					case NBTTagLong:
						return String.valueOf(this.nbt.getInteger(path));
					case NBTTagShort:
						return String.valueOf(this.nbt.getShort(path));
					case NBTTagByte:
						return String.valueOf(this.nbt.getByte(path));
					default:
						return null;
				}
			}
		}

		public static class TestData extends AbstractData {
			private final Map<String, Object> data;

			public TestData(final Map<String, Object> data) {
				this.data = data;
			}

			@Override
			public String get(final String path) {
				final Object obj = this.data.get(path);
				if (obj==null)
					return null;
				return String.valueOf(obj);
			}
		}
	}
}