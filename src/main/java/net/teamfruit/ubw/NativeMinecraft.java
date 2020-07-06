package net.teamfruit.ubw;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public interface NativeMinecraft {
	int getVersion();

	int getDropData(final Block block);

	ItemStack getItemFromBlock(final Block block);

	boolean placeItem(final Player player, final Block block, final ItemStackHolder handItemStack, final ItemStack placeItemStack, final BlockFace face, final Location eyeLocation);

	void playSound(final Player player, final Location location, final Block block, float volume, float pitch);

	boolean canPlace(final Block block);

	boolean canReplace(final Block block);

	boolean hasSubType(final ItemStack itemStack);

	RayTraceResult rayTrace(final Player player);

	void spawnParticles(final Player player, final Location loc, float r, float g, float b);

	boolean isMainHand(final PlayerInteractEvent event);

	ItemStack getItemInHand(final PlayerInventory inventory);

	void setItemInHand(final PlayerInventory inventory, final ItemStack itemStack);

	class NativeMinecraftFactory {
		// Tested Versions: "v1_7_R4", "v1_8_R3", "v1_9_R2", "v1_10_R1", "v1_11_R1", "v1_12_R1", "v1_15_R1"
		public static NativeMinecraft create(final Plugin plugin) {
			final Class<?> $class = plugin.getServer().getClass();
			final String fullname = $class.getPackage().getName();
			final String vername = StringUtils.substringAfterLast(fullname, ".");
			final Logger logger = plugin.getLogger();

			try {
				if (StringUtils.startsWith(vername, "v1_15")) {
					logger.info("applying v1_15_R1 -> [" + vername + "]");
					return new NativeMinecraft_v1_15_R1(vername);
				} else if (StringUtils.startsWith(vername, "v1_12")) {
					logger.info("applying v1_12_R1 -> ["+vername+"]");
					return new NativeMinecraft_v1_12_R1(vername);
				} else if (StringUtils.startsWith(vername, "v1_11")) {
					logger.info("applying v1_11_R1 -> ["+vername+"]");
					return new NativeMinecraft_v1_11_R1(vername);
				} else if (StringUtils.startsWith(vername, "v1_10")) {
					logger.info("applying v1_10_R1 -> ["+vername+"]");
					return new NativeMinecraft_v1_10_R1(vername);
				} else if (StringUtils.startsWith(vername, "v1_9")) {
					logger.info("applying v1_10_R1 -> ["+vername+"]");
					return new NativeMinecraft_v1_10_R1(vername);
				} else if (StringUtils.startsWith(vername, "v1_8")) {
					logger.info("applying v1_8_R3 -> ["+vername+"]");
					return new NativeMinecraft_v1_8_R3(vername);
				} else if (StringUtils.startsWith(vername, "v1_7")) {
					logger.info("applying v1_7_R4 -> ["+vername+"]");
					return new NativeMinecraft_v1_7_R4(vername);
				} else {
					logger.warning("###### UNSUPPORTED MINECRAFT VERSION ######");
					logger.warning("# Internal Version: "+StringUtils.rightPad(vername, "INECRAFT VERSION ####".length())+" #");
					logger.warning("#                                         #");
					logger.warning("# I will do my best to work, but          #");
					logger.warning("#  there is a fear that it will not work. #");
					logger.warning("#                                         #");
					logger.warning("# 1.7.x 1.8.x 1.9.x 1.10.x 1.11.x 1.12.x is Supported. #");
					logger.warning("# 1.7.10 1.8.9 1.9.4 1.10.2 1.11.2 1.12 is Verified.  #");
					logger.warning("###########################################");
				}
			} catch (final Exception e) {
				logger.warning("********** Problem has occurred. **********");
				logger.warning("  Internal Version: "+vername);
				logger.warning("  About Error: "+e.getMessage());
				logger.warning("                                           ");
				logger.warning("  I will do my best to work, but           ");
				logger.warning("   there is a fear that it will not work.  ");
				logger.warning("                                           ");
				logger.warning("  This Minecraft version is supported,     ");
				logger.warning("  However, because it is different from    ");
				logger.warning("    the structure we imagined, it can not  ");
				logger.warning("    be operated normally.                  ");
				logger.warning("                                           ");
				logger.warning("  1.7.x 1.8.x 1.9.x 1.10.x 1.11.x 1.12.x 1.15.x is Supported.  ");
				logger.warning("  1.7.10 1.8.9 1.9.4 1.10.2 1.11.2 1.12.2 1.15.2 is Verified.   ");
				logger.warning("                                           ");
				logger.warning("  Please report 'Internal Version' and     ");
				logger.warning("    'About Error' to us!                   ");
				logger.warning("*******************************************");
				logger.log(Level.WARNING, e.getMessage(), e);
			}

			try {
				logger.warning("trying to apply v1_15_R1 -> ["+vername+"]");
				return new NativeMinecraft_v1_15_R1(vername);
			} catch (final Exception e) {
				logger.log(Level.FINEST, e.getMessage(), e);
				logger.warning("failed to apply v1_15_R1");
			}
			try {
				logger.warning("trying to apply v1_12_R1 -> ["+vername+"]");
				return new NativeMinecraft_v1_12_R1(vername);
			} catch (final Exception e) {
				logger.log(Level.FINEST, e.getMessage(), e);
				logger.warning("failed to apply v1_12_R1");
			}
			try {
				logger.warning("trying to apply v1_11_R1 -> ["+vername+"]");
				return new NativeMinecraft_v1_11_R1(vername);
			} catch (final Exception e) {
				logger.log(Level.FINEST, e.getMessage(), e);
				logger.warning("failed to apply v1_11_R1");
			}
			try {
				logger.warning("trying to apply v1_10_R1 -> ["+vername+"]");
				return new NativeMinecraft_v1_10_R1(vername);
			} catch (final Exception e) {
				logger.log(Level.FINEST, e.getMessage(), e);
				logger.warning("failed to apply v1_10_R1");
			}
			try {
				logger.warning("trying to apply v1_8_R3 -> ["+vername+"]");
				return new NativeMinecraft_v1_8_R3(vername);
			} catch (final Exception e) {
				logger.log(Level.FINEST, e.getMessage(), e);
				logger.warning("failed to apply v1_8_R3");
			}
			try {
				logger.warning("trying to apply v1_7_R4 -> ["+vername+"]");
				return new NativeMinecraft_v1_7_R4(vername);
			} catch (final Exception e) {
				logger.log(Level.FINEST, e.getMessage(), e);
				logger.warning("failed to apply v1_7_R4");
			}
			logger.log(Level.SEVERE, "FATAL ERROR: Sorry this plugin doesn't work with this enviroment.");
			throw new RuntimeException("FATAL ERROR: UNSUPPORTED");
		}
	}
}
