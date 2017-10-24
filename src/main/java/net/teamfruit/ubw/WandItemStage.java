package net.teamfruit.ubw;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import net.teamfruit.ubw.meta.Features;
import net.teamfruit.ubw.meta.IWandWritableMeta;
import net.teamfruit.ubw.meta.WandItem;
import net.teamfruit.ubw.meta.WandTextUtils;

public class WandItemStage implements ItemStackHolder {
	private final WandData wanddata;
	private ItemStack itemStack;
	private WandItem witem;
	private IWandWritableMeta wmeta;

	public WandItemStage(final @Nonnull WandData wanddata) {
		this.wanddata = wanddata;
	}

	@Override
	public void setItem(final ItemStack itemStack) {
		this.itemStack = itemStack;
		this.witem = null;
		this.wmeta = null;
	}

	@Override
	public ItemStack getItem() {
		if (this.witem!=null)
			return this.witem.getItem();
		return this.itemStack;
	}

	public boolean isItem() {
		return WandItem.isItem(getItem());
	}

	public boolean isWandItem() {
		return isItem()&&WandItem.isWandItem(getItem());
	}

	public @Nonnull WandItem getWandItem() {
		if (this.witem==null)
			this.witem = WandItem.newWandItem(getItem());
		return this.witem;
	}

	public @Nonnull IWandWritableMeta meta() {
		if (this.wmeta==null)
			this.wmeta = this.wanddata.wrapMeta(getWandItem().getMeta());
		return this.wmeta;
	}

	public void updateItem() {
		if (!isItem()||!isWandItem())
			return;
		final FileConfiguration cfg = this.wanddata.getConfig();

		Object itemprefix = cfg.get(WandData.ITEM_PREFIX);
		if (itemprefix==null)
			itemprefix = WandData.it.get(WandData.ITEM_PREFIX);
		if (itemprefix instanceof String) {
			final ItemMeta itemmeta = getItem().getItemMeta();
			if (itemmeta.hasDisplayName()) {
				final String name = itemmeta.getDisplayName();
				if (!StringUtils.startsWith(name, (String) itemprefix))
					meta().setText(Features.FEATURE_META_NAME.path, name);
			}
		}

		final ItemMeta itemmeta = getItem().getItemMeta();
		Object itemtitle = cfg.get(WandData.ITEM_TITLE);
		if (itemtitle==null)
			itemtitle = WandData.it.get(WandData.ITEM_TITLE);
		if (itemtitle instanceof String)
			itemmeta.setDisplayName((itemprefix instanceof String ? itemprefix : "")+WandTextUtils.resolve(meta(), (String) itemtitle));

		Object itemlore = cfg.get(WandData.ITEM_LORE);
		if (itemlore==null)
			itemlore = WandData.it.get(WandData.ITEM_LORE);
		if (itemlore instanceof List<?>) {
			final List<String> newlore = Lists.newArrayList();
			for (final Object obj : (List<?>) itemlore)
				if (obj instanceof String) {
					final String res = WandTextUtils.resolve(meta(), (String) obj);
					if (!StringUtils.isEmpty(res))
						newlore.add(res);
				}
			itemmeta.setLore(newlore);
		}
		getItem().setItemMeta(itemmeta);
	}
}
