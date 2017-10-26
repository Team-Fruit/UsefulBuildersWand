package net.teamfruit.ubw.meta;

import org.apache.commons.lang.StringUtils;

import net.teamfruit.ubw.api.WandProperty;

public final class WandFeature<T> implements WandProperty<T> {
	public static final String FEATURE_META = "feature.meta";
	public final String key;
	public final String path;
	public final WandItemMetaType type;
	public final T defaultValue;

	private WandFeature(final String key, final WandItemMetaType type, final T defaultValue) {
		this.key = StringUtils.substringBeforeLast(key, ".data");
		this.path = FEATURE_META+"."+key;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String getPath() {
		return this.path;
	}

	@Override
	public T getDefaultValue() {
		return this.defaultValue;
	}

	public static WandFeature<Integer> numberFeature(final String key, final Integer defaultValue) {
		return new WandFeature<Integer>(key, WandItemMetaType.NUMBER, defaultValue);
	}

	public static WandFeature<String> textFeature(final String key, final String defaultValue) {
		return new WandFeature<String>(key, WandItemMetaType.TEXT, defaultValue);
	}

	public static WandFeature<Boolean> flagFeature(final String key, final Boolean defaultValue) {
		return new WandFeature<Boolean>(key, WandItemMetaType.FLAG, defaultValue);
	}
}
