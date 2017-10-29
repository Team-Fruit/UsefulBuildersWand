package net.teamfruit.ubw.meta;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

import net.teamfruit.ubw.WandData;
import net.teamfruit.ubw.api.WandItemProperty;
import net.teamfruit.ubw.api.WandItemReadOnlyProperty;

public class WandFeature<T> {
	public static final String FEATURE_META = "feature.meta";
	private static final Set<String> registeredPaths = Sets.newHashSet();

	public final String key;
	public final String path;
	private final WandItemMetaType<T> type;

	private WandFeature(final String key, final WandItemMetaType<T> type) {
		this.path = FEATURE_META+"."+key;
		if (registeredPaths.contains(this.path))
			throw new IllegalStateException("The same path can be registered only once");
		registeredPaths.add(this.path);
		this.key = StringUtils.substringBeforeLast(key, ".data");
		this.type = type;
	}

	public String getKey() {
		return this.key;
	}

	public String getPath() {
		return this.path;
	}

	public T getDefaultValue() {
		return this.type.getImpl(WandData.INSTANCE.configMeta(), this.path);
	}

	public WandItemReadOnlyProperty<T> property(final IWandMeta meta) {
		return new WandItemReadOnlyPropertyImpl<T>(this.path, this.type, meta);
	}

	public WandItemProperty<T> property(final IWandWritableMeta wmeta) {
		return new WandItemPropertyImpl<T>(this.path, this.type, wmeta);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime*result+(this.path==null ? 0 : this.path.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this==obj)
			return true;
		if (obj==null)
			return false;
		if (!(obj instanceof WandFeature))
			return false;
		final WandFeature<?> other = (WandFeature<?>) obj;
		if (this.path==null) {
			if (other.path!=null)
				return false;
		} else if (!this.path.equals(other.path))
			return false;
		return true;
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

	private static class WandItemReadOnlyPropertyImpl<T> implements WandItemReadOnlyProperty<T> {
		protected final String path;
		protected final WandItemMetaType<T> type;
		protected final IWandMeta meta;

		public WandItemReadOnlyPropertyImpl(final String path, final WandItemMetaType<T> type, final IWandMeta meta) {
			this.path = path;
			this.type = type;
			this.meta = meta;
		}

		@Override
		public T getValue() {
			return this.type.getImpl(this.meta, this.path);
		}
	}

	private static class WandItemPropertyImpl<T> extends WandItemReadOnlyPropertyImpl<T> implements WandItemProperty<T> {
		private final IWandWritableMeta wmeta;

		public WandItemPropertyImpl(final String path, final WandItemMetaType<T> type, final IWandWritableMeta wmeta) {
			super(path, type, wmeta);
			this.wmeta = wmeta;
		}

		@Override
		public void setValue(final T value) {
			this.type.setImpl(this.wmeta, this.path, value);
		}
	}

	private static abstract class WandItemMetaType<T> {
		public static WandItemMetaType<Integer> NUMBER = new WandItemMetaType<Integer>() {
			@Override
			public void setImpl(final IWandWritableMeta meta, final String path, final Integer value) {
				meta.setNumber(path, value);
			}

			@Override
			public Integer getImpl(final IWandMeta meta, final String path) {
				return meta.getNumber(path);
			};
		};
		public static WandItemMetaType<String> TEXT = new WandItemMetaType<String>() {
			@Override
			public void setImpl(final IWandWritableMeta meta, final String path, final String value) {
				meta.setText(path, value);
			}

			@Override
			public String getImpl(final IWandMeta meta, final String path) {
				return meta.getText(path);
			}
		};
		public static WandItemMetaType<Boolean> FLAG = new WandItemMetaType<Boolean>() {
			@Override
			public void setImpl(final IWandWritableMeta meta, final String path, final Boolean value) {
				meta.setFlag(path, value);
			}

			@Override
			public Boolean getImpl(final IWandMeta meta, final String path) {
				return meta.getFlag(path);
			}
		};

		public abstract void setImpl(final @Nonnull IWandWritableMeta meta, final String path, final @Nullable T value);

		public abstract @Nullable T getImpl(final @Nonnull IWandMeta meta, final String path);
	}
}
