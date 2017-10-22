package net.teamfruit.usefulbuilderswand;

import static net.teamfruit.usefulbuilderswand.meta.Features.*;
import static net.teamfruit.usefulbuilderswand.meta.WandMetaUtils.*;

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

import net.teamfruit.usefulbuilderswand.WorldGuardHandler.WorldGuardHandleException;
import net.teamfruit.usefulbuilderswand.meta.IWandMeta;
import net.teamfruit.usefulbuilderswand.meta.WandItem;
import net.teamfruit.usefulbuilderswand.meta.WandItemMeta;

public class WandListener implements Listener, UsefulBuildersWandAPI {
	private final Plugin plugin;
	private final WandData wanddata;
	private NativeMinecraft nativemc;
	private final WorldGuardHandler worldguard;

	public WandListener(final Plugin plugin, final WandData wanddata, final NativeMinecraft nativemc) {
		this.plugin = plugin;
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

	@Override
	public @Nullable RayTraceResult rayTrace(final Player player) {
		return this.nativemc.rayTrace(player);
	}

	public void onEffect() {
		for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
			final WandItem witem = new WandItem(this.nativemc.getItemInHand(player.getInventory()));

			{
				final ItemStack itemStack = witem.getItem();
				if (itemStack==null||itemStack.getAmount()==0)
					continue;
			}

			final IWandMeta meta = this.wanddata.wrapMeta(witem.getMeta());

			if (!witem.hasContent())
				return;

			onEffect(player, witem, meta);
		}
	}

