package net.teamfruit.ubw.meta;

import javax.annotation.Nullable;

import net.teamfruit.ubw.lib.de.tr7zw.itemnbtapi.NBTCompound;

public class WandItemMeta implements IWandWritableMeta {
	public final NBTCompound nbt;

	public WandItemMeta(final NBTCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public WandItemMetaType getType(final String path) {
		final WandFeature<?> ft = WandFeatureRegistry.getFeaturePath(path);
		if (ft!=null)
			return ft.type;
		return null;
	}

	@Override
	public @Nullable Integer getNumber(final String key) {
		if (this.nbt.hasKey(key))
			return this.nbt.getInteger(key);
		return null;
	}

	@Override
	public void setNumber(final String key, @Nullable final Integer value) {
		this.nbt.setInteger(key, value);
	}

	@Override
	public @Nullable String getText(final String key) {
		if (this.nbt.hasKey(key))
			return this.nbt.getString(key);
		return null;
	}

	@Override
	public void setText(final String key, @Nullable final String value) {
		this.nbt.setString(key, value);
	}

	@Override
	public @Nullable Boolean getFlag(final String key) {
		if (this.nbt.hasKey(key))
			return this.nbt.getBoolean(key);
		return null;
	}

	@Override
	public void setFlag(final String key, @Nullable final Boolean value) {
		this.nbt.setBoolean(key, value);
	}
}