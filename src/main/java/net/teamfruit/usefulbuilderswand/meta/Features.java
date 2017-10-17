package net.teamfruit.usefulbuilderswand.meta;

import static net.teamfruit.usefulbuilderswand.meta.WandItemMetaType.*;

import org.apache.commons.lang.StringUtils;

public enum Features {
	FEATURE_META_SIZE("feature.meta.size", NUMBER, 9),
	FEATURE_META_MODE("feature.meta.mode", FLAG, false),
	FEATURE_META_DURABILITY("feature.meta.durability.data", NUMBER, 27),
	FEATURE_META_DURABILITY_MAX("feature.meta.durability.max", NUMBER, 27),
	FEATURE_META_DURABILITY_BLOCKCOUNT("feature.meta.durability.blockcount", FLAG, false),
	FEATURE_META_DURABILITY_UNBREAKABLE("feature.meta.durability.unbreakable", FLAG, false),
	FEATURE_META_COUNT_PLACE("feature.meta.count.place", NUMBER, 0),
	FEATURE_META_COUNT_USE("feature.meta.count.use", NUMBER, 0),
	FEATURE_META_PARTICLE_COLOR_R("feature.meta.particle.color.r", NUMBER, 255),
	FEATURE_META_PARTICLE_COLOR_G("feature.meta.particle.color.g", NUMBER, 255),
	FEATURE_META_PARTICLE_COLOR_B("feature.meta.particle.color.b", NUMBER, 255),
	FEATURE_META_PARTICLE_SHARE("feature.meta.particle.share", FLAG, true),
	FEATURE_META_OWNER("feature.meta.owner.data", FLAG, false),
	FEATURE_META_OWNER_ID("feature.meta.owner.id", TEXT, ""),
	FEATURE_META_OWNER_NAME("feature.meta.owner.name", TEXT, ""),
	;

	public final String key;
	public final WandItemMetaType type;
	public final Object defaultValue;

	private Features(final String key, final WandItemMetaType type, final Object defaultValue) {
		this.key = key;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public static Features getFeature(final String key) {
		for (final Features feature : values())
			if (StringUtils.equals(feature.key, key))
				return feature;
		return null;
	}
}
