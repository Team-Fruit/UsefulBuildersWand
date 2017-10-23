package net.teamfruit.ubw;

import org.bukkit.inventory.ItemStack;

public class ItemStackHolder {
	private ItemStack itemStack;

	public ItemStackHolder(final ItemStack itemStack) {
		setItem(itemStack);
	}

	public void setItem(final ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public ItemStack getItem() {
		return this.itemStack;
	}
}
