package net.teamfruit.usefulbuilderswand.meta;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

import net.teamfruit.usefulbuilderswand.ItemStackHolder;
import net.teamfruit.usefulbuilderswand.WandData;
import net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi.NBTItem;

public class WandItem implements ItemStackHolder {
	private NBTItem nbtItem;
	private WandItemMeta data;

	public WandItem(final ItemStack item) {
		this.nbtItem = new NBTItem(item);
	}

	public void activate() {
		if (!hasContent()) {
			this.nbtItem.addCompound(WandData.USEFUL_BUILDERS_WAND_NBT);
			init();
		}
	}

	public boolean hasContent() {
		return this.nbtItem.hasKey(WandData.USEFUL_BUILDERS_WAND_NBT);
	}

	public boolean init() {
		if (this.data==null&&hasContent())
			this.data = new WandItemMeta(this.nbtItem.getCompound(WandData.USEFUL_BUILDERS_WAND_NBT));
		return this.data!=null;
	}

	@Override
	public ItemStack getItem() {
		return this.nbtItem.getItem();
	}

	@Override
	public void setItem(final ItemStack itemStack) {
		this.nbtItem = new NBTItem(itemStack);
		this.data = null;
	}

	public @Nullable WandItemMeta getMeta() {
		init();
		return this.data;
	}
}