package net.teamfruit.ubw.api;

/**
 * Wand attribute property of item
 * @param <T> attribute type
 * @author TeamFruit
 */
public interface WandItemProperty<T> extends WandProperty<T> {

	/**
	 * Set value to this property.
	 * <p>
	 * This method is item-dependent
	 * @param value value of property
	 */
	void setValue(T value);

	/**
	 * Get value of this property.
	 * <p>
	 * This value is item-dependent
	 * @return value of property
	 */
	T getValue();

}