package net.teamfruit.ubw.api.impl;

import org.bukkit.inventory.ItemStack;

import net.teamfruit.ubw.WandData;
import net.teamfruit.ubw.WandItemStage;
import net.teamfruit.ubw.api.WandItemEditor;
import net.teamfruit.ubw.api.WandItemProperty;
import net.teamfruit.ubw.meta.Features;
import net.teamfruit.ubw.meta.IWandMeta;
import net.teamfruit.ubw.meta.WandMetaUtils;

public class WandItemEditorImpl implements WandItemEditor {
	private final WandData wanddata;
	private WandItemStage stage;
	private IWandMeta defmeta;

	public WandItemEditorImpl(final WandData wanddata, final ItemStack itemStack) {
		this.wanddata = wanddata;
		this.stage = new WandItemStage(wanddata);
		this.stage.setItem(itemStack);
		if (!this.stage.isItem())
			throw new IllegalArgumentException("This itemstack is null or empty");
		this.defmeta = this.wanddata.configMeta();
	}

	private final WandItemProperty<String> NAME = new WandItemPropertyImpl<String>(Features.FEATURE_META_NAME);
	private final WandItemProperty<Integer> SIZE = new WandItemPropertyImpl<Integer>(Features.FEATURE_META_SIZE);
	private final WandItemProperty<Boolean> MODE = new WandItemPropertyImpl<Boolean>(Features.FEATURE_META_MODE);
	private final WandItemProperty<Integer> DURABILITY = new WandItemPropertyImpl<Integer>(Features.FEATURE_META_DURABILITY);
	private final WandItemProperty<Integer> DURABILITY_MAX = new WandItemPropertyImpl<Integer>(Features.FEATURE_META_DURABILITY_MAX);
	private final WandItemProperty<Boolean> DURABILITY_BLOCKCOUNT = new WandItemPropertyImpl<Boolean>(Features.FEATURE_META_DURABILITY_BLOCKCOUNT);
	private final WandItemProperty<Integer> COUNT_PLACE = new WandItemPropertyImpl<Integer>(Features.FEATURE_META_COUNT_PLACE);
	private final WandItemProperty<Integer> COUNT_USE = new WandItemPropertyImpl<Integer>(Features.FEATURE_META_COUNT_USE);
	private final WandItemProperty<Integer> PARTICLE_COLOR_R = new WandItemPropertyImpl<Integer>(Features.FEATURE_META_PARTICLE_COLOR_R);
	private final WandItemProperty<Integer> PARTICLE_COLOR_G = new WandItemPropertyImpl<Integer>(Features.FEATURE_META_PARTICLE_COLOR_G);
	private final WandItemProperty<Integer> PARTICLE_COLOR_B = new WandItemPropertyImpl<Integer>(Features.FEATURE_META_PARTICLE_COLOR_B);
	private final WandItemProperty<Boolean> PARTICLE_SHARE = new WandItemPropertyImpl<Boolean>(Features.FEATURE_META_PARTICLE_SHARE);
	private final WandItemProperty<Boolean> OWNER = new WandItemPropertyImpl<Boolean>(Features.FEATURE_META_OWNER);
	private final WandItemProperty<String> OWNER_ID = new WandItemPropertyImpl<String>(Features.FEATURE_META_OWNER_ID);

	@Override
	public WandItemProperty<String> nameProperty() {
		return this.NAME;
	}

	@Override
	public WandItemProperty<Integer> sizeProperty() {
		return this.SIZE;
	}

	@Override
	public WandItemProperty<Boolean> placeTypeProperty() {
		return this.MODE;
	}

	@Override
	public WandItemProperty<Integer> durabilityProperty() {
		return this.DURABILITY;
	}

	@Override
	public WandItemProperty<Integer> durabilityMaxProperty() {
		return this.DURABILITY_MAX;
	}

	@Override
	public WandItemProperty<Boolean> durabilityCountTypeProperty() {
		return this.DURABILITY_BLOCKCOUNT;
	}

	@Override
	public WandItemProperty<Integer> countPlaceProperty() {
		return this.COUNT_PLACE;
	}

	@Override
	public WandItemProperty<Integer> countUseProperty() {
		return this.COUNT_USE;
	}

	@Override
	public WandItemProperty<Integer> particleRedProperty() {
		return this.PARTICLE_COLOR_R;
	}

	@Override
	public WandItemProperty<Integer> particleGreenProperty() {
		return this.PARTICLE_COLOR_G;
	}

	@Override
	public WandItemProperty<Integer> particleBlueProperty() {
		return this.PARTICLE_COLOR_B;
	}

	@Override
	public WandItemProperty<Boolean> particleShareProperty() {
		return this.PARTICLE_SHARE;
	}

	@Override
	public WandItemProperty<Boolean> ownerEnabledProperty() {
		return this.OWNER;
	}

	@Override
	public WandItemProperty<String> ownerIdProperty() {
		return this.OWNER_ID;
	}

	public class WandItemPropertyImpl<T> implements WandItemProperty<T> {
		private final Features ft;

		public WandItemPropertyImpl(final Features ft) {
			this.ft = ft;
		}

		@Override
		public String getKey() {
			return this.ft.key;
		}

		@Override
		public String getPath() {
			return this.ft.path;
		}

		@Override
		public void setValue(final T value) {
			WandMetaUtils.set(WandItemEditorImpl.this.stage.meta(), this.ft, value);
		}

		@Override
		@SuppressWarnings("unchecked")
		public T getValue() {
			return (T) WandMetaUtils.get(WandItemEditorImpl.this.stage.meta(), this.ft);
		}

		@Override
		@SuppressWarnings("unchecked")
		public T getDefaultValue() {
			return (T) WandMetaUtils.get(WandItemEditorImpl.this.defmeta, this.ft);
		}
	}
}
