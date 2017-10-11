package net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi;

import org.bukkit.entity.Entity;

public class NBTEntity extends NBTCompound {

	private final Entity ent;

	public NBTEntity(final Entity entity) {
		super(null, null);
		this.ent = entity;
	}

	@Override
	protected Object getCompound() {
		return NBTReflectionUtil.getEntityNBTTagCompound(NBTReflectionUtil.getNMSEntity(this.ent));
	}

	@Override
	protected void setCompound(final Object tag) {
		NBTReflectionUtil.setEntityNBTTag(tag, NBTReflectionUtil.getNMSEntity(this.ent));
	}

}
