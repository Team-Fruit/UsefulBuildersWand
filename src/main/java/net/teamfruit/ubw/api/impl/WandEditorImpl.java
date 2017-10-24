package net.teamfruit.ubw.api.impl;

import org.bukkit.inventory.ItemStack;

import net.teamfruit.ubw.WandData;
import net.teamfruit.ubw.api.WandEditor;
import net.teamfruit.ubw.meta.Features;
import net.teamfruit.ubw.meta.IWandMeta;
import net.teamfruit.ubw.meta.WandItem;
import net.teamfruit.ubw.meta.WandItemMeta;
import net.teamfruit.ubw.meta.WandMetaUtils;

public class WandEditorImpl implements WandEditor {
	private final WandData wanddata;
	private final WandItem witem;
	private WandItemMeta wmeta;
	private IWandMeta defmeta;
	private IWandMeta meta;

	public WandEditorImpl(final WandData wanddata, final ItemStack itemStack) {
		this.wanddata = wanddata;
		if (!WandItem.isItem(itemStack))
			throw new IllegalArgumentException("null itemstack");
		this.witem = WandItem.newWandItem(itemStack);
		this.wmeta = this.witem.getMeta();
		this.defmeta = this.wanddata.configMeta();
		this.meta = this.wanddata.wrapMeta(this.wmeta);
	}

	public WandProperty<Integer> size = new WandProperty<Integer>(Features.FEATURE_META_SIZE);
	public WandProperty<Boolean> mode = new WandProperty<Boolean>(Features.FEATURE_META_MODE);

	public class WandProperty<T> {
		private final Features ft;

		public WandProperty(final Features ft) {
			this.ft = ft;
		}

		public void set(final T value) {
			WandMetaUtils.set(WandEditorImpl.this.wmeta, this.ft, value);
		}

		@SuppressWarnings("unchecked")
		public T get() {
			return (T) WandMetaUtils.get(WandEditorImpl.this.meta, this.ft);
		}

		public void setToDefault() {
			set(getDefault());
		}

		@SuppressWarnings("unchecked")
		public T getDefault() {
			return (T) WandMetaUtils.get(WandEditorImpl.this.defmeta, this.ft);
		}
	}

	/*
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
	*/
}
