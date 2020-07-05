package net.teamfruit.ubw;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class WandItemStage implements ItemStackHolder {
    private ItemStack itemStack;

    @Override
    public void setItem(final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack getItem() {
        return this.itemStack;
    }

    public boolean isWandItem() {
        return itemStack.getType() == Material.STICK;
    }
}
