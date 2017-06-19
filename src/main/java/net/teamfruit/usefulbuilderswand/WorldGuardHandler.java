package net.teamfruit.usefulbuilderswand;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public interface WorldGuardHandler {
	boolean canBuild(Player player, Location loc) throws WorldGuardHandleException;

	boolean canBuild(Player player, Block target) throws WorldGuardHandleException;

	public static class WrappedWorldGuardHandler implements WorldGuardHandler {
		private final WorldGuardPlugin worldguard;

		public WrappedWorldGuardHandler(final WorldGuardPlugin worldguard) {
			this.worldguard = worldguard;
		}

		public boolean canBuild(final Player player, final Location loc) throws WorldGuardHandleException {
			try {
				return worldguard.canBuild(player, loc);
			} catch (final Throwable e) {
				throw new WorldGuardHandleException(e);
			}
		}

		public boolean canBuild(final Player player, final Block block) throws WorldGuardHandleException {
			try {
				return worldguard.canBuild(player, block);
			} catch (final Throwable e) {
				throw new WorldGuardHandleException(e);
			}
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

	public static class WorldGuardHandleException extends Exception {
		public WorldGuardHandleException() {
			super();
		}

		public WorldGuardHandleException(final String message, final Throwable cause) {
			super(message, cause);
		}

		public WorldGuardHandleException(final String message) {
			super(message);
		}

		public WorldGuardHandleException(final Throwable cause) {
			super(cause);
		}
	}
}
