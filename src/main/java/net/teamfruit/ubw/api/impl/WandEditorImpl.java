package net.teamfruit.ubw.api.impl;

import org.bukkit.inventory.ItemStack;

import net.teamfruit.ubw.WandData;
import net.teamfruit.ubw.api.WandEditor;
import net.teamfruit.ubw.meta.IWandMeta;
import net.teamfruit.ubw.meta.WandItem;
import net.teamfruit.ubw.meta.WandItemMeta;

public class WandEditorImpl implements WandEditor {
	private final WandData wanddata;
	private final WandItem witem;
	private WandItemMeta wmeta;
	private IWandMeta meta;

	public WandEditorImpl(final WandData wanddata, final ItemStack itemStack) {
		this.wanddata = wanddata;
		if (!WandItem.isItem(itemStack))
			throw new IllegalArgumentException("null itemstack");
		this.witem = WandItem.newWandItem(itemStack);
		this.wmeta = this.witem.getMeta();
		this.meta = this.wanddata.wrapMeta(this.wmeta);
	}
}