	public void onEffect(final Player player, final WandItem witem, final IWandMeta meta) {
		List<Location> blocks = null;
		try {
			final RayTraceResult res = this.nativemc.rayTrace(player);
			if (res!=null)
				blocks = getPotentialBlocks(witem, meta, player, player.getWorld(), res.location.getBlock(), res.face);
		} catch (final Exception e) {
		}

		final Boolean ownerenabled = meta.getFlag(FEATURE_META_OWNER.path);
		if (ownerenabled!=null&&ownerenabled) {
			final String uuidhash = StringUtils.replace(player.getUniqueId().toString(), "-", "");
			final String metaid = meta.getText(FEATURE_META_OWNER_ID.path);
			if (!StringUtils.isEmpty(metaid)&&!StringUtils.equals(uuidhash, metaid))
				return;
		}

		if (blocks!=null) {
			final int color_r = or(meta.getNumber(FEATURE_META_PARTICLE_COLOR_R.path), 255);
			final int color_g = or(meta.getNumber(FEATURE_META_PARTICLE_COLOR_G.path), 255);
			final int color_b = or(meta.getNumber(FEATURE_META_PARTICLE_COLOR_B.path), 255);
			final int range = this.wanddata.getConfig().getInt(WandData.SETTING_EFFECT_RANGE);
			if (range>0&&or(meta.getFlag(FEATURE_META_PARTICLE_SHARE.path), true)) {
				for (final Player other : Bukkit.getOnlinePlayers())
					if (other.getLocation().distance(player.getLocation())<=range)
						for (final Location block : blocks)
							this.nativemc.spawnParticles(other, block, color_r/255f, color_g/255f, color_b/255f);
			} else
				for (final Location block : blocks)
					this.nativemc.spawnParticles(player, block, color_r/255f, color_g/255f, color_b/255f);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerUse(final PlayerInteractEvent event) {
		if (!this.nativemc.isMainHand(event))
			return;

		final Player player = event.getPlayer();
		final PlayerInventory inventory = player.getInventory();
		final WandItem witem = new WandItem(this.nativemc.getItemInHand(inventory));

		{
			final ItemStack itemStack = witem.getItem();
			if (itemStack==null||itemStack.getAmount()==0)
				return;

			if (itemStack.getAmount()>1) {
				final ItemStack[] stacks = inventory.getContents();
				final int heldslot = inventory.getHeldItemSlot();
				stackbreak: {
					final ItemStack newItemStack = itemStack.clone();
					newItemStack.setAmount(itemStack.getAmount()-1);
					itemStack.setAmount(1);
					for (int i = 0; i<stacks.length; i++)
						if (i!=heldslot&&stacks[i]==null) {
							inventory.setItem(i, newItemStack);
							break stackbreak;
						}
					player.getWorld().dropItem(player.getEyeLocation(), newItemStack);
				}
			}
		}

		final WandItemMeta wmeta = witem.getMeta();
		if (wmeta==null)
			return;
		final IWandMeta meta = this.wanddata.wrapMeta(wmeta);

		if (!witem.hasContent())
			return;

		try {
			final Boolean ownerenabled = meta.getFlag(FEATURE_META_OWNER.path);
			if (ownerenabled!=null&&ownerenabled) {
				final String uuidhash = StringUtils.replace(player.getUniqueId().toString(), "-", "");
				final String metaid = meta.getText(FEATURE_META_OWNER_ID.path);
				if (!StringUtils.isEmpty(metaid)&&!StringUtils.equals(uuidhash, metaid))
					return;
				else {
					wmeta.setText(FEATURE_META_OWNER_ID.path, uuidhash);
					wmeta.setText(FEATURE_META_OWNER_NAME.path, player.getDisplayName());
				}
			} else {
				wmeta.setText(FEATURE_META_OWNER_ID.path, null);
				wmeta.setText(FEATURE_META_OWNER_NAME.path, null);
			}

			final Action action = event.getAction();
			final Block block = event.getClickedBlock();

			if (block!=null&&action==Action.RIGHT_CLICK_BLOCK) {
				final BlockFace face = event.getBlockFace();
				if (face!=null)
					if (onItemUse(witem, meta, player, player.getWorld(), block, face))
						event.setCancelled(true);
			} else if (player.isSneaking()&&(action==Action.LEFT_CLICK_AIR||action==Action.LEFT_CLICK_BLOCK)) {
				wmeta.setFlag(FEATURE_META_MODE.path, !or(meta.getFlag(FEATURE_META_MODE.path), false));
				event.setCancelled(true);
			}

			this.wanddata.updateItem(witem);
			this.nativemc.setItemInHand(inventory, witem.getItem());
		} catch (final Throwable e) {
			final String errorcode = Long.toHexString(System.currentTimeMillis());
			player.sendMessage(String.format("§c[Useful Builder's Wand] A fatal error has occured. Please report to server administrator. [errorcode=%s]", errorcode));
			Log.log.log(Level.SEVERE, String.format("[player=%s, errorcode=%s]: A fatal error has occured: ", player.getDisplayName(), errorcode), e.getCause());
		}
	}

	public boolean onItemUse(final WandItem witem, final IWandMeta meta, final Player player, final World world, final Block target, final BlockFace face) throws WorldGuardHandleException {
		if (target.isEmpty())
			return false;

		final WandItemMeta wmeta = witem.getMeta();
		if (wmeta==null)
			return false;

		final int maxdurability = or(meta.getNumber(FEATURE_META_DURABILITY_MAX.path), 0);

		final List<Location> blocks = getPotentialBlocks(witem, meta, player, world, target, face);

		if (blocks.isEmpty())
			return false;

		int durability = or(meta.getNumber(FEATURE_META_DURABILITY.path), 0);
		final boolean blockcount = or(meta.getFlag(FEATURE_META_DURABILITY_BLOCKCOUNT.path), false);

		if (maxdurability>0&&durability<=0)
			return false;

		int data = -1;

		final ItemStack item1 = this.nativemc.getItemFromBlock(target);

		if (this.nativemc.hasSubType(item1))
			data = this.nativemc.getDropData(target);

		int slot = 0;
		final PlayerInventory inventory = player.getInventory();
		int placecount = 0;
		for (final Location temp : blocks) {
			if (maxdurability>0&&durability<=0)
				return false;
			for (slot = 0; slot<inventory.getSize(); ++slot) {
				final ItemStack item = inventory.getItem(slot);
				if (item==null||!item.getType().equals(item1.getType()))
					continue;
				if (data==-1||data==item.getDurability())
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

			if (this.nativemc.placeItem(player, block, witem, objitem, EquipmentSlot.HAND, face, player.getEyeLocation())) {
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
		wmeta.setNumber(FEATURE_META_COUNT_PLACE.path, or(meta.getNumber(FEATURE_META_COUNT_PLACE.path), 0)+placecount);
		wmeta.setNumber(FEATURE_META_COUNT_USE.path, or(meta.getNumber(FEATURE_META_COUNT_USE.path), 0)+1);
		if (maxdurability>0)
			wmeta.setNumber(FEATURE_META_DURABILITY.path, durability);

		return true;
	}

	@Override
	public List<Location> getCandidateBlocks(final WandItem witem, final IWandMeta meta, final Player player, final @Nullable World world, final @Nullable Block target, final BlockFace face) {
		try {
			return getPotentialBlocks(witem, meta, player, world, target, face);
		} catch (final WorldGuardHandleException e) {
			return Lists.newArrayList();
		}
	}

	public List<Location> getPotentialBlocks(final WandItem witem, final IWandMeta meta, final Player player, final @Nullable World world, final @Nullable Block target, final BlockFace face) throws WorldGuardHandleException {
		final List<Location> blocks = Lists.newArrayList();

		final int maxdurability = or(meta.getNumber(FEATURE_META_DURABILITY_MAX.path), 0);
		final int durability = or(meta.getNumber(FEATURE_META_DURABILITY.path), 0);
		final boolean blockcount = or(meta.getFlag(FEATURE_META_DURABILITY_BLOCKCOUNT.path), false);

		if (maxdurability>0&&durability<=0)
			return blocks;

		final int maxBlocks = or(meta.getNumber(FEATURE_META_SIZE.path), 0);

		if (world==null)
			return blocks;

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
			final boolean isVertical = or(meta.getFlag(FEATURE_META_MODE.path), false);
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
}