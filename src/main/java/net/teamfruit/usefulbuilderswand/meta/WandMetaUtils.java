package net.teamfruit.usefulbuilderswand.meta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;

public class WandMetaUtils {

	public static void set(final @Nonnull IWandWritableMeta meta, final WandItemMetaType type, final String path, final @Nullable Object value) {
		if (type!=null)
			switch (type) {
				case NUMBER:
					meta.setNumber(path, toNumber(value));
					break;
				default:
				case TEXT:
					meta.setText(path, toText(value));
					break;
				case FLAG:
					meta.setFlag(path, toFlag(value));
					break;
			}
	}

	public static @Nullable Object get(final @Nonnull IWandMeta meta, final WandItemMetaType type, final String path) {
		if (type!=null)
			switch (type) {
				case NUMBER:
					return meta.getNumber(path);
				default:
				case TEXT:
					return meta.getText(path);
				case FLAG:
					return meta.getFlag(path);
			}
		return null;
	}

	public static void set(final @Nonnull IWandWritableMeta meta, final @Nonnull Features ft, final Object value) {
		set(meta, ft.type, ft.path, value);
	}

	public static Object get(final @Nonnull IWandMeta meta, final @Nonnull Features ft) {
		return get(meta, ft.type, ft.path);
	}

	public static void set(final IWandWritableMeta meta, final String path, final Object value) {
		set(meta, meta.getType(path), path, value);
	}

	public static Object get(final IWandMeta meta, final String path) {
		return get(meta, meta.getType(path), path);
	}

	public static @Nullable WandItemMetaType getType(@Nullable final Object value) {
		if (value instanceof Number)
			return WandItemMetaType.NUMBER;
		else if (value instanceof String)
			return WandItemMetaType.TEXT;
		else if (value instanceof Boolean)
			return WandItemMetaType.FLAG;
		return null;
	}

	public static @Nullable Integer toNumber(@Nullable final Object value) {
		if (value instanceof Number)
			return ((Number) value).intValue();
		else if (value instanceof String)
			return NumberUtils.toInt((String) value);
		return null;
	}

	public static @Nullable String toText(@Nullable final Object value) {
		if (value instanceof String)
			return (String) value;
		return null;
	}

	public static @Nullable Boolean toFlag(@Nullable final Object value) {
		if (value instanceof Boolean)
			return (Boolean) value;
		else if (value instanceof String)
			return BooleanUtils.toBooleanObject((String) value);
		return null;
	}

	public static @Nonnull <T> T or(final @Nullable T value, final @Nonnull T defaultValue) {
		return value!=null ? value : defaultValue;
	}
}
