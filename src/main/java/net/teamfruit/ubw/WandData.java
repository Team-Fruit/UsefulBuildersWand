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
import net.teamfruit.ubw.meta.WandFeature;
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

	public static final FeatureCategory CUSTOM_CATEGORY = WandFeatureRegistry.ROOT_CATEGORY.createCategory();

	private static final Map<String, Object> configInit = Maps.newHashMap();
	static {
		configInit.put(SETTING_LANG, "en_US.lang");
		configInit.put(SETTING_EFFECT_RANGE, 48);

		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.durability.unbreakable.eval")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "<=");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.durability.unbreakable.arg0")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${"+FEATURE_META_DURABILITY_MAX.path+"}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.durability.unbreakable.arg1")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "0");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.namecolor.eval")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "replace");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.namecolor.arg0")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${"+FEATURE_META_NAME.path+"}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.namecolor.arg1")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "&");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.namecolor.arg2")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "§");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.namenotexists.eval")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "empty");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.namenotexists.arg0")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${custom.title.namecolor}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.name.if")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${custom.title.namenotexists}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.name.true")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "§eBuilder's Wand");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.name.false")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${custom.title.namecolor}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.mode.if")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${"+FEATURE_META_MODE.path+"}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.mode.true")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "┃");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.mode.false")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "━");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.durability.if")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${custom.durability.unbreakable}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.durability.true")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "Infinity");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.title.durability.false")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${"+FEATURE_META_DURABILITY.path+"}/${"+FEATURE_META_DURABILITY_MAX.path+"}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.mode.if")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${"+FEATURE_META_MODE.path+"}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.mode.true")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "Vertical");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.mode.false")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "Horizonal");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.blockcount.if")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${"+FEATURE_META_DURABILITY_BLOCKCOUNT.path+"}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.blockcount.true")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, " blocks");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.blockcount.false")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, " times");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.durability.if")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${custom.durability.unbreakable}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.durability.true")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "(Infinity)");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.durability.false")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${"+FEATURE_META_DURABILITY.path+"} of ${"+FEATURE_META_DURABILITY_MAX.path+"}${custom.lore.blockcount}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.public.eval")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "empty");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.public.arg0")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${"+FEATURE_META_OWNER_ID.path+"}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.name.eval")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "name");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.name.arg0")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${"+FEATURE_META_OWNER_ID.path+"}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.namenotexists.eval")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "empty");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.namenotexists.arg0")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${custom.lore.owner.name}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.nameuuid.if")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${custom.lore.owner.namenotexists}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.nameuuid.true")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "§8${"+FEATURE_META_OWNER_ID.path+"}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.nameuuid.false")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${custom.lore.owner.name}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.namepublic.if")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${custom.lore.owner.public}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.namepublic.true")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "§bPublic");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.namepublic.false")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${custom.lore.owner.nameuuid}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.if")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "${"+FEATURE_META_OWNER.path+"}");
		CUSTOM_CATEGORY.register(WandFeature.textFeature("custom.lore.owner.true")).setAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE, "§3 - Owner §7: ${custom.lore.owner.namepublic}");

		for (final FeatureRegistryEntry<?> entry : WandFeatureRegistry.ROOT_CATEGORY.getFeatureEntries())
			configInit.put(entry.feature().getPath(), entry.getAttribute(WandFeatureRegistry.ATTRIBUTE_INIT_DEFAULT_VALUE));

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