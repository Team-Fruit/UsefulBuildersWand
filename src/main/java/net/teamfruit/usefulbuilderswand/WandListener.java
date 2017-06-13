package net.teamfruit.usefulbuilderswand;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
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

import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreContent;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreDataFormat;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreMeta;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreRaw;
import net.teamfruit.usefulbuilderswand.NativeMinecraft.RayTraceResult;

public class WandListener implements Listener, CommandExecutor {
	private final Plugin plugin;
	private final WandData wanddata;
	private NativeMinecraft nativemc;

	public WandListener(final Plugin plugin, final WandData data) {
		this.plugin = plugin;
		this.wanddata = data;
		this.nativemc = NativeMinecraft.NativeMinecraftFactory.create(plugin);
		new BukkitRunnable() {
			public void run() {
				onEffect();
			}
		}.runTaskTimer(this.plugin, 3, 3);
	}

	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			final ItemStack itemStack = this.nativemc.getItemInHand(player.getInventory());
			if (itemStack!=null) {
				final ItemLoreDataFormat format = this.wanddata.getFormat();
				final ItemLoreRaw raw = new ItemLoreRaw().readItemStack(format, itemStack);
				final ItemLoreMeta meta = new ItemLoreMeta().fromContents(format, new ItemLoreContent().fromRaw(format, raw));
				for (final String arg : args)
					if (StringUtils.contains(arg, "=")) {
						final String key = StringUtils.substringBefore(arg, "=");
						final String value = StringUtils.substringAfter(arg, "=");
						final String key1 = this.wanddata.keyData(WandData.FEATURE+"."+key);
						meta.set(format, key1!=null ? key1 : key, value);
					}
				raw.updateContents(format, new ItemLoreContent().fromMeta(format, meta)).writeItemStack(format, itemStack);
			}
		}
		return false;
	}

	public void onEffect() {
		final ItemLoreDataFormat format = this.wanddata.getFormat();
		for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
			final ItemStackHolder itemStackHolder = new ItemStackHolder(this.nativemc.getItemInHand(player.getInventory()));

			{
				final ItemStack itemStack = itemStackHolder.get();
				if (itemStack==null||itemStack.getAmount()==0)
					return;
			}

			final ItemLoreRaw raw = new ItemLoreRaw().readItemStack(format, itemStackHolder.get());
			final ItemLoreMeta meta = new ItemLoreMeta().fromContents(format, new ItemLoreContent().fromRaw(format, raw));

			if (raw.hasContent(format))
				onEffect(player, itemStackHolder, meta);
		}
	}

	public void onEffect(final Player player, final ItemStackHolder itemStack, final ItemLoreMeta meta) {
		List<Location> blocks = null;
		try {
			final RayTraceResult res = this.nativemc.rayTrace(player);
			if (res!=null)
				blocks = getPotentialBlocks(itemStack, meta, player, player.getWorld(), res.location.getBlock(), res.face);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		if (blocks!=null)
			for (final Location block : blocks)
				this.nativemc.spawnParticles(player, block);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerUse(final PlayerInteractEvent event) {
		if (!this.nativemc.isMainHand(event))
			return;

		final Player player = event.getPlayer();
		final PlayerInventory inventory = player.getInventory();
		final ItemStackHolder itemStackHolder = new ItemStackHolder(this.nativemc.getItemInHand(inventory));

		{
			final ItemStack itemStack = itemStackHolder.get();
			if (itemStack==null||itemStack.getAmount()==0)
				return;

			if (itemStack.getAmount()>1) {
				final ItemStack[] stacks = inventory.getStorageContents();
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

		final ItemLoreDataFormat format = this.wanddata.getFormat();
		final ItemLoreRaw raw = new ItemLoreRaw().readItemStack(format, itemStackHolder.get());

		if (!raw.hasContent(format))
			return;

		final ItemLoreMeta meta = new ItemLoreMeta().fromContents(format, new ItemLoreContent().fromRaw(format, raw));
		final int modcount = meta.getModCount();

		final Action action = event.getAction();
		final Block block = event.getClickedBlock();

		if (block!=null&&action==Action.RIGHT_CLICK_BLOCK) {
			final BlockFace face = event.getBlockFace();
			if (face!=null) {
				onItemUse(itemStackHolder, meta, player, player.getWorld(), block, face);
				event.setCancelled(true);
			}
		} else if (player.isSneaking()&&(action==Action.LEFT_CLICK_AIR||action==Action.LEFT_CLICK_BLOCK)) {
			final String key = this.wanddata.key(WandData.FEATURE_META_VERTICALMODE);
			final boolean b = !meta.getFlag(key, false);
			player.chat(String.valueOf(b));
			meta.setFlag(key, b);
			event.setCancelled(true);
		}

		if (modcount!=meta.getModCount())
			raw.updateContents(format, new ItemLoreContent().fromMeta(format, meta)).writeItemStack(format, itemStackHolder.get());
	}

	public boolean onItemUse(final ItemStackHolder itemStack, final ItemLoreMeta meta, final Player player, final World world, final Block target, final BlockFace face) {
		/*if (!player.capabilities.allowEdit)
			return false;
		else*/ {
			final List<Location> blocks = getPotentialBlocks(itemStack, meta, player, world, target, face);

			if (blocks.isEmpty())
				return false;
			else if (target.isEmpty())
				return false;
			else {
				int data = -1;

				final ItemStack item1 = this.nativemc.getItemFromBlock(target);

				if (this.nativemc.hasSubType(item1))
					data = this.nativemc.getDropData(target);

				int slot = 0;
				final PlayerInventory inventory = player.getInventory();
				int placecount = 0;
				for (final Location temp : blocks) {
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

					if (this.nativemc.placeItem(player, block, itemStack, objitem, EquipmentSlot.HAND, face, player.getEyeLocation())) {
						objitem.setAmount(objitem.getAmount()-1);
						if (item.getAmount()<=0)
							inventory.setItem(slot, null);
						else
							inventory.setItem(slot, item);

						this.nativemc.playSound(player, temp, target, .25f, 1f);
						placecount++;
					}
				}
				final String keyplace = this.wanddata.keyData(WandData.FEATURE_META_COUNT_PLACE);
				meta.setNumber(keyplace, meta.getNumber(keyplace, 0)+placecount);
				final String keyuse = this.wanddata.keyData(WandData.FEATURE_META_COUNT_USE);
				meta.setNumber(keyuse, meta.getNumber(keyuse, 0)+1);

				return true;
			}
		}
	}

	public List<Location> getPotentialBlocks(final ItemStackHolder itemStack, final ItemLoreMeta meta, final Player player, final World world, final Block target, final BlockFace face) {
		final int maxBlocks = meta.getNumber(this.wanddata.keyData(WandData.FEATURE_META_SIZE), 0);

		final List<Location> blocks = Lists.newArrayList();

		if (world==null)
			return blocks;

		if (target==null||target.isEmpty())
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
			final boolean isVertical = meta.getFlag(this.wanddata.keyData(WandData.FEATURE_META_VERTICALMODE), false);
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

		if (numBlocks<=0||!this.nativemc.canPlace(modblockpos.getBlock())||modblockpos.getBlockY()>=255||!world.getNearbyEntities(modblockpos.clone().add(.5, .5, .5), .5, .5, .5).isEmpty())
			return blocks;

		blocks.add(modblockpos);

		for (int i = 0; i<blocks.size()&&blocks.size()<numBlocks; ++i) {
			final Location invblock = blocks.get(i);
			for (int ax = -mx; ax<=mx; ++ax)
				for (int ay = -my; ay<=my; ++ay)
					for (int az = -mz; az<=mz; ++az) {
						final Location targetloc = invblock.clone().add(ax, ay, az);
						final Location baseloc = targetloc.clone().subtract(dx, dy, dz);

						if (
							blocks.size()>=numBlocks||
									blocks.contains(targetloc)||
									!this.nativemc.canPlace(targetloc.getBlock())||
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