package net.teamfruit.ubw.api.impl;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

import net.teamfruit.ubw.WandData;
import net.teamfruit.ubw.api.WandItemAPI;
import net.teamfruit.ubw.api.WandItemEditor;
import net.teamfruit.ubw.meta.WandItem;

public class WandItemAPIImpl implements WandItemAPI {
	private WandData wanddata;

	public WandItemAPIImpl(final WandData wanddata) {
		this.wanddata = wanddata;
	}

	private void checkItem(final ItemStack itemStack) throws IllegalArgumentException {
		if (!WandItem.isItem(itemStack))
			throw new IllegalArgumentException("This itemstack is null or empty.");
	}

	@Override
	public boolean isWand(final ItemStack itemStack) throws IllegalArgumentException {
		checkItem(itemStack);
		return WandItem.isWandItem(itemStack);
	}

	@Override
	public boolean activateWand(final ItemStack itemStack) throws IllegalArgumentException {
		checkItem(itemStack);
		WandItem.newWandItem(itemStack);
		return WandItem.isWandItem(itemStack);
	}

	@Override
	public @Nonnull WandItemEditor editWand(final ItemStack itemStack) throws IllegalArgumentException {
		if (!isWand(itemStack))
			throw new IllegalArgumentException("This itemstack is not a wand. Please activate before editing wand.");
		return new WandItemEditorImpl(this.wanddata, itemStack);
	}
}