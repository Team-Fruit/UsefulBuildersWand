package net.teamfruit.ubw;

import org.bukkit.inventory.ItemStack;

public interface ItemStackHolder {
	public void setItem(final ItemStack itemStack);

	public ItemStack getItem();

	public static class DefaultItemStackHolder implements ItemStackHolder {
		private ItemStack itemStack;

		public DefaultItemStackHolder(final ItemStack itemStack) {
			setItem(itemStack);
		}

		@Override
		public void setItem(final ItemStack itemStack) {
			this.itemStack = itemStack;
		}

		@Override
		public ItemStack getItem() {
			return this.itemStack;
		}
	}
}
