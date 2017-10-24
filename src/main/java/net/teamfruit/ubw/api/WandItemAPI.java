package net.teamfruit.ubw.api;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

/**
 * Create a wand / Check if the item is a wand / Change wand attribute API
 *
 * @author TeamFruit
 */
public interface WandItemAPI {

	/**
	 * Check if the item is a wand
	 * @param itemStack check target
	 * @return true if the item is a wand
	 * @throws IllegalArgumentException itemStack is null or empty
	 */
	boolean isWand(@Nonnull ItemStack itemStack) throws IllegalArgumentException;

	/**
	 * Create a wand / Grant the ability of a wand to an item
	 * @param itemStack grant target
	 * @return true if successful
	 * @throws IllegalArgumentException itemStack is null or empty
	 */
	boolean activateWand(@Nonnull ItemStack itemStack) throws IllegalArgumentException;

	/**
	 * Change wand attribute
	 * <p>
	 * Returns an object for changing the attribute of the wand
	 * @param itemStack edit target
	 * @return object for changing the attribute
	 * @throws IllegalArgumentException itemStack is null or empty or <b>item not activated</b><br>Please activate the wand before editing.
	 */
	@Nonnull
	WandItemEditor editWand(@Nonnull ItemStack itemStack) throws IllegalArgumentException;

}