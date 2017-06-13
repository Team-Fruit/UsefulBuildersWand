package net.teamfruit.usefulbuilderswand;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Lists;

public class AngelBlock {
	private final Plugin plugin;

	public AngelBlock(final Plugin plugin, final WandData data) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerUse(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		@SuppressWarnings("deprecation")
		final ItemStack itemStack = player.getInventory().getItemInHand();
		final Material material = itemStack.getType();
		final Action action = event.getAction();
		final Block block = event.getClickedBlock();

		if (material==Material.OBSIDIAN&&action==Action.RIGHT_CLICK_AIR) {
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
	}

	public class AngelBlockMetadata extends MetadataValueAdapter {
		private boolean isAngel;

		protected AngelBlockMetadata() {
			super(AngelBlock.this.plugin);
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
}
