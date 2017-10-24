package net.teamfruit.ubw.api;

import javax.annotation.Nonnull;

/**
 * Wand attribute property
 * @param <T> attribute type
 * @author TeamFruit
 */
public interface WandProperty<T> {

	/**
	 * Short key of feature.
	 * <p>
	 * This value is item-independent
	 * @return Short key
	 */
	@Nonnull
	String getKey();

	/**
	 * Full path of feature.
	 * <p>
	 * This path is used for internal NBT key and config.
	 * <br>
	 * This value is item-independent
	 * @return Full path
	 */
	@Nonnull
	String getPath();

	/**
	 * Get default value.
	 * <p>
	 * This value is item-independent
	 * @return default value
	 */
	T getDefaultValue();

}