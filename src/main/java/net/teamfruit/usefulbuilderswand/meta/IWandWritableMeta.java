package net.teamfruit.usefulbuilderswand.meta;

import javax.annotation.Nullable;

public interface IWandWritableMeta extends IWandMeta {
	void setNumber(final String key, @Nullable final Integer value);

	void setText(final String key, @Nullable final String value);

	void setFlag(final String key, @Nullable final Boolean value);
}
