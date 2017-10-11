package net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi;

import org.bukkit.inventory.ItemStack;

public class NBTItem extends NBTCompound {

	private ItemStack bukkitItem;

	public NBTItem(final ItemStack item) {
		super(null, null);
		this.bukkitItem = item.clone();
	}

	@Override
	protected Object getCompound() {
		return NBTReflectionUtil.getItemRootNBTTagCompound(NBTReflectionUtil.getNMSItemStack(this.bukkitItem));
	}

	@Override
	protected void setCompound(final Object tag) {
		this.bukkitItem = NBTReflectionUtil.getBukkitItemStack(NBTReflectionUtil.setNBTTag(tag, NBTReflectionUtil.getNMSItemStack(this.bukkitItem)));
	}

	public ItemStack getItem() {
		return this.bukkitItem;
	}

	@Override
	protected void setItem(final ItemStack item) {
		this.bukkitItem = item;
	}

}
