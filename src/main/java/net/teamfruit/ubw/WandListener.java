package net.teamfruit.ubw;

import static net.teamfruit.ubw.meta.WandFeatureRegistry.*;
import static net.teamfruit.ubw.meta.WandMetaUtils.*;

import java.util.List;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.mojang.util.UUIDTypeAdapter;

import net.teamfruit.ubw.I18n.Locale;
import net.teamfruit.ubw.WorldGuardHandler.WorldGuardHandleException;

public class WandListener implements Listener {
	private final Plugin plugin;
	private final Locale locale;
	private final WandData wanddata;
	private NativeMinecraft nativemc;
	private final WorldGuardHandler worldguard;

	public WandListener(final Plugin plugin, final Locale locale, final WandData wanddata, final NativeMinecraft nativemc) {
		this.plugin = plugin;
		this.locale = locale;
		this.wanddata = wanddata;
		this.nativemc = nativemc;
		this.worldguard = WorldGuardHandler.Factory.create(plugin);
		new BukkitRunnable() {
			@Override
			public void run() {
				onEffect();
			}
		}.runTaskTimer(this.plugin, 3, 3);
	}

	private void onEffect() {
		for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
			final WandItemStage stage = new WandItemStage(this.wanddata);
			stage.setItem(this.nativemc.getItemInHand(player.getInventory()));
			if (!stage.isItem()||!stage.isWandItem())
				continue;

			List<Location> blocks = null;
			try {
				final RayTraceResult res = this.nativemc.rayTrace(player);
				if (res!=null)
					blocks = getPotentialBlocks(stage, player, res.location.getBlock(), res.face);
			} catch (final Exception e) {
			}

			final boolean ownerenabled = or(stage.meta().getFlag(FEATURE_META_OWNER.path), false);
			String ownerid = stage.meta().getText(FEATURE_META_OWNER_ID.path);
			final String playerid = UUIDTypeAdapter.fromUUID(player.getUniqueId());
			if (ownerenabled) {
				try {
					UUIDTypeAdapter.fromString(ownerid);
				} catch (final IllegalArgumentException e) {
					final String playername = player.getName();
					if (StringUtils.equals(playername, ownerid))
						ownerid = null;
				}
				if (!(StringUtils.isEmpty(ownerid)||StringUtils.equals(playerid, ownerid)))
					return;
			}

			if (blocks!=null) {
				final int color_r = or(stage.meta().getNumber(FEATURE_META_PARTICLE_COLOR_R.path), 255);
				final int color_g = or(stage.meta().getNumber(FEATURE_META_PARTICLE_COLOR_G.path), 255);
				final int color_b = or(stage.meta().getNumber(FEATURE_META_PARTICLE_COLOR_B.path), 255);
				final int range = this.wanddata.getConfig().getInt(WandData.SETTING_EFFECT_RANGE);
				if (range>0&&or(stage.meta().getFlag(FEATURE_META_PARTICLE_SHARE.path), true)) {
					for (final Player other : Bukkit.getOnlinePlayers())
						if (other.getLocation().distance(player.getLocation())<=range)
							for (final Location block : blocks)
								this.nativemc.spawnParticles(other, block, color_r/255f, color_g/255f, color_b/255f);
				} else
					for (final Location block : blocks)
						this.nativemc.spawnParticles(player, block, color_r/255f, color_g/255f, color_b/255f);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerUse(final PlayerInteractEvent event) {
		if (!this.nativemc.isMainHand(event))
			return;

		final Player player = event.getPlayer();
		if (sendResultMessage(onPlayerUse(player, event.getAction(), event.getClickedBlock(), event.getBlockFace()), this.locale, player))
			event.setCancelled(true);
	}

	private ActionResult onPlayerUse(final Player player, final Action action, final Block target, final BlockFace face) {
		final PlayerInventory inventory = player.getInventory();
		final WandItemStage stage = new WandItemStage(this.wanddata);
		stage.setItem(this.nativemc.getItemInHand(inventory));
		if (!stage.isItem()||!stage.isWandItem())
			return ActionResult.error();
		if (stage.getItem().getAmount()>1) {
			final ItemStack[] stacks = inventory.getContents();
			final int heldslot = inventory.getHeldItemSlot();
			stackbreak: {
				final ItemStack newItemStack = stage.getItem().clone();
				newItemStack.setAmount(stage.getItem().getAmount()-1);
				stage.getItem().setAmount(1);
				for (int i = 0; i<stacks.length; i++)
					if (i!=heldslot&&stacks[i]==null) {
						inventory.setItem(i, newItemStack);
						break stackbreak;
					}
				player.getWorld().dropItem(player.getEyeLocation(), newItemStack);
			}
		}

		try {
			final boolean ownerenabled = or(stage.meta().getFlag(FEATURE_META_OWNER.path), false);
			String ownerid = stage.meta().getText(FEATURE_META_OWNER_ID.path);
			final String playerid = UUIDTypeAdapter.fromUUID(player.getUniqueId());
			if (ownerenabled) {
				try {
					UUIDTypeAdapter.fromString(ownerid);
				} catch (final IllegalArgumentException e) {
					final String playername = player.getName();
					if (StringUtils.equals(playername, ownerid))
						ownerid = null;
				}
				if (StringUtils.isEmpty(ownerid)||StringUtils.equals(playerid, ownerid))
					stage.meta().setText(FEATURE_META_OWNER_ID.path, playerid);
				else {
					Log.log.log(Level.INFO, String.format("[player=%s] tried to use protected wand of others: [player=%s, uuid=%s]", player.getName(), PlayerUUID.getName(ownerid).orElse("<unknown>"), ownerid));
					return ActionResult.error(I18n.format(this.locale, "ubw.action.error.owner"));
				}
			} else
				stage.meta().setText(FEATURE_META_OWNER_ID.path, null);

			if (target!=null&&action==Action.RIGHT_CLICK_BLOCK) {
				if (face!=null) {
					if (!this.worldguard.canBuild(player, target)) {
						Log.log.log(Level.INFO, String.format("[player=%s] tried to use wand in protected area: %s", player.getName(), target));
						return ActionResult.error(I18n.format(this.locale, "ubw.action.error.worldguard"));
					}
					if (target.isEmpty())
						return ActionResult.error();

					final int maxdurability = or(stage.meta().getNumber(FEATURE_META_DURABILITY_MAX.path), 0);

					final List<Location> blocks = getPotentialBlocks(stage, player, target, face);

					if (blocks.isEmpty())
						return ActionResult.error();

					int durability = or(stage.meta().getNumber(FEATURE_META_DURABILITY.path), 0);
					final boolean blockcount = or(stage.meta().getFlag(FEATURE_META_DURABILITY_BLOCKCOUNT.path), false);

					if (maxdurability>0&&durability<=0)
						return ActionResult.error();

					int data = -1;

					final ItemStack item1 = this.nativemc.getItemFromBlock(target);

					if (this.nativemc.hasSubType(item1))
						data = this.nativemc.getDropData(target);

					int slot = 0;
					int placecount = 0;
					for (final Location temp : blocks) {
						if (maxdurability>0&&durability<=0)
							return ActionResult.error();
						for (slot = 0; slot<inventory.getSize(); ++slot) {
							final ItemStack itemslot = inventory.getItem(slot);
							if (itemslot==null||itemslot.getAmount()==0||!itemslot.getType().equals(item1.getType()))
								continue;
							if (data==-1||data==itemslot.getDurability())
								break;
						}

						if (slot>=inventory.getSize())
							break;

						final ItemStack item = inventory.getItem(slot);
						ItemStack objitem = item;
						if (player.getGameMode()==GameMode.CREATIVE) {
							objitem = objitem.clone();
							objitem.setAmount(1);
						}

						final Block block = temp.getBlock().getRelative(face.getOppositeFace());

						final boolean placeresult = this.nativemc.placeItem(player, block, stage, objitem, EquipmentSlot.HAND, face, player.getEyeLocation());

						if (placeresult) {
							objitem.setAmount(objitem.getAmount()-1);
							if (item.getAmount()<=0)
								inventory.setItem(slot, null);
							else
								inventory.setItem(slot, item);

							this.nativemc.playSound(player, temp, target, .25f, 1f);
							placecount++;
							if (blockcount)
								durability--;
						}
					}
					if (!blockcount)
						durability--;
					stage.meta().setNumber(FEATURE_META_COUNT_PLACE.path, or(stage.meta().getNumber(FEATURE_META_COUNT_PLACE.path), 0)+placecount);
					stage.meta().setNumber(FEATURE_META_COUNT_USE.path, or(stage.meta().getNumber(FEATURE_META_COUNT_USE.path), 0)+1);
					if (maxdurability>0)
						stage.meta().setNumber(FEATURE_META_DURABILITY.path, durability);

					return ActionResult.success();
				}
			} else if (player.isSneaking()&&(action==Action.LEFT_CLICK_AIR||action==Action.LEFT_CLICK_BLOCK)) {
				stage.meta().setFlag(FEATURE_META_MODE.path, !or(stage.meta().getFlag(FEATURE_META_MODE.path), false));
				return ActionResult.success();
			}
			return ActionResult.error();
		} catch (final Throwable e) {
			final String errorcode = Long.toHexString(System.currentTimeMillis());
			Log.log.log(Level.SEVERE, String.format("[player=%s, errorcode=%s]: A fatal error has occured: ", player.getName(), errorcode), e.getCause());
			return ActionResult.error(I18n.format(this.locale, "ubw.action.error", e.getMessage()), I18n.format(this.locale, "ubw.action.error.reportcode", errorcode));
		} finally {
			stage.updateItem();
			this.nativemc.setItemInHand(inventory, stage.getItem());
		}
	}

	private List<Location> getPotentialBlocks(final WandItemStage stage, final Player player, final @Nullable Block target, final BlockFace face) throws WorldGuardHandleException {
		final List<Location> blocks = Lists.newArrayList();

		final int maxdurability = or(stage.meta().getNumber(FEATURE_META_DURABILITY_MAX.path), 0);
		final int durability = or(stage.meta().getNumber(FEATURE_META_DURABILITY.path), 0);
		final boolean blockcount = or(stage.meta().getFlag(FEATURE_META_DURABILITY_BLOCKCOUNT.path), false);

		if (maxdurability>0&&durability<=0)
			return blocks;

		final int maxBlocks = or(stage.meta().getNumber(FEATURE_META_SIZE.path), 0);

		if (target==null||target.isEmpty())
			return blocks;

		if (!this.worldguard.canBuild(player, target))
			return blocks;

		final ItemStack blockItem = this.nativemc.getItemFromBlock(target);

		int data = -1;

		if (this.nativemc.hasSubType(blockItem))
			data = this.nativemc.getDropData(target);

		int numBlocks = 0;
		final Inventory inventory = player.getInventory();
		for (int i = 0; i<inventory.getSize(); ++i) {
			final ItemStack itemStack2 = inventory.getItem(i);
			if (itemStack2!=null) {
				if (itemStack2.getType().equals(blockItem.getType())&&(data==-1||data==itemStack2.getDurability()))
					if (player.getGameMode()==GameMode.CREATIVE) {
						numBlocks = maxBlocks;
						break;
					} else
						numBlocks += itemStack2.getAmount();
				if (numBlocks>=maxBlocks) {
					numBlocks = maxBlocks;
					break;
				}
			}
		}

		final int dx = face.getModX();
		final int dy = face.getModY();
		final int dz = face.getModZ();
		int mx = dx==0 ? 1 : 0;
		int my = dy==0 ? 1 : 0;
		int mz = dz==0 ? 1 : 0;

		if (player.isSneaking()) {
			final boolean isVertical = or(stage.meta().getFlag(FEATURE_META_MODE.path), false);
			;
			if (face!=BlockFace.UP&&face!=BlockFace.DOWN)
				if (isVertical) {
					if (face!=BlockFace.NORTH&&face!=BlockFace.SOUTH)
						mz = 0;
					else
						mx = 0;
				} else
					my = 0;
			else {
				final double rotation = player.getLocation().getYaw()+360;
				final boolean b = 45*1<=rotation&&rotation<45*3||45*5<=rotation&&rotation<45*7;
				if ((b||isVertical)&&(!b||!isVertical))
					mx = 0;
				else
					mz = 0;
			}
		}

		final World world = player.getWorld();
		if (world==null)
			return blocks;

		final Location modblockpos = target.getLocation().clone().add(dx, dy, dz);

		if (numBlocks<=0||!this.nativemc.canPlace(modblockpos.getBlock())||modblockpos.getBlockY()>=255)
			return blocks;

		if (!world.getNearbyEntities(modblockpos.clone().add(.5, .5, .5), .5, .5, .5).isEmpty())
			return blocks;

		if (!this.worldguard.canBuild(player, modblockpos))
			return blocks;

		blocks.add(modblockpos);

		for (int i = 0; i<blocks.size()&&blocks.size()<numBlocks; ++i) {
			final Location invblock = blocks.get(i);
			for (int ax = -mx; ax<=mx; ++ax)
				for (int ay = -my; ay<=my; ++ay)
					for (int az = -mz; az<=mz; ++az) {
						final Location targetloc = invblock.clone().add(ax, ay, az);
						final Location baseloc = targetloc.clone().subtract(dx, dy, dz);

						final int count = blocks.size();
						if (
							count>=numBlocks||
									blockcount&&maxdurability>0&&durability-count<=0||
									blocks.contains(targetloc)||
									!this.nativemc.canPlace(targetloc.getBlock())||
									!this.worldguard.canBuild(player, targetloc)||
									!baseloc.getBlock().getState().getData().equals(target.getState().getData())||
									data!=-1&&data!=this.nativemc.getDropData(baseloc.getBlock())||
									!world.getNearbyEntities(targetloc.clone().add(.5, .5, .5), .5, .5, .5).isEmpty()
						)
							continue;
						blocks.add(targetloc);
					}
		}
		return blocks;
	}

	public boolean sendResultMessage(final ActionResult result, final Locale locale, final CommandSender sender) {
		switch (result.getType()) {
			case ERROR: {
				final String message = result.getMessage();
				if (message!=null)
					sender.sendMessage(I18n.format(locale, "ubw.action.format.error.main", message));
				final String[] details = result.getDetails();
				for (final String detail : details)
					sender.sendMessage(I18n.format(locale, "ubw.action.format.error.sub", detail));
				return false;
			}
			default:
			case SUCCESS:
				return true;
		}
	}

	public static class ActionResult {
		private ResultType type;
		private String message;
		private String[] details;

		private ActionResult(final ResultType type, final String message, final String... details) {
			this.type = type;
			this.message = message;
			this.details = details;
		}

		public ResultType getType() {
			return this.type;
		}

		public String getMessage() {
			return this.message;
		}

		public String[] getDetails() {
			return this.details;
		}

		public static ActionResult success() {
			return new ActionResult(ResultType.SUCCESS, null);
		}

		public static ActionResult error() {
			return new ActionResult(ResultType.ERROR, null);
		}

		public static ActionResult error(final String message, final String... details) {
			return new ActionResult(ResultType.ERROR, message, details);
		}

		public enum ResultType {
			SUCCESS,
			ERROR,
		}
	}
}