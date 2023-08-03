package me.glicz.holograms.line;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemHologramLine extends HologramLine<ItemStack> {
    @Override
    default @NotNull Type getType() {
        return Type.ITEM;
    }
}
