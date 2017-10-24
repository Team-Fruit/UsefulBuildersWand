package net.teamfruit.ubw.meta;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

public class WandCompoundMeta {
	private static class WandReadableCompoundMeta implements IWandMeta {
		private final List<IWandMeta> compound;

		private WandReadableCompoundMeta(final List<IWandMeta> compound) {
			this.compound = compound;
		}

		@Override
		public WandItemMetaType getType(final String path) {
			for (final IWandMeta meta : this.compound) {
				final WandItemMetaType type = meta.getType(path);
				if (type!=null)
					return type;
			}
			return null;
		}

		@Override
		public @Nullable Integer getNumber(final String key) {
			for (final IWandMeta meta : this.compound) {
				final Integer value = meta.getNumber(key);
				if (value!=null)
					return value;
			}
			return null;
		}

		@Override
		public @Nullable String getText(final String key) {
			for (final IWandMeta meta : this.compound) {
				final String value = meta.getText(key);
				if (value!=null)
					return value;
			}
			return null;
		}

		@Override
		public @Nullable Boolean getFlag(final String key) {
			for (final IWandMeta meta : this.compound) {
				final Boolean value = meta.getFlag(key);
				if (value!=null)
					return value;
			}
			return null;
		}
	}

	private static class WandWritableCompoundMeta extends WandReadableCompoundMeta implements IWandWritableMeta {
		private final IWandWritableMeta writable;

		private WandWritableCompoundMeta(final IWandWritableMeta writable, final List<IWandMeta> compound) {
			super(compound);
			this.writable = writable;
		}

		@Override
		public void setNumber(final String key, final Integer value) {
			this.writable.setNumber(key, value);
		}

		@Override
		public void setText(final String key, final String value) {
			this.writable.setText(key, value);
		}

		@Override
		public void setFlag(final String key, final Boolean value) {
			this.writable.setFlag(key, value);
		}
	}

	@Deprecated
	public static @Nonnull IWandMeta of(final IWandMeta... metas) {
		return new WandReadableCompoundMeta(Lists.newArrayList(metas));
	}

	public static @Nonnull IWandWritableMeta writableof(final IWandWritableMeta writable, final IWandMeta... metas) {
		final List<IWandMeta> metalist = Lists.newArrayList(metas);
		metalist.add(0, writable);
		return new WandWritableCompoundMeta(writable, metalist);
	}
}