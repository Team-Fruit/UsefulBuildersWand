package net.teamfruit.ubw;

import com.google.common.collect.Lists;
import net.teamfruit.ubw.I18n.Locale;
import net.teamfruit.ubw.WorldGuardHandler.WorldGuardHandleException;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import javax.annotation.Nullable;
import java.util.List;
import java.util.logging.Level;

public class WandListener implements Listener, CommandExecutor, TabCompleter {
    private final Plugin plugin;
    private final Locale locale;
    private NativeMinecraft nativemc;
    private final WorldGuardHandler worldguard;
    private final WandSessionManager sessionManager;

    private final Objective SCORE_WAND_SIZE;
    private final Objective SCORE_WAND_VERTICAL;
    private final Objective SCORE_WAND_RADIUS;
    private final Objective SCORE_WAND_EFFECT_RADIUS;
    private final Objective SCORE_WAND_EFFECT_COLOR;

    public WandListener(final Plugin plugin, final Locale locale) {
        this.plugin = plugin;
        this.locale = locale;
        this.nativemc = UBWPlugin.nativemc;
        this.worldguard = WorldGuardHandler.Factory.create(plugin);
        this.sessionManager = new WandSessionManager();
        new BukkitRunnable() {
            @Override
            public void run() {
                onEffect();
            }
        }.runTaskTimer(this.plugin, 3, 3);

        SCORE_WAND_SIZE = WandData.INSTANCE.getOrNewObjective(WandData.SCOREBOARD_WAND_SIZE, "dummy", "Wand Size");
        SCORE_WAND_VERTICAL = WandData.INSTANCE.getOrNewObjective(WandData.SCOREBOARD_WAND_VERTICAL, "dummy", "Wand Vertical");
        SCORE_WAND_RADIUS = WandData.INSTANCE.getOrNewObjective(WandData.SCOREBOARD_WAND_RADIUS, "dummy", "Wand Radius");
        SCORE_WAND_EFFECT_RADIUS = WandData.INSTANCE.getOrNewObjective(WandData.SCOREBOARD_WAND_EFFECT_RADIUS, "dummy", "Wand Effect Radius");
        SCORE_WAND_EFFECT_COLOR = WandData.INSTANCE.getOrNewObjective(WandData.SCOREBOARD_WAND_EFFECT_COLOR, "dummy", "Wand Effect Color Code");
    }

    private void onEffect() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            final ItemStack stage = this.nativemc.getItemInHand(player.getInventory());
            if (stage.getType() != Material.STICK)
                continue;

            if (!player.hasPermission("ubw.use"))
                continue;

            List<Location> blocks = null;
            try {
                final RayTraceResult res = this.nativemc.rayTrace(player);
                if (res != null)
                    blocks = getPotentialBlocks(stage, player, res.location.getBlock(), res.face);
            } catch (final Exception e) {
            }

