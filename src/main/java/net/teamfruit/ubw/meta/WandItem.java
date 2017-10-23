package net.teamfruit.ubw.meta;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

import net.teamfruit.ubw.WandData;
import net.teamfruit.ubw.lib.de.tr7zw.itemnbtapi.NBTItem;

public class WandItem {
	private @Nonnull NBTItem nbtItem;
	private @Nonnull WandItemMeta data;

	private WandItem(final @Nonnull NBTItem nbtItem, final @Nonnull WandItemMeta data) {
		this.nbtItem = nbtItem;
		this.data = data;
	}

	public @Nonnull ItemStack getItem() {
		return this.nbtItem.getItem();
	}

	public @Nonnull WandItemMeta getMeta() {
		return this.data;
	}

	public static boolean isItem(final ItemStack itemStack) {
		return !(itemStack==null||itemStack.getAmount()==0);
	}

	public static boolean isWandItem(final @Nonnull ItemStack itemStack) {
		if (!isItem(itemStack))
			throw new IllegalArgumentException("argument is not item");
		return new NBTItem(itemStack).hasKey(WandData.USEFUL_BUILDERS_WAND_NBT);
	}

	public static @Nonnull WandItem newWandItem(final @Nonnull ItemStack itemStack) {
		if (!isItem(itemStack))
			throw new IllegalArgumentException("argument is not item");
		final NBTItem nbtItem = new NBTItem(itemStack);
		if (!nbtItem.hasKey(WandData.USEFUL_BUILDERS_WAND_NBT))
			nbtItem.addCompound(WandData.USEFUL_BUILDERS_WAND_NBT);
		return new WandItem(nbtItem, new WandItemMeta(nbtItem.getCompound(WandData.USEFUL_BUILDERS_WAND_NBT)));
	}
}