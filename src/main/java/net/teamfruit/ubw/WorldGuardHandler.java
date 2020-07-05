package net.teamfruit.ubw;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface WorldGuardHandler {
    boolean canBuild(Player player, Location loc) throws WorldGuardHandleException;

    public static class WrappedWorldGuardHandler implements WorldGuardHandler {
        private final WorldGuardPlugin worldguardplugin;
        private final WorldGuard worldguard;

        public WrappedWorldGuardHandler(WorldGuardPlugin worldguardplugin, final WorldGuard worldguard) {
            this.worldguardplugin = worldguardplugin;
            this.worldguard = worldguard;
        }

        public boolean canBuild(final Player player, final Location loc) throws WorldGuardHandleException {
            try {
                LocalPlayer localPlayer = worldguardplugin.wrapPlayer(player);
                if (worldguard.getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld()))
                    return true;
                com.sk89q.worldedit.util.Location location = BukkitAdapter.adapt(loc);
                if (worldguard.getPlatform().getRegionContainer().createQuery().testState(location, localPlayer, Flags.BUILD))
                    return true;
                return false;
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
            try {
                return new WrappedWorldGuardHandler(WorldGuardPlugin.inst(), WorldGuard.getInstance());
            } catch (NoClassDefFoundError e) {
                // WorldGuard may not be loaded
                return new DummyWorldGuardHandler(); // Maybe you want throw an exception instead
            }
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
