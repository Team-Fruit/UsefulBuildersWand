package net.teamfruit.usefulbuilderswand;

import static net.teamfruit.usefulbuilderswand.meta.Features.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi.NBTCompound;
import net.teamfruit.usefulbuilderswand.meta.Evals;
import net.teamfruit.usefulbuilderswand.meta.Features;

public class WandData {
	public static final String SETTING_EFFECT_RANGE = "setting.effect.range";

	public static final String USEFUL_BUILDERS_WAND_NBT = "ubwand";

	public static final String FEATURE_META = "feature.meta";

	public static final String ITEM_TITLE = "item.title";
	public static final String ITEM_LORE = "item.lore";

	public static final Map<String, Object> it = Maps.newHashMap();
	static {
		it.put(SETTING_EFFECT_RANGE, 48);

		for (final Features ft : values())
			it.put(ft.key, ft.defaultValue);

		it.put("custom.durability.unbreakable.eval", "<=");
		it.put("custom.durability.unbreakable.arg0", "${"+FEATURE_META_DURABILITY_MAX.key+"}");
		it.put("custom.durability.unbreakable.arg1", "0");
		it.put("custom.title.mode.if", "${"+FEATURE_META_MODE.key+"}");
		it.put("custom.title.mode.true", "┃");
		it.put("custom.title.mode.false", "━");
		it.put("custom.title.durability.if", "${custom.durability.unbreakable}");
		it.put("custom.title.durability.true", "Infinity");
		it.put("custom.title.durability.false", "${"+FEATURE_META_DURABILITY.key+"}/${"+FEATURE_META_DURABILITY_MAX.key+"}");
		it.put("custom.lore.mode.if", "${"+FEATURE_META_MODE.key+"}");
		it.put("custom.lore.mode.true", "Vertical");
		it.put("custom.lore.mode.false", "Horizonal");
		it.put("custom.lore.blockcount.if", "${"+FEATURE_META_DURABILITY_BLOCKCOUNT.key+"}");
		it.put("custom.lore.blockcount.true", " blocks");
		it.put("custom.lore.blockcount.false", " times");
		it.put("custom.lore.durability.if", "${custom.durability.unbreakable}");
		it.put("custom.lore.durability.true", "(Infinity)");
		it.put("custom.lore.durability.false", "${"+FEATURE_META_DURABILITY.key+"} of ${"+FEATURE_META_DURABILITY_MAX.key+"}${custom.lore.blockcount}");
		it.put("custom.lore.owner.if", "${"+FEATURE_META_OWNER.key+"}");
		it.put("custom.lore.owner.true", "§3 - Owner §7: ${"+FEATURE_META_OWNER_NAME.key+"}");

		it.put(ITEM_TITLE, "§eBuilder's Wand §7x${"+FEATURE_META_SIZE.key+"} [${custom.title.mode}] (${custom.title.durability})");
		it.put(ITEM_LORE, Lists.newArrayList(new String[] {
				"§3 - Mode §7: ${custom.lore.mode}",
				"§3 - Durability §7: ${custom.lore.durability}",
				"§3 - Size §7: ${"+FEATURE_META_SIZE.key+"}",
				"§3 - UseCount §7: ${"+FEATURE_META_COUNT_USE.key+"}",
				"§3 - PlaceCount §7: ${"+FEATURE_META_COUNT_PLACE.key+"}",
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

	public static abstract class AbstractSettings {
		private static Pattern p = Pattern.compile("\\$\\{(.*?)\\}");

		protected String resolve(final Deque<String> stack, final AbstractData data, final String str) {
			if (stack.contains(str))
				return null;
			stack.push(str);

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

						final String valueeval = getKeyFromDataOrSetting(data, key+".eval");
						if (valueeval!=null) {
							final Evals evtype = Evals.evals.get(valueeval);
							if (evtype!=null) {
								final List<String> vargs = Lists.newArrayList();
								String valuearg;
								for (int i = 0; (valuearg = getKeyFromDataOrSetting(data, key+".arg"+i))!=null; i++) {
									final String resarg = resolve(stack, data, valuearg);
									if (resarg!=null)
										vargs.add(resarg);
								}
								res = evtype.eval(vargs);
								break c;
							}
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
				if (this.cfg.isConfigurationSection(path)) {
					if (!StringUtils.endsWith(path, ".data"))
						return getKeyFromSetting(path+".data");
				} else {
					final String key = this.cfg.getString(path);
					if (!StringUtils.isEmpty(key))
						return key;
				}
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
				if (!this.nbt.hasKey(path))
					return null;
				final Features ft = Features.getFeature(path);
				if (ft!=null)
					switch (ft.type) {
						case NUMBER:
							final Integer num = this.nbt.getInteger(path);
							if (num!=null)
								return String.valueOf(num);
							break;
						default:
						case TEXT:
							final String str = this.nbt.getString(path);
							if (str!=null)
								return str;
							break;
						case FLAG:
							final Boolean fla = this.nbt.getBoolean(path);
							if (fla!=null)
								return String.valueOf(fla);
							break;
					}
				return null;
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