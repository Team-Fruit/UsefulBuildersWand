package net.teamfruit.ubw;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class WandSession {
    public transient static int MAX_HISTORY_SIZE = 15;

    private transient LinkedList<EditSession> history = new LinkedList<>();
    private transient int historyPointer = 0;

    public void clearHistory() {
        history.clear();
        historyPointer = 0;
    }

    public void remember(EditSession editSession) {
        checkNotNull(editSession);

        // Don't store anything if no changes were made
        if (editSession.size() == 0) return;

        // Destroy any sessions after this undo point
        while (historyPointer < history.size()) {
            history.remove(historyPointer);
        }
        history.add(editSession);
        while (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
        historyPointer = history.size();
    }

    public EditSession undo(Player player) {
        checkNotNull(player);
        --historyPointer;
        if (historyPointer >= 0) {
            EditSession editSession = history.get(historyPointer);
            editSession.undo();
            return editSession;
        } else {
            historyPointer = 0;
            return null;
        }
    }

    public EditSession redo(Player player) {
        checkNotNull(player);
        if (historyPointer < history.size()) {
            EditSession editSession = history.get(historyPointer);
            editSession.redo();
            ++historyPointer;
            return editSession;
        }

        return null;
    }

    public static class EditSession {
        private final Player player;
        private final List<Location> blocks;
        private final ItemStack item;
        private final int data;
        private final ItemStackHolder wand;
        private final BlockFace face;

        public EditSession(Player player, List<Location> blocks, ItemStack item, int data, ItemStackHolder wand, BlockFace face) {
            this.player = player;
            this.blocks = blocks;
            this.item = item;
            this.data = data;
            this.wand = wand;
            this.face = face;
        }

        public int size() {
            return blocks.size();
        }

        protected void commit() {
            if (blocks.isEmpty())
                return;

            final Inventory inventory = player.getInventory();

            final int maxdurability = 0;
            int durability = 0;
            final boolean blockcount = false;

            if (maxdurability > 0 && durability <= 0)
                return;

            ItemStack item1 = item;

            if (player.getGameMode() == GameMode.CREATIVE) {
                for (int i = 0; i < inventory.getSize(); ++i) {
                    final ItemStack itemslot = inventory.getItem(i);
                    if (itemslot == null || itemslot.getAmount() == 0 || !itemslot.getType().equals(item1.getType()))
                        continue;
                    if (data == -1 || data == itemslot.getDurability()) {
                        item1 = itemslot;
                        break;
                    }
                }
            }

            int cacheslot = -1;
            int placecount = 0;
            for (final Location temp : blocks) {
                if (maxdurability > 0 && durability <= 0)
                    return;

                int slot = -1;
                final ItemStack item;
                ItemStack objitem;
                if (player.getGameMode() == GameMode.CREATIVE) {
                    item = item1;
                    objitem = item1.clone();
                    objitem.setAmount(1);
                } else {
                    if (cacheslot >= 0) {
                        final ItemStack itemslot = inventory.getItem(cacheslot);
                        if (!(itemslot == null || itemslot.getAmount() == 0 || !itemslot.getType().equals(item1.getType())))
                            if (data == -1 || data == itemslot.getDurability()) {
                                slot = cacheslot;
                                break;
                            }
                    }
                    if (slot < 0) {
                        for (int i = 0; i < inventory.getSize(); ++i) {
                            final ItemStack itemslot = inventory.getItem(i);
                            if (!(itemslot == null || itemslot.getAmount() == 0 || !itemslot.getType().equals(item1.getType())))
                                if (data == -1 || data == itemslot.getDurability()) {
                                    cacheslot = slot = i;
                                    break;
                                }
                        }
                    }

                    if (slot < 0)
                        break;

                    item = inventory.getItem(slot);
                    objitem = item;
                }

                final Block block = temp.getBlock().getRelative(face.getOppositeFace());

                final boolean placeresult = UBWPlugin.nativemc.placeItem(player, block, wand, objitem, face, player.getEyeLocation());

                if (placeresult) {
                    objitem.setAmount(objitem.getAmount() - 1);
                    if (slot >= 0) {
                        if (objitem.getAmount() <= 0)
                            inventory.setItem(slot, null);
                        else
                            inventory.setItem(slot, item);
                    }

                    placecount++;
                    if (blockcount)
                        durability--;
                }
            }
            if (!blockcount)
                durability--;
        }

        public void undo() {
            for (Location loc : blocks) {
                Block block = loc.getBlock();
                final ItemStack item0 = UBWPlugin.nativemc.getItemFromBlock(block);
                if (item.isSimilar(item0)) {
                    block.setType(Material.AIR);
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        player.getInventory().addItem(item0);
                    }
                }
            }
        }

        public void redo() {
            commit();
        }
    }
}
