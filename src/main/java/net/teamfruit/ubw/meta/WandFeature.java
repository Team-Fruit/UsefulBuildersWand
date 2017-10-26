package net.teamfruit.ubw.meta;

import org.apache.commons.lang.StringUtils;

public final class WandFeature<T> {
	public static final String FEATURE_META = "feature.meta";
	public final String key;
	public final String path;
	public final WandItemMetaType type;

	private WandFeature(final String key, final WandItemMetaType type) {
		this.key = StringUtils.substringBeforeLast(key, ".data");
		this.path = FEATURE_META+"."+key;
		this.type = type;
	}

	public String getKey() {
		return this.key;
	}

	public String getPath() {
		return this.path;
	}

	public static WandFeature<Integer> numberFeature(final String key) {
		return new WandFeature<Integer>(key, WandItemMetaType.NUMBER);
	}

	public static WandFeature<String> textFeature(final String key) {
		return new WandFeature<String>(key, WandItemMetaType.TEXT);
	}

	public static WandFeature<Boolean> flagFeature(final String key) {
		return new WandFeature<Boolean>(key, WandItemMetaType.FLAG);
	}
}
