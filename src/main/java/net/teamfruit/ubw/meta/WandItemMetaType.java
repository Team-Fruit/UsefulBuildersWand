package net.teamfruit.ubw.meta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class WandItemMetaType<T> {
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