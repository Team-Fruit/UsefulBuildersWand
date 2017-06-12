package net.teamfruit.usefulbuilderswand;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreContent;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreDataFormat;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreMeta;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreRaw;
import net.teamfruit.usefulbuilderswand.NativeMinecraft.RayTraceResult;
import net.teamfruit.usefulbuilderswand.UsefulBuildersWand.WandData;

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
			final ItemStack item = player.getInventory().getItemInMainHand();
			if (item!=null) {
				final ItemLoreDataFormat format = this.wanddata.getFormat();
				final ItemLoreRaw raw = new ItemLoreRaw().readItemStack(format, item);
				final ItemLoreMeta meta = new ItemLoreMeta().fromContents(format, new ItemLoreContent().fromRaw(format, raw));
				for (final String arg : args)
					if (StringUtils.contains(arg, "=")) {
						final String key = StringUtils.substringBefore(arg, "=");
						final String value = StringUtils.substringAfter(arg, "=");
						meta.set(format, key, value);
					}
				raw.updateContents(format, new ItemLoreContent().fromMeta(format, meta));
				raw.writeItemStack(format, item);
			}
		}
		return false;
	}

	public void onEffect() {
		for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
			final ItemStack item = this.nativemc.getItemInHand(player.getInventory());
			if (item.getType()==Material.STICK||item.getType()==Material.BLAZE_ROD)
				onEffect(player, item);
		}
	}

	public void onEffect(final Player player, final ItemStack itemStack) {
		List<Location> blocks = null;
		try {
			final RayTraceResult res = this.nativemc.rayTrace(player);
			if (res!=null)
				blocks = getPotentialBlocks(itemStack, player, player.getWorld(), res.location.getBlock(), res.face);
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
		final ItemStack itemStack = this.nativemc.getItemInHand(player.getInventory());
		final ItemLoreDataFormat format = this.wanddata.getFormat();
		final ItemLoreRaw raw = new ItemLoreRaw().readItemStack(format, itemStack);
		if (!raw.hasContent(format))
			return;
		final ItemLoreMeta meta = new ItemLoreMeta().fromContents(format, new ItemLoreContent().fromRaw(format, raw));
		final int modcount = meta.getModCount();

		final Material material = itemStack.getType();
		final Action action = event.getAction();
		final Block block = event.getClickedBlock();

		if (material==Material.STICK||material==Material.BLAZE_ROD) {
			if (block!=null&&action==Action.RIGHT_CLICK_BLOCK) {
				final BlockFace face = event.getBlockFace();
				if (face!=null) {
					onItemUse(itemStack, player, player.getWorld(), block, face);
					event.setCancelled(true);
				}
			} else if (player.isSneaking()&&(action==Action.LEFT_CLICK_AIR||action==Action.LEFT_CLICK_BLOCK)) {
				final String key = this.wanddata.key(WandData.FEATURE_DATA_VERTICALMODE);
				meta.setFlag(key, !meta.getFlag(key));
				event.setCancelled(true);
			}
		} else if (material==Material.OBSIDIAN&&action==Action.RIGHT_CLICK_AIR) {
			final List<String> lore2 = itemStack.getItemMeta().getLore();
			final boolean isVertical = lore2!=null&&lore2.contains("§r");
			if (!isVertical)
				return;

			final Location location = player.getEyeLocation();
			Block target = location.getBlock();
			BlockFace face;

			final float pitch = location.getPitch()+90;
			final int pitchrotate = (int) pitch/45;
			if (pitchrotate<=0)
				face = BlockFace.UP;
			else if (pitchrotate>=3) {
				face = BlockFace.DOWN;
				target = target.getRelative(BlockFace.DOWN);
			} else {
				final float yaw = ((location.getYaw()-45)%360+360)%360;
				final int rotate = (int) yaw/90;
				switch (rotate) {
					default:
					case 1:
						face = BlockFace.NORTH;
						break;
					case 2:
						face = BlockFace.EAST;
						break;
					case 3:
						face = BlockFace.SOUTH;
						break;
					case 0:
						face = BlockFace.WEST;
						break;
				}
			}

			final Block relative = target.getRelative(face);
			if (relative.isEmpty()) {
				relative.setType(Material.OBSIDIAN);
				relative.setMetadata("AngelBlock", new AngelBlockMetadata().setAngel(true));

				final ItemStack itemStack2 = new ItemStack(Material.OBSIDIAN);
				final ItemMeta metad = itemStack2.getItemMeta();
				List<String> lore = metad.getLore();
				if (lore==null)
					lore = Lists.newArrayList();
				lore.add("§r");
				metad.setLore(lore);
				itemStack2.setItemMeta(metad);

				player.getInventory().addItem(itemStack2);
			}
		} else if (block!=null&&block.getType()==Material.OBSIDIAN) {
			final List<MetadataValue> metadatas = block.getMetadata("AngelBlock");
			for (final MetadataValue metadata : metadatas)
				if (metadata instanceof AngelBlockMetadata)
					if (((AngelBlockMetadata) metadata).isAngel()) {
						block.setType(Material.AIR);
						itemStack.setAmount(itemStack.getAmount()-1);
						event.setCancelled(true);
					}
		}

		if (modcount!=meta.getModCount())
			raw.updateContents(format, new ItemLoreContent().fromMeta(format, meta)).writeItemStack(format, itemStack);
	}

	public class AngelBlockMetadata extends MetadataValueAdapter {
		private boolean isAngel;

		protected AngelBlockMetadata() {
			super(WandListener.this.plugin);
		}

		public AngelBlockMetadata setAngel(final boolean isAngel) {
			this.isAngel = isAngel;
			return this;
		}

		public boolean isAngel() {
			return this.isAngel;
		}

		public void invalidate() {
		}

		public Object value() {
			return this.isAngel;
		}
	}

	public boolean onItemUse(final ItemStack itemStack, final Player player, final World world, final Block target, final BlockFace face) {
		/*if (!player.capabilities.allowEdit)
			return false;
		else*/ {
			final List<Location> blocks = getPotentialBlocks(itemStack, player, world, target, face);

			if (blocks.size()==0)
				return false;
			else if (target.isEmpty())
				return false;
			else {
				int data = -1;

				final ItemStack item1 = this.nativemc.getItemFromBlock(target);

				if (this.nativemc.hasSubType(item1))
					data = this.nativemc.getDropData(target);

				if (blocks.size()>0) {
					int slot = 0;
					final Inventory inventory = player.getInventory();
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

						if (this.nativemc.placeItem(player, block, objitem, EquipmentSlot.HAND, face, player.getEyeLocation())) {

							objitem.setAmount(objitem.getAmount()-1);
							if (item.getAmount()<=0)
								inventory.setItem(slot, null);
							else
								inventory.setItem(slot, item);

							this.nativemc.playSound(player, temp, target, .25f, 1f);
						}
					}
				}

				return true;
			}
		}
	}

	public List<Location> getPotentialBlocks(final ItemStack itemStack, final Player player, final World world, final Block target, final BlockFace face) {
		final int maxBlocks = itemStack.getType()==Material.BLAZE_ROD ? 49 : 9;

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
			final List<String> lore = itemStack.getItemMeta().getLore();
			final boolean isVertical = lore!=null&&lore.contains("§r");
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