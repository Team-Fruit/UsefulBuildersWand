package net.teamfruit.usefulbuilderswand;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public interface WorldGuardHandler {
	boolean canBuild(Player player, Location loc);

	boolean canBuild(Player player, Block target);

	public static class WrappedWorldGuardHandler implements WorldGuardHandler {
		private final WorldGuardPlugin worldguard;

		public WrappedWorldGuardHandler(final WorldGuardPlugin worldguard) {
			this.worldguard = worldguard;
		}

		public boolean canBuild(final Player player, final Location loc) {
			return worldguard.canBuild(player, loc);
		}

		public boolean canBuild(final Player player, final Block block) {
			return worldguard.canBuild(player, block);
		}
	}

	public static class DummyWorldGuardHandler implements WorldGuardHandler {
		public boolean canBuild(final Player player, final Location loc) {
			return true;
		}

		public boolean canBuild(final Player player, final Block target) {
			return true;
		}
	}

	public static class Factory {
		public static WorldGuardHandler create(final Plugin plugin) {
			final Plugin worldguard = plugin.getServer().getPluginManager().getPlugin("WorldGuard");

			// WorldGuard may not be loaded
			if (worldguard==null||!(worldguard instanceof WorldGuardPlugin))
				return new DummyWorldGuardHandler(); // Maybe you want throw an exception instead

			return new WrappedWorldGuardHandler((WorldGuardPlugin) worldguard);
		}
	}
}
