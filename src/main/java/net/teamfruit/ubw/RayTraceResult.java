package net.teamfruit.ubw;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

/**
 * Block Location and Face
 *
 * @author TeamFruit
 */
public class RayTraceResult {
	/**
	 * Location
	 */
	public final @Nonnull Location location;
	/**
	 * Face
	 */
	public final @Nonnull BlockFace face;

	public RayTraceResult(final @Nonnull Location location, final @Nonnull BlockFace face) {
		this.location = location;
		this.face = face;
	}
}