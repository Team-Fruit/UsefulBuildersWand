package net.teamfruit.ubw.api;

import net.teamfruit.ubw.meta.WandFeature;

public interface WandItemEditor {

	<T> WandItemProperty<T> property(WandFeature<T> feature);

}
