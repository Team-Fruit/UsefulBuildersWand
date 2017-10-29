package net.teamfruit.ubw.api;

/**
 * Wand attribute read-only property of item
 * @param <T> attribute type
 * @author TeamFruit
 */
public interface WandItemReadOnlyProperty<T> {

	/**
	 * Get value of this property.
	 * <p>
	 * This value is item-dependent
	 * @return value of property
	 */
	T getValue();

}