package net.teamfruit.ubw.api;

public interface WandItemEditor {

	WandItemProperty<String> ownerIdProperty();

	WandItemProperty<Boolean> ownerEnabledProperty();

	WandItemProperty<Boolean> particleShareProperty();

	WandItemProperty<Integer> particleBlueProperty();

	WandItemProperty<Integer> particleGreenProperty();

	WandItemProperty<Integer> particleRedProperty();

	WandItemProperty<Integer> countUseProperty();

	WandItemProperty<Integer> countPlaceProperty();

	WandItemProperty<Boolean> durabilityCountTypeProperty();

	WandItemProperty<Integer> durabilityMaxProperty();

	WandItemProperty<Integer> durabilityProperty();

	WandItemProperty<Boolean> placeTypeProperty();

	WandItemProperty<Integer> sizeProperty();

	WandItemProperty<String> nameProperty();

}
