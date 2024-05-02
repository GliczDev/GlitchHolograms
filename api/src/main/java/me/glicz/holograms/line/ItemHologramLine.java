package me.glicz.holograms.line;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface ItemHologramLine extends HologramLine<ItemStack> {
    @Override
    default @NotNull Type type() {
        return Type.ITEM;
    }
}
