package net.teamfruit.ubw.meta;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class WandFeatureRegistry {
	private static final List<WandFeature<?>> registry = Lists.newArrayList();

	public static <T extends WandFeature<?>> T register(final T feature) {
		registry.add(feature);
		return feature;
	}

	public static WandFeature<String> FEATURE_META_NAME = register(WandFeature.textFeature("name", ""));
	public static WandFeature<Integer> FEATURE_META_SIZE = register(WandFeature.numberFeature("size", 9));
	public static WandFeature<Boolean> FEATURE_META_MODE = register(WandFeature.flagFeature("mode", false));
	public static WandFeature<Integer> FEATURE_META_DURABILITY = register(WandFeature.numberFeature("durability.data", 27));
	public static WandFeature<Integer> FEATURE_META_DURABILITY_MAX = register(WandFeature.numberFeature("durability.max", 27));
	public static WandFeature<Boolean> FEATURE_META_DURABILITY_BLOCKCOUNT = register(WandFeature.flagFeature("durability.blockcount", false));
	public static WandFeature<Integer> FEATURE_META_COUNT_PLACE = register(WandFeature.numberFeature("count.place", 0));
	public static WandFeature<Integer> FEATURE_META_COUNT_USE = register(WandFeature.numberFeature("count.use", 0));
	public static WandFeature<Integer> FEATURE_META_PARTICLE_COLOR_R = register(WandFeature.numberFeature("particle.color.r", 255));
	public static WandFeature<Integer> FEATURE_META_PARTICLE_COLOR_G = register(WandFeature.numberFeature("particle.color.g", 255));
	public static WandFeature<Integer> FEATURE_META_PARTICLE_COLOR_B = register(WandFeature.numberFeature("particle.color.b", 255));
	public static WandFeature<Boolean> FEATURE_META_PARTICLE_SHARE = register(WandFeature.flagFeature("particle.share", true));
	public static WandFeature<Boolean> FEATURE_META_OWNER = register(WandFeature.flagFeature("owner.data", false));
	public static WandFeature<String> FEATURE_META_OWNER_ID = register(WandFeature.textFeature("owner.id", ""));

	/*
	("", NUMBER, , "usefulbuilderswand.set.settings.size"),
	("", FLAG, , "usefulbuilderswand.set.meta.mode"),
	("", NUMBER, , "usefulbuilderswand.set.meta.durability"),
	("", NUMBER, , "usefulbuilderswand.set.settings.durabilitymax"),
	("", FLAG, , "usefulbuilderswand.set.settings.blockcount"),
	("", NUMBER, , "usefulbuilderswand.set.statistics.countplace"),
	("", NUMBER, , "usefulbuilderswand.set.statistics.countuse"),
	("", NUMBER, , "usefulbuilderswand.set.appearance.color"),
	("", NUMBER, , "usefulbuilderswand.set.appearance.color"),
	("", NUMBER, , "usefulbuilderswand.set.appearance.color"),
	("", FLAG, , "usefulbuilderswand.set.appearance.share"),
	("", FLAG, , "usefulbuilderswand.set.owner.manage"),
	("", TEXT, , "usefulbuilderswand.set.owner.manage"),
	;
	*/

	public static List<WandFeature<?>> getFeatures() {
		return registry;
	}

	@SuppressWarnings("unchecked")
	public static <T> WandFeature<T> getFeature(final String path) {
		for (final WandFeature<?> feature : getFeatures())
			if (StringUtils.equals(feature.path, path))
				return (WandFeature<T>) feature;
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> WandFeature<T> getFeatureKey(final String key) {
		for (final WandFeature<?> feature : getFeatures())
			if (StringUtils.equals(feature.key, key))
				return (WandFeature<T>) feature;
		return null;
	}

	/**
	 * 	public static WandProperty<String> FEATURE_META_NAME = new WandFeatures<String>("name", TEXT, "", "usefulbuilderswand.set.settings.name");
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

	 */
}
