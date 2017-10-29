package net.teamfruit.ubw.meta;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class WandFeatureRegistry {
	private static final FeatureCategory rootCategory = new FeatureCategory(null);

	public static <T> WandFeature<T> register(final WandFeature<T> feature) {
		rootCategory.register(feature);
		return feature;
	}

	public static FeatureCategory getRootCategory() {
		return rootCategory;
	}

	public static final FeatureAttribute<Object> ATTRIBUTE_DEFAULT = FeatureAttribute.attribute("attribute.type.default");

	public static final WandFeature<String> FEATURE_META_NAME = rootCategory.register(WandFeature.textFeature("name")).setAttribute(ATTRIBUTE_DEFAULT, "").feature();
	public static final WandFeature<Integer> FEATURE_META_SIZE = rootCategory.register(WandFeature.numberFeature("size")).setAttribute(ATTRIBUTE_DEFAULT, 9).feature();
	public static final WandFeature<Boolean> FEATURE_META_MODE = rootCategory.register(WandFeature.flagFeature("mode")).setAttribute(ATTRIBUTE_DEFAULT, false).feature();
	public static final WandFeature<Integer> FEATURE_META_DURABILITY = rootCategory.register(WandFeature.numberFeature("durability.data")).setAttribute(ATTRIBUTE_DEFAULT, 27).feature();
	public static final WandFeature<Integer> FEATURE_META_DURABILITY_MAX = rootCategory.register(WandFeature.numberFeature("durability.max")).setAttribute(ATTRIBUTE_DEFAULT, 27).feature();
	public static final WandFeature<Boolean> FEATURE_META_DURABILITY_BLOCKCOUNT = rootCategory.register(WandFeature.flagFeature("durability.blockcount")).setAttribute(ATTRIBUTE_DEFAULT, false).feature();
	public static final WandFeature<Integer> FEATURE_META_COUNT_PLACE = rootCategory.register(WandFeature.numberFeature("count.place")).setAttribute(ATTRIBUTE_DEFAULT, 0).feature();
	public static final WandFeature<Integer> FEATURE_META_COUNT_USE = rootCategory.register(WandFeature.numberFeature("count.use")).setAttribute(ATTRIBUTE_DEFAULT, 0).feature();
	public static final WandFeature<Integer> FEATURE_META_PARTICLE_COLOR_R = rootCategory.register(WandFeature.numberFeature("particle.color.r")).setAttribute(ATTRIBUTE_DEFAULT, 255).feature();
	public static final WandFeature<Integer> FEATURE_META_PARTICLE_COLOR_G = rootCategory.register(WandFeature.numberFeature("particle.color.g")).setAttribute(ATTRIBUTE_DEFAULT, 255).feature();
	public static final WandFeature<Integer> FEATURE_META_PARTICLE_COLOR_B = rootCategory.register(WandFeature.numberFeature("particle.color.b")).setAttribute(ATTRIBUTE_DEFAULT, 255).feature();
	public static final WandFeature<Boolean> FEATURE_META_PARTICLE_SHARE = rootCategory.register(WandFeature.flagFeature("particle.share")).setAttribute(ATTRIBUTE_DEFAULT, true).feature();
	public static final WandFeature<Boolean> FEATURE_META_OWNER = rootCategory.register(WandFeature.flagFeature("owner.data")).setAttribute(ATTRIBUTE_DEFAULT, false).feature();
	public static final WandFeature<String> FEATURE_META_OWNER_ID = rootCategory.register(WandFeature.textFeature("owner.id")).setAttribute(ATTRIBUTE_DEFAULT, "").feature();

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

	public abstract static class FeatureAttribute<T> {
		private FeatureAttribute() {
		}

		private static class ObjectFeatureAttribute<T> extends FeatureAttribute<T> {
			private static final Map<Object, Class<?>> registeredTypes = Maps.newHashMap();

			private final Object key;

			public ObjectFeatureAttribute(final Object key, final T... emptyParamToCheckType) {
				Validate.notNull(emptyParamToCheckType, "Do not pass anything to the variable array. (null value)");
				final Class<?> clazz1 = emptyParamToCheckType.getClass().getComponentType();
				Validate.notNull(clazz1, "Do not pass anything to the variable array. (non-array value)");
				final Class<?> clazz2 = registeredTypes.get(key);
				if (clazz2!=null) {
					if (!clazz2.equals(clazz1))
						throw new IllegalArgumentException("This key is registered as a different type");
				} else
					registeredTypes.put(key, clazz1);
				this.key = key;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime*result+(this.key==null ? 0 : this.key.hashCode());
				return result;
			}

			@Override
			public boolean equals(final Object obj) {
				if (this==obj)
					return true;
				if (obj==null)
					return false;
				if (!(obj instanceof ObjectFeatureAttribute))
					return false;
				final ObjectFeatureAttribute<?> other = (ObjectFeatureAttribute<?>) obj;
				if (this.key==null) {
					if (other.key!=null)
						return false;
				} else if (!this.key.equals(other.key))
					return false;
				return true;
			}
		}

		private static class IdentityFeatureAttribute<T> extends FeatureAttribute<T> {
		}

		public static <T> FeatureAttribute<T> attribute(final Object key, final T... emptyParamToCheckType) {
			return new ObjectFeatureAttribute<T>(key, emptyParamToCheckType);
		}

		public static <T> FeatureAttribute<T> attribute() {
			return new IdentityFeatureAttribute<T>();
		}
	}

	public static class FeatureRegistryEntry<T> {
		private final Table<WandFeature<?>, FeatureAttribute<?>, Object> metatable;
		private final WandFeature<T> feature;

		private FeatureRegistryEntry(final Table<WandFeature<?>, FeatureAttribute<?>, Object> metatable, final WandFeature<T> feature) {
			this.metatable = metatable;
			this.feature = feature;
		}

		public WandFeature<T> feature() {
			return this.feature;
		}

		public <E> FeatureRegistryEntry<T> setAttribute(final FeatureAttribute<E> key, final E value) {
			this.metatable.put(this.feature, key, value);
			return this;
		}

		@SuppressWarnings("unchecked")
		public <E> E getAttribute(final FeatureAttribute<E> key) {
			return (E) this.metatable.get(this.feature, key);
		}
	}

	public static class FeatureCategory {
		private final Set<WandFeature<?>> registry = Sets.newHashSet();
		private final Table<WandFeature<?>, FeatureAttribute<?>, Object> metatable = HashBasedTable.create();
		private final FeatureCategory parent;

		private FeatureCategory(final FeatureCategory parent) {
			this.parent = parent;
		}

		public FeatureCategory createCategory() {
			return new FeatureCategory(this);
		}

		public <T> FeatureRegistryEntry<T> register(final WandFeature<T> feature) {
			if (this.parent!=null)
				this.parent.register(feature);
			this.registry.add(feature);
			return getEntry(feature);
		}

		public @Nullable <T> FeatureRegistryEntry<T> getEntry(final WandFeature<T> feature) {
			if (!this.metatable.containsRow(feature))
				return null;
			return new FeatureRegistryEntry<T>(this.metatable, feature);
		}

		@SuppressWarnings("unchecked")
		public @Nullable <T> WandFeature<T> fromPath(final String path) {
			for (final WandFeature<?> feature : this.registry)
				if (StringUtils.equals(feature.path, path))
					return (WandFeature<T>) feature;
			return null;
		}

		@SuppressWarnings("unchecked")
		public @Nullable <T> WandFeature<T> fromAttribute(final FeatureAttribute<T> attribute, final T value) {
			for (final Entry<WandFeature<?>, Object> entry : this.metatable.column(attribute).entrySet())
				if (Objects.equals(entry.getValue(), value))
					return (WandFeature<T>) entry.getKey();
			return null;
		}

		public Set<WandFeature<?>> getFeatures() {
			return Collections.unmodifiableSet(this.registry);
		}

		@SuppressWarnings("unchecked")
		public <T> Set<FeatureRegistryEntry<?>> getFeatureEntries() {
			final Set<FeatureRegistryEntry<?>> entries = Sets.newHashSet();
			for (final WandFeature<?> feature : this.registry)
				entries.add(new FeatureRegistryEntry<Object>(this.metatable, (WandFeature<Object>) feature));
			return entries;
		}
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
