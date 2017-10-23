package net.teamfruit.ubw;

import static net.teamfruit.ubw.meta.Features.*;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.teamfruit.ubw.meta.Features;
import net.teamfruit.ubw.meta.IWandMeta;
import net.teamfruit.ubw.meta.WandCompoundMeta;
import net.teamfruit.ubw.meta.WandConfigMeta;
import net.teamfruit.ubw.meta.WandItem;
import net.teamfruit.ubw.meta.WandTextUtils;

public class WandData {
	public static final String SETTING_LANG = "setting.lang";
	public static final String SETTING_EFFECT_RANGE = "setting.effect.range";

	public static final String USEFUL_BUILDERS_WAND_NBT = "ubwand";

	public static final String ITEM_TITLE = "item.title";
	public static final String ITEM_LORE = "item.lore";

	public static final Map<String, Object> it = Maps.newHashMap();
	static {
		it.put(SETTING_LANG, "en_US.lang");
		it.put(SETTING_EFFECT_RANGE, 48);

		for (final Features ft : values())
			it.put(ft.path, ft.defaultValue);

		it.put("custom.durability.unbreakable.eval", "<=");
		it.put("custom.durability.unbreakable.arg0", "${"+FEATURE_META_DURABILITY_MAX.path+"}");
		it.put("custom.durability.unbreakable.arg1", "0");
		it.put("custom.title.mode.if", "${"+FEATURE_META_MODE.path+"}");
		it.put("custom.title.mode.true", "┃");
		it.put("custom.title.mode.false", "━");
		it.put("custom.title.durability.if", "${custom.durability.unbreakable}");
		it.put("custom.title.durability.true", "Infinity");
		it.put("custom.title.durability.false", "${"+FEATURE_META_DURABILITY.path+"}/${"+FEATURE_META_DURABILITY_MAX.path+"}");
		it.put("custom.lore.mode.if", "${"+FEATURE_META_MODE.path+"}");
		it.put("custom.lore.mode.true", "Vertical");
		it.put("custom.lore.mode.false", "Horizonal");
		it.put("custom.lore.blockcount.if", "${"+FEATURE_META_DURABILITY_BLOCKCOUNT.path+"}");
		it.put("custom.lore.blockcount.true", " blocks");
		it.put("custom.lore.blockcount.false", " times");
		it.put("custom.lore.durability.if", "${custom.durability.unbreakable}");
		it.put("custom.lore.durability.true", "(Infinity)");
		it.put("custom.lore.durability.false", "${"+FEATURE_META_DURABILITY.path+"} of ${"+FEATURE_META_DURABILITY_MAX.path+"}${custom.lore.blockcount}");
		it.put("custom.lore.owner.public.eval", "empty");
		it.put("custom.lore.owner.public.arg0", "${"+FEATURE_META_OWNER_ID.path+"}");
		it.put("custom.lore.owner.name.eval", "name");
		it.put("custom.lore.owner.name.arg0", "${"+FEATURE_META_OWNER_ID.path+"}");
		it.put("custom.lore.owner.namenotexists.eval", "empty");
		it.put("custom.lore.owner.namenotexists.arg0", "${custom.lore.owner.name}");
		it.put("custom.lore.owner.nameuuid.if", "${custom.lore.owner.namenotexists}");
		it.put("custom.lore.owner.nameuuid.true", "§8${"+FEATURE_META_OWNER_ID.path+"}");
		it.put("custom.lore.owner.nameuuid.false", "${custom.lore.owner.name}");
		it.put("custom.lore.owner.namepublic.if", "${custom.lore.owner.public}");
		it.put("custom.lore.owner.namepublic.true", "§bPublic");
		it.put("custom.lore.owner.namepublic.false", "${custom.lore.owner.nameuuid}");
		it.put("custom.lore.owner.if", "${"+FEATURE_META_OWNER.path+"}");
		it.put("custom.lore.owner.true", "§3 - Owner §7: ${custom.lore.owner.namepublic}");

		it.put(ITEM_TITLE, "§eBuilder's Wand §7x${"+FEATURE_META_SIZE.path+"} [${custom.title.mode}] (${custom.title.durability})");
		it.put(ITEM_LORE, Lists.newArrayList(new String[] {
				"§3 - Mode §7: ${custom.lore.mode}",
				"§3 - Durability §7: ${custom.lore.durability}",
				"§3 - Size §7: ${"+FEATURE_META_SIZE.path+"}",
				"§3 - UseCount §7: ${"+FEATURE_META_COUNT_USE.path+"}",
				"§3 - PlaceCount §7: ${"+FEATURE_META_COUNT_PLACE.path+"}",
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

	public WandConfigMeta configMeta() {
		return new WandConfigMeta(getConfig());
	}

	public IWandMeta wrapMeta(final IWandMeta meta) {
		return WandCompoundMeta.of(meta, configMeta());
	}

	public void updateItem(final WandItem meta) {
		final ItemStack item = meta.getItem();
		final ItemMeta itemmeta = item.getItemMeta();
		final FileConfiguration cfg = getConfig();
		final IWandMeta wmeta = wrapMeta(meta.getMeta());

		Object itemtitle = cfg.get(WandData.ITEM_TITLE);
		if (itemtitle==null)
			itemtitle = WandData.it.get(WandData.ITEM_TITLE);
		if (itemtitle instanceof String)
			itemmeta.setDisplayName(WandTextUtils.resolve(wmeta, (String) itemtitle));

		Object itemlore = cfg.get(WandData.ITEM_LORE);
		if (itemlore==null)
			itemlore = WandData.it.get(WandData.ITEM_LORE);
		if (itemlore instanceof List<?>) {
			final List<String> newlore = Lists.newArrayList();
			for (final Object obj : (List<?>) itemlore)
				if (obj instanceof String) {
					final String res = WandTextUtils.resolve(wmeta, (String) obj);
					if (!StringUtils.isEmpty(res))
						newlore.add(res);
				}
			itemmeta.setLore(newlore);
		}
		item.setItemMeta(itemmeta);
	}
}