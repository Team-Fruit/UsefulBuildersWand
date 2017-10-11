package net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi;

import org.bukkit.block.BlockState;

public class NBTTileEntity extends NBTCompound {

	private final BlockState tile;

	public NBTTileEntity(final BlockState tile) {
		super(null, null);
		this.tile = tile;
	}

	@Override
	protected Object getCompound() {
		return NBTReflectionUtil.getTileEntityNBTTagCompound(this.tile);
	}

	@Override
	protected void setCompound(final Object tag) {
		NBTReflectionUtil.setTileEntityNBTTagCompound(this.tile, tag);
	}

}
