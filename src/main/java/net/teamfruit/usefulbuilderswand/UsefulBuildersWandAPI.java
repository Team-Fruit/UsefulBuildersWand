package net.teamfruit.usefulbuilderswand;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import net.teamfruit.usefulbuilderswand.meta.WandItemMeta;

public interface UsefulBuildersWandAPI {
	@Nullable
	RayTraceResult rayTrace(final @Nonnull Player player);

	List<Location> getCandidateBlocks(final @Nonnull WandItemMeta meta, final @Nonnull Player player, final @Nullable World world, final @Nullable Block target, final @Nonnull BlockFace face);
}