            if (blocks != null) {
                final int color = WandData.INSTANCE.getScoreOrDefault(SCORE_WAND_EFFECT_COLOR, player, 0xffffff);
                final int color_r = (color >> 16) & 0xff;
                final int color_g = (color >> 8) & 0xff;
                final int color_b = (color >> 0) & 0xff;
                final int range = Math.min(
                        WandData.INSTANCE.getScoreOrDefault(SCORE_WAND_EFFECT_RADIUS, player, 0),
                        WandData.INSTANCE.getConfig().getInt(WandData.SETTING_EFFECT_RANGE)
                );
                if (range > 0) {
                    for (final Player other : Bukkit.getOnlinePlayers())
                        if (other.getLocation().distance(player.getLocation()) <= range)
                            for (final Location block : blocks)
                                this.nativemc.spawnParticles(other, block, color_r / 255f, color_g / 255f, color_b / 255f);
                } else
                    for (final Location block : blocks)
                        this.nativemc.spawnParticles(player, block, color_r / 255f, color_g / 255f, color_b / 255f);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerUse(final PlayerInteractEvent event) {
        if (!this.nativemc.isMainHand(event))
            return;

        if (!event.getPlayer().hasPermission(WandData.PERMISSION_WAND_USE))
            return;

        final Player player = event.getPlayer();
        if (sendResultMessage(onPlayerUse(player, event.getAction(), event.getClickedBlock(), event.getBlockFace()), this.locale, player))
            event.setCancelled(true);
    }

    private ActionResult onPlayerUse(final Player player, final Action action, final Block target, final BlockFace face) {
        if (!player.hasPermission("ubw.use"))
            return ActionResult.error();
        final PlayerInventory inventory = player.getInventory();
        ItemStack stage = this.nativemc.getItemInHand(inventory);
        if (stage.getType() != Material.STICK)
            return ActionResult.error();
        if (stage.getAmount() > 1) {
            final ItemStack[] stacks = inventory.getContents();
            final int heldslot = inventory.getHeldItemSlot();
            stackbreak:
            {
                final ItemStack newItemStack = stage.clone();
                newItemStack.setAmount(stage.getAmount() - 1);
                stage.setAmount(1);
                for (int i = 0; i < stacks.length; i++)
                    if (i != heldslot && stacks[i] == null) {
                        inventory.setItem(i, newItemStack);
                        break stackbreak;
                    }
                player.getWorld().dropItem(player.getEyeLocation(), newItemStack);
            }
        }

        try {
            if (target != null && action == Action.RIGHT_CLICK_BLOCK) {
                if (face != null) {
                    if (!this.worldguard.canBuild(player, target.getLocation())) {
                        Log.log.log(Level.INFO, String.format("[player=%s] tried to use wand in protected area: %s", player.getName(), target));
                        return ActionResult.error(I18n.format(this.locale, "ubw.action.error.worldguard"));
                    }
                    if (target.isEmpty())
                        return ActionResult.error();

                    final List<Location> blocks = getPotentialBlocks(stage, player, target, face);

                    final ItemStack item0 = UBWPlugin.nativemc.getItemFromBlock(target);

                    int data = -1;

                    if (UBWPlugin.nativemc.hasSubType(item0))
                        data = UBWPlugin.nativemc.getDropData(target);

                    ItemStackHolder wand = new ItemStackHolder.DefaultItemStackHolder(stage);

                    WandSession session = sessionManager.get(player);
                    WandSession.EditSession editSession = new WandSession.EditSession(player, blocks, item0, data, wand, face);
                    editSession.commit();
                    session.remember(editSession);

                    stage = wand.getItem();

                    UBWPlugin.nativemc.playSound(player, target.getLocation(), target, .25f, 1f);

                    return ActionResult.success();
                }
            } else if (player.isSneaking() && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
                boolean isVertical = WandData.INSTANCE.getScoreOrDefault(SCORE_WAND_VERTICAL, player, 0) != 0;
                isVertical = !isVertical;
                SCORE_WAND_VERTICAL.getScore(player).setScore(isVertical ? 1 : 0);
                return ActionResult.success();
            }
            return ActionResult.error();
        } catch (final Throwable e) {
            final String errorcode = Long.toHexString(System.currentTimeMillis());
            Log.log.log(Level.SEVERE, String.format("[player=%s, errorcode=%s]: A fatal error has occured: ", player.getName(), errorcode), e.getCause());
            return ActionResult.error(I18n.format(this.locale, "ubw.action.error", e.getMessage()), I18n.format(this.locale, "ubw.action.error.reportcode", errorcode));
        } finally {
            this.nativemc.setItemInHand(inventory, stage);
        }
    }

    private boolean hasNearbyEntities(World world, Location modblockpos) {
        if (this.nativemc.getVersion() <= 7) {
            Location center = modblockpos.clone().add(.5, .5, .5);
            for (Entity e : modblockpos.getChunk().getEntities()) {
                if (e.getLocation().distance(center) <= .9) {
                    return true;
                }
            }
            return false;
        } else {
            return !world.getNearbyEntities(modblockpos.clone().add(.5, .5, .5), .5, .5, .5).isEmpty();
        }
    }

    private List<Location> getPotentialBlocks(final ItemStack stage, final Player player, final @Nullable Block target, final BlockFace face) throws WorldGuardHandleException {
        final List<Location> blocks = Lists.newArrayList();

        final int maxdurability = 0;
        final int durability = 0;
        final boolean blockcount = false;

        if (maxdurability > 0 && durability <= 0)
            return blocks;

        final int maxBlocks = Math.min(
                WandData.INSTANCE.getScoreOrDefault(SCORE_WAND_SIZE, player, Integer.MAX_VALUE),
                WandData.INSTANCE.getConfig().getInt(WandData.SETTING_MAX_BLOCKS)
        );
        if (maxBlocks <= 0)
            return blocks;
        final int maxRadius = WandData.INSTANCE.getScoreOrDefault(SCORE_WAND_RADIUS, player, 48);
        if (maxRadius <= 0)
            return blocks;

        if (target == null || target.isEmpty())
            return blocks;

        if (!this.worldguard.canBuild(player, target.getLocation()))
            return blocks;

        final ItemStack blockItem = this.nativemc.getItemFromBlock(target);

        int data = -1;

        if (this.nativemc.hasSubType(blockItem))
            data = this.nativemc.getDropData(target);

        int numBlocks = 0;
        if (player.getGameMode() == GameMode.CREATIVE) {
            numBlocks = maxBlocks;
        } else {
            final Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getSize(); ++i) {
                final ItemStack itemStack2 = inventory.getItem(i);
                if (itemStack2 != null) {
                    if (itemStack2.getType().equals(blockItem.getType()) && (data == -1 || data == itemStack2.getDurability()))
                        numBlocks += itemStack2.getAmount();
                    if (numBlocks >= maxBlocks) {
                        numBlocks = maxBlocks;
                        break;
                    }
                }
            }
        }

        final int dx = face.getModX();
        final int dy = face.getModY();
        final int dz = face.getModZ();
        int mx = dx == 0 ? 1 : 0;
        int my = dy == 0 ? 1 : 0;
        int mz = dz == 0 ? 1 : 0;

        if (player.isSneaking()) {
            final boolean isVertical = WandData.INSTANCE.getScoreOrDefault(SCORE_WAND_VERTICAL, player, 0) != 0;
            if (face != BlockFace.UP && face != BlockFace.DOWN)
                if (isVertical) {
                    if (face != BlockFace.NORTH && face != BlockFace.SOUTH)
                        mz = 0;
                    else
                        mx = 0;
                } else
                    my = 0;
            else {
                final double rotation = player.getLocation().getYaw() + 360;
                final boolean b = 45 * 1 <= rotation && rotation < 45 * 3 || 45 * 5 <= rotation && rotation < 45 * 7;
                if ((b || isVertical) && (!b || !isVertical))
                    mx = 0;
                else
                    mz = 0;
            }
        }

        final World world = player.getWorld();
        if (world == null)
            return blocks;

        final Location modblockpos = target.getLocation().clone().add(dx, dy, dz);

        if (numBlocks <= 0 || !this.nativemc.canPlace(modblockpos.getBlock()) || modblockpos.getBlockY() >= 255)
            return blocks;

        if (hasNearbyEntities(world, modblockpos))
            return blocks;

        if (!this.worldguard.canBuild(player, modblockpos))
            return blocks;

        blocks.add(modblockpos);

        for (int i = 0; i < blocks.size() && blocks.size() < numBlocks; ++i) {
            final Location invblock = blocks.get(i);
            for (int ax = -mx; ax <= mx; ++ax)
                for (int ay = -my; ay <= my; ++ay)
                    for (int az = -mz; az <= mz; ++az) {
                        final Location targetloc = invblock.clone().add(ax, ay, az);
                        final Location baseloc = targetloc.clone().subtract(dx, dy, dz);

                        final Location offset = targetloc.clone().subtract(modblockpos);
                        if (Math.abs(offset.getX()) > maxRadius - 1 ||
                                Math.abs(offset.getY()) > maxRadius - 1 ||
                                Math.abs(offset.getZ()) > maxRadius - 1
                        )
                            continue;

                        final int count = blocks.size();
                        if (
                                count >= numBlocks ||
                                        blockcount && maxdurability > 0 && durability - count <= 0 ||
                                        blocks.contains(targetloc) ||
                                        !this.nativemc.canPlace(targetloc.getBlock()) ||
                                        !this.worldguard.canBuild(player, targetloc) ||
                                        !baseloc.getBlock().getState().getData().equals(target.getState().getData()) ||
                                        data != -1 && data != this.nativemc.getDropData(baseloc.getBlock()) ||
                                        hasNearbyEntities(world, targetloc)
                        )
                            continue;
                        //Log.log.info(targetloc.toString());
                        blocks.add(targetloc);
                    }
        }
        return blocks;
    }

