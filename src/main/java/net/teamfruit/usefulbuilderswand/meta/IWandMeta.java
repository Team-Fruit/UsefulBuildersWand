package net.teamfruit.usefulbuilderswand.meta;

import javax.annotation.Nullable;

public interface IWandMeta {
	@Nullable
	WandItemMetaType getType(String key);

	@Nullable
	Integer getNumber(final String key);

	@Nullable
	String getText(final String key);

	@Nullable
	Boolean getFlag(final String key);
}
