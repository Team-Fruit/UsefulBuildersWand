package net.teamfruit.ubw.api.impl;

import org.bukkit.inventory.ItemStack;

import net.teamfruit.ubw.WandItemStage;
import net.teamfruit.ubw.api.WandItemEditor;
import net.teamfruit.ubw.api.WandItemProperty;
import net.teamfruit.ubw.meta.WandFeature;

public class WandItemEditorImpl implements WandItemEditor {
	private WandItemStage stage;

	public WandItemEditorImpl(final ItemStack itemStack) {
		this.stage = new WandItemStage();
		this.stage.setItem(itemStack);
		if (!this.stage.isItem())
			throw new IllegalArgumentException("This itemstack is null or empty");
	}

	@Override
	public <T> WandItemProperty<T> property(final WandFeature<T> feature) {
		return feature.property(this.stage.meta());
	}
}