    public boolean sendResultMessage(final ActionResult result, final Locale locale, final CommandSender sender) {
        switch (result.getType()) {
            case ERROR: {
                final String message = result.getMessage();
                if (message != null)
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


    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (command.getName().equalsIgnoreCase("wand")) {
            if (!sender.hasPermission(WandData.PERMISSION_WAND_GRANT)) {
                sender.sendMessage("You don't have permission to do that");
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("You need to be a Player.");
                return true;
            }
            Player player = (Player) sender;
            if (args.length < 1)
                return false;
            if ("undo".equalsIgnoreCase(args[0])) {
                sessionManager.get(player).undo(player);
                sender.sendMessage("Undo successful!");
                return true;
            }
            if ("redo".equalsIgnoreCase(args[0])) {
                sessionManager.get(player).redo(player);
                sender.sendMessage("Redo successful!");
                return true;
            }
            if (args.length < 2)
                return false;
            if ("effect_radius".equalsIgnoreCase(args[0]) || "fxr".equalsIgnoreCase(args[0])) {
                int radius = NumberUtils.toInt(args[1]);
                SCORE_WAND_EFFECT_RADIUS.getScore(sender.getName()).setScore(radius);
                sender.sendMessage("Effect radius is set to " + radius);
                return true;
            }
            if ("effect_color".equalsIgnoreCase(args[0]) || "fxc".equalsIgnoreCase(args[0])) {
                int color = 0;
                try {
                    color = Integer.valueOf(args[1], 16);
                } catch (NumberFormatException ignored) {
                }
                SCORE_WAND_EFFECT_COLOR.getScore(sender.getName()).setScore(color);
                sender.sendMessage("Effect color is set to " + color);
                return true;
            }
            if ("size".equalsIgnoreCase(args[0]) || "s".equalsIgnoreCase(args[0])) {
                int size = NumberUtils.toInt(args[1]);
                SCORE_WAND_SIZE.getScore(sender.getName()).setScore(size);
                sender.sendMessage("Size limit is set to " + size);
                return true;
            }
            if ("radius".equalsIgnoreCase(args[0]) || "r".equalsIgnoreCase(args[0])) {
                int radius = NumberUtils.toInt(args[1]);
                SCORE_WAND_RADIUS.getScore(sender.getName()).setScore(radius);
                sender.sendMessage("Radius limit is set to " + radius);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        return null;
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