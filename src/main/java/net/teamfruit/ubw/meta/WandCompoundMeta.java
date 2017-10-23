package net.teamfruit.ubw.meta;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

public class WandCompoundMeta implements IWandMeta {
	private final List<IWandMeta> compound;

	public WandCompoundMeta(final List<IWandMeta> compound) {
		this.compound = compound;
	}

	@Override
	public WandItemMetaType getType(final String path) {
		for (final IWandMeta meta : this.compound) {
			final WandItemMetaType type = meta.getType(path);
			if (type!=null)
				return type;
		}
		return null;
	}

	@Override
	public @Nullable Integer getNumber(final String key) {
		for (final IWandMeta meta : this.compound) {
			final Integer value = meta.getNumber(key);
			if (value!=null)
				return value;
		}
		return null;
	}

	@Override
	public @Nullable String getText(final String key) {
		for (final IWandMeta meta : this.compound) {
			final String value = meta.getText(key);
			if (value!=null)
				return value;
		}
		return null;
	}

	@Override
	public @Nullable Boolean getFlag(final String key) {
		for (final IWandMeta meta : this.compound) {
			final Boolean value = meta.getFlag(key);
			if (value!=null)
				return value;
		}
		return null;
	}

	public static WandCompoundMeta of(final IWandMeta... metas) {
		return new WandCompoundMeta(Lists.newArrayList(metas));
	}
}