package net.teamfruit.usefulbuilderswand.meta;

import static net.teamfruit.usefulbuilderswand.meta.WandItemMetaType.*;

import org.apache.commons.lang.StringUtils;

import net.teamfruit.usefulbuilderswand.WandData;

public enum Features {
	FEATURE_META_SIZE("size", NUMBER, 9),
	FEATURE_META_MODE("mode", FLAG, false),
	FEATURE_META_DURABILITY("durability.data", NUMBER, 27),
	FEATURE_META_DURABILITY_MAX("durability.max", NUMBER, 27),
	FEATURE_META_DURABILITY_BLOCKCOUNT("durability.blockcount", FLAG, false),
	FEATURE_META_COUNT_PLACE("count.place", NUMBER, 0),
	FEATURE_META_COUNT_USE("count.use", NUMBER, 0),
	FEATURE_META_PARTICLE_COLOR_R("particle.color.r", NUMBER, 255),
	FEATURE_META_PARTICLE_COLOR_G("particle.color.g", NUMBER, 255),
	FEATURE_META_PARTICLE_COLOR_B("particle.color.b", NUMBER, 255),
	FEATURE_META_PARTICLE_SHARE("particle.share", FLAG, true),
	FEATURE_META_OWNER("owner.data", FLAG, false),
	FEATURE_META_OWNER_ID("owner.id", TEXT, ""),
	FEATURE_META_OWNER_NAME("owner.name", TEXT, ""),
	;

	public final String key;
	public final String path;
	public final WandItemMetaType type;
	public final Object defaultValue;

	private Features(final String key, final WandItemMetaType type, final Object defaultValue) {
		this.key = StringUtils.substringBeforeLast(key, ".data");
		this.path = WandData.FEATURE_META+"."+key;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public static Features getFeature(final String path) {
		for (final Features feature : values())
			if (StringUtils.equals(feature.path, path))
				return feature;
		return null;
	}

	public static Features getFeatureKey(final String key) {
		for (final Features feature : values())
			if (StringUtils.equals(feature.key, key))
				return feature;
		return null;
	}
}
