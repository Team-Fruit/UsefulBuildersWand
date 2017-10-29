package net.teamfruit.ubw;

import static net.teamfruit.ubw.meta.WandFeatureRegistry.*;

import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.teamfruit.ubw.meta.IWandWritableMeta;
import net.teamfruit.ubw.meta.WandCompoundMeta;
import net.teamfruit.ubw.meta.WandConfigMeta;
import net.teamfruit.ubw.meta.WandFeatureRegistry;
import net.teamfruit.ubw.meta.WandFeatureRegistry.FeatureCategory;
import net.teamfruit.ubw.meta.WandFeatureRegistry.FeatureRegistryEntry;

public class WandData {
	public static final WandData INSTANCE = new WandData();

	public static final String SETTING_LANG = "setting.lang";
	public static final String SETTING_EFFECT_RANGE = "setting.effect.range";

	public static final String USEFUL_BUILDERS_WAND_NBT = "ubwand";

	public static final String ITEM_PREFIX = "item.prefix";
	public static final String ITEM_TITLE = "item.title";
	public static final String ITEM_LORE = "item.lore";

	private static final Map<String, Object> configInit = Maps.newHashMap();
	static {
		configInit.put(SETTING_LANG, "en_US.lang");
		configInit.put(SETTING_EFFECT_RANGE, 48);

		final FeatureCategory category = WandFeatureRegistry.getRootCategory();
		for (final FeatureRegistryEntry<?> entry : category.getFeatureEntries())
			configInit.put(entry.feature().getPath(), entry.getAttribute(WandFeatureRegistry.ATTRIBUTE_DEFAULT));

		configInit.put("custom.durability.unbreakable.eval", "<=");
		configInit.put("custom.durability.unbreakable.arg0", "${"+FEATURE_META_DURABILITY_MAX.path+"}");
		configInit.put("custom.durability.unbreakable.arg1", "0");
		configInit.put("custom.title.namecolor.eval", "replace");
		configInit.put("custom.title.namecolor.arg0", "${"+FEATURE_META_NAME.path+"}");
		configInit.put("custom.title.namecolor.arg1", "&");
		configInit.put("custom.title.namecolor.arg2", "§");
		configInit.put("custom.title.namenotexists.eval", "empty");
		configInit.put("custom.title.namenotexists.arg0", "${custom.title.namecolor}");
		configInit.put("custom.title.name.if", "${custom.title.namenotexists}");
		configInit.put("custom.title.name.true", "§eBuilder's Wand");
		configInit.put("custom.title.name.false", "${custom.title.namecolor}");
		configInit.put("custom.title.mode.if", "${"+FEATURE_META_MODE.path+"}");
		configInit.put("custom.title.mode.true", "┃");
		configInit.put("custom.title.mode.false", "━");
		configInit.put("custom.title.durability.if", "${custom.durability.unbreakable}");
		configInit.put("custom.title.durability.true", "Infinity");
		configInit.put("custom.title.durability.false", "${"+FEATURE_META_DURABILITY.path+"}/${"+FEATURE_META_DURABILITY_MAX.path+"}");
		configInit.put("custom.lore.mode.if", "${"+FEATURE_META_MODE.path+"}");
		configInit.put("custom.lore.mode.true", "Vertical");
		configInit.put("custom.lore.mode.false", "Horizonal");
		configInit.put("custom.lore.blockcount.if", "${"+FEATURE_META_DURABILITY_BLOCKCOUNT.path+"}");
		configInit.put("custom.lore.blockcount.true", " blocks");
		configInit.put("custom.lore.blockcount.false", " times");
		configInit.put("custom.lore.durability.if", "${custom.durability.unbreakable}");
		configInit.put("custom.lore.durability.true", "(Infinity)");
		configInit.put("custom.lore.durability.false", "${"+FEATURE_META_DURABILITY.path+"} of ${"+FEATURE_META_DURABILITY_MAX.path+"}${custom.lore.blockcount}");
		configInit.put("custom.lore.owner.public.eval", "empty");
		configInit.put("custom.lore.owner.public.arg0", "${"+FEATURE_META_OWNER_ID.path+"}");
		configInit.put("custom.lore.owner.name.eval", "name");
		configInit.put("custom.lore.owner.name.arg0", "${"+FEATURE_META_OWNER_ID.path+"}");
		configInit.put("custom.lore.owner.namenotexists.eval", "empty");
		configInit.put("custom.lore.owner.namenotexists.arg0", "${custom.lore.owner.name}");
		configInit.put("custom.lore.owner.nameuuid.if", "${custom.lore.owner.namenotexists}");
		configInit.put("custom.lore.owner.nameuuid.true", "§8${"+FEATURE_META_OWNER_ID.path+"}");
		configInit.put("custom.lore.owner.nameuuid.false", "${custom.lore.owner.name}");
		configInit.put("custom.lore.owner.namepublic.if", "${custom.lore.owner.public}");
		configInit.put("custom.lore.owner.namepublic.true", "§bPublic");
		configInit.put("custom.lore.owner.namepublic.false", "${custom.lore.owner.nameuuid}");
		configInit.put("custom.lore.owner.if", "${"+FEATURE_META_OWNER.path+"}");
		configInit.put("custom.lore.owner.true", "§3 - Owner §7: ${custom.lore.owner.namepublic}");

		configInit.put(ITEM_PREFIX, "§m§a§k§e§r");
		configInit.put(ITEM_TITLE, "${custom.title.name} §7x${"+FEATURE_META_SIZE.path+"} [${custom.title.mode}] (${custom.title.durability})");
		configInit.put(ITEM_LORE, Lists.newArrayList(new String[] {
				"§3 - Mode §7: ${custom.lore.mode}",
				"§3 - Durability §7: ${custom.lore.durability}",
				"§3 - Size §7: ${"+FEATURE_META_SIZE.path+"}",
				"§3 - UseCount §7: ${"+FEATURE_META_COUNT_USE.path+"}",
				"§3 - PlaceCount §7: ${"+FEATURE_META_COUNT_PLACE.path+"}",
				"${custom.lore.owner}",
		}));
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
		if (this.cfg==null)
			throw new IllegalStateException("WandData config data is not initialized. call after initialized.");
		return this.cfg;
	}

	private WandConfigMeta configMeta;

	public WandConfigMeta configMeta() {
		if (this.configMeta==null)
			this.configMeta = new WandConfigMeta(getConfig());
		return this.configMeta;
	}

	public @Nonnull IWandWritableMeta wrapMeta(final IWandWritableMeta meta) {
		return WandCompoundMeta.writableof(meta, configMeta());
	}
}