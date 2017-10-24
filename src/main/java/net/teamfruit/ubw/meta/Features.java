package net.teamfruit.ubw.meta;

import static net.teamfruit.ubw.meta.WandItemMetaType.*;

import org.apache.commons.lang.StringUtils;

public enum Features {
	FEATURE_META_NAME("name", TEXT, "", "usefulbuilderswand.set.settings.name"),
	FEATURE_META_SIZE("size", NUMBER, 9, "usefulbuilderswand.set.settings.size"),
	FEATURE_META_MODE("mode", FLAG, false, "usefulbuilderswand.set.meta.mode"),
	FEATURE_META_DURABILITY("durability.data", NUMBER, 27, "usefulbuilderswand.set.meta.durability"),
	FEATURE_META_DURABILITY_MAX("durability.max", NUMBER, 27, "usefulbuilderswand.set.settings.durabilitymax"),
	FEATURE_META_DURABILITY_BLOCKCOUNT("durability.blockcount", FLAG, false, "usefulbuilderswand.set.settings.blockcount"),
	FEATURE_META_COUNT_PLACE("count.place", NUMBER, 0, "usefulbuilderswand.set.statistics.countplace"),
	FEATURE_META_COUNT_USE("count.use", NUMBER, 0, "usefulbuilderswand.set.statistics.countuse"),
	FEATURE_META_PARTICLE_COLOR_R("particle.color.r", NUMBER, 255, "usefulbuilderswand.set.appearance.color"),
	FEATURE_META_PARTICLE_COLOR_G("particle.color.g", NUMBER, 255, "usefulbuilderswand.set.appearance.color"),
	FEATURE_META_PARTICLE_COLOR_B("particle.color.b", NUMBER, 255, "usefulbuilderswand.set.appearance.color"),
	FEATURE_META_PARTICLE_SHARE("particle.share", FLAG, true, "usefulbuilderswand.set.appearance.share"),
	FEATURE_META_OWNER("owner.data", FLAG, false, "usefulbuilderswand.set.owner.manage"),
	FEATURE_META_OWNER_ID("owner.id", TEXT, "", "usefulbuilderswand.set.owner.manage"),
	;

	public static final String FEATURE_META = "feature.meta";
	public final String key;
	public final String path;
	public final WandItemMetaType type;
	public final Object defaultValue;
	public final String permission;
	private Features(final String key, final WandItemMetaType type, final Object defaultValue, final String permission) {
		this.key = StringUtils.substringBeforeLast(key, ".data");
		this.path = FEATURE_META+"."+key;
		this.type = type;
		this.defaultValue = defaultValue;
		this.permission = permission;
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
