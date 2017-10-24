package net.teamfruit.ubw.api;

import javax.annotation.Nonnull;

/**
 * API Terminal
 * <p>
 * Select API you want to use.
 * <p>
 * Some features may <b>not be initialized</b> before this plugin <code>onEnable</code> phase
 * <br>
 * <b>Do not use these features</b> before the <code>onEnable</code> phase completes.
 *
 * @author TeamFruit
 */
public interface UsefulBuildersWandAPI {

	/**
	 * Create a wand / Check if the item is a wand / Change wand attribute API
	 * @return item API object.
	 * @throws IllegalStateException before initialized.
	 */
	@Nonnull
	WandItemAPI itemAPI() throws IllegalStateException;

}
