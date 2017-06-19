package net.teamfruit.usefulbuilderswand;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreDataFormat;
import net.teamfruit.usefulbuilderswand.ItemLore.ItemLoreMeta;

public interface UsefulBuildersWandAPI {
	@Nonnull
	ItemLoreDataFormat getFormat();

	ItemLoreMeta readMeta(@Nonnull ItemStack itemStack);

	void writeMeta(@Nonnull ItemStack itemStack, @Nonnull ItemLoreMeta meta);

	@Nullable
	RayTraceResult rayTrace(final @Nonnull Player player);

	public List<Location> getCandidateBlocks(final @Nonnull ItemLoreMeta meta, final @Nonnull Player player, final @Nullable World world, final @Nullable Block target, final @Nonnull BlockFace face);
}
