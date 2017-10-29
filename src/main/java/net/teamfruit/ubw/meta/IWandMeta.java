package net.teamfruit.ubw.meta;

import javax.annotation.Nullable;

public interface IWandMeta {
	@Nullable
	Integer getNumber(final String key);

	@Nullable
	String getText(final String key);

	@Nullable
	Boolean getFlag(final String key);
}
