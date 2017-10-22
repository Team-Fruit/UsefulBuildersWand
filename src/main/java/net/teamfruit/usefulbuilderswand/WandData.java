package net.teamfruit.usefulbuilderswand;

import static net.teamfruit.usefulbuilderswand.meta.Features.*;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.teamfruit.usefulbuilderswand.meta.WandConfigMeta;
import net.teamfruit.usefulbuilderswand.meta.Features;
import net.teamfruit.usefulbuilderswand.meta.IWandMeta;
import net.teamfruit.usefulbuilderswand.meta.WandCompoundMeta;
import net.teamfruit.usefulbuilderswand.meta.WandItem;
import net.teamfruit.usefulbuilderswand.meta.WandTextUtils;

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

	public IWandMeta wrapMeta(final IWandMeta meta) {
		return WandCompoundMeta.of(meta, new WandConfigMeta(getConfig()));
	}

	public void updateItem(final WandItem meta) {
		if (meta.init()) {
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
}