package net.teamfruit.usefulbuilderswand;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class RayTraceResult {
	public final Location location;
	public final BlockFace face;

	public RayTraceResult(final Location location, final BlockFace face) {
		this.location = location;
		this.face = face;
	}
}