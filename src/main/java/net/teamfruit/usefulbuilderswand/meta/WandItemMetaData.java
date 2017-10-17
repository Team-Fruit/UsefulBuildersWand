package net.teamfruit.usefulbuilderswand.meta;

import javax.annotation.Nullable;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;

import net.teamfruit.usefulbuilderswand.lib.de.tr7zw.itemnbtapi.NBTCompound;

public class WandItemMetaData {
	public final NBTCompound nbt;

	public WandItemMetaData(final NBTCompound nbt) {
		this.nbt = nbt;
	}

	public @Nullable Integer getNumber(final String key) {
		return this.nbt.getInteger(key);
	}

	public Integer getNumber(final String key, final Integer defaultValue) {
		final Integer value = getNumber(key);
		if (value!=null)
			return value;
		return defaultValue;
	}

	public void setNumber(final String key, @Nullable final Integer value) {
		if (value!=null)
			this.nbt.setInteger(key, value);
		else
			this.nbt.removeKey(key);
	}

	public @Nullable String getText(final String key) {
		return this.nbt.getString(key);
	}

	public String getText(final String key, final String defaultValue) {
		final String value = getText(key);
		if (value!=null)
			return value;
		return defaultValue;
	}

	public void setText(final String key, @Nullable final String value) {
		if (value!=null)
			this.nbt.setString(key, value);
		else
			this.nbt.removeKey(key);
	}

	public @Nullable Boolean getFlag(final String key) {
		return this.nbt.getBoolean(key);
	}

	public Boolean getFlag(final String key, final Boolean defaultValue) {
		final Boolean value = getFlag(key);
		if (value!=null)
			return value;
		return defaultValue;
	}

	public void setFlag(final String key, @Nullable final Boolean value) {
		if (value!=null)
			this.nbt.setBoolean(key, value);
		else
			this.nbt.removeKey(key);
	}

	public void set(final Features ft, final Object value) {
		switch (ft.type) {
			case NUMBER:
				if (value instanceof Number)
					setNumber(ft.key, ((Number) value).intValue());
				else if (value instanceof String)
					setNumber(ft.key, NumberUtils.toInt((String) value));
				else if (ft.defaultValue instanceof Number)
					setNumber(ft.key, ((Number) ft.defaultValue).intValue());
				else if (ft.defaultValue==null)
					setNumber(ft.key, null);
				break;
			default:
			case TEXT:
				if (value instanceof String)
					setText(ft.key, (String) value);
				else if (ft.defaultValue instanceof Number)
					setText(ft.key, (String) ft.defaultValue);
				else if (ft.defaultValue==null)
					setText(ft.key, null);
				break;
			case FLAG:
				if (value instanceof Boolean)
					setFlag(ft.key, (Boolean) value);
				else if (value instanceof String)
					setFlag(ft.key, BooleanUtils.toBooleanObject((String) value));
				else if (ft.defaultValue instanceof Boolean)
					setFlag(ft.key, (Boolean) ft.defaultValue);
				else if (ft.defaultValue==null)
					setFlag(ft.key, null);
				break;
		}
	}

	public Object get(final Features ft) {
		switch (ft.type) {
			case NUMBER:
				return getNumber(ft.key);
			default:
			case TEXT:
				return getText(ft.key);
			case FLAG:
				return getFlag(ft.key);
		}
	}

	public Object get(final Features ft, final Object defaultValue) {
		switch (ft.type) {
			case NUMBER:
				if (defaultValue instanceof Number)
					return getNumber(ft.key, ((Number) defaultValue).intValue());
				else if (defaultValue instanceof String)
					return getNumber(ft.key, NumberUtils.toInt((String) defaultValue));
				else if (ft.defaultValue instanceof Number)
					return getNumber(ft.key, ((Number) ft.defaultValue).intValue());
				else
					return getNumber(ft.key, null);
			default:
			case TEXT:
				if (defaultValue instanceof String)
					return getText(ft.key, (String) defaultValue);
				else if (ft.defaultValue instanceof Number)
					return getText(ft.key, (String) ft.defaultValue);
				else
					return getText(ft.key, null);
			case FLAG:
				if (defaultValue instanceof Boolean)
					return getFlag(ft.key, (Boolean) defaultValue);
				else if (defaultValue instanceof String)
					return getFlag(ft.key, BooleanUtils.toBooleanObject((String) defaultValue));
				else if (ft.defaultValue instanceof Boolean)
					return getFlag(ft.key, (Boolean) ft.defaultValue);
				else
					return getFlag(ft.key, null);
		}
	}
}