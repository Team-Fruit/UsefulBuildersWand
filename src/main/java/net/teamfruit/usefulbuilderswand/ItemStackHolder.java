package net.teamfruit.usefulbuilderswand;

import org.bukkit.inventory.ItemStack;

public class ItemStackHolder {
	private ItemStack itemStack;

	public ItemStackHolder(final ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public ItemStackHolder set(final ItemStack itemStack) {
		this.itemStack = itemStack;
		return this;
	}

	public ItemStack get() {
		return this.itemStack;
	}
}
