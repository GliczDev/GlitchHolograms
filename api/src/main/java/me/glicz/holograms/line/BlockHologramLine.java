package me.glicz.holograms.line;

import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface BlockHologramLine extends HologramLine<BlockData> {
    @Override
    default @NotNull Type type() {
        return Type.BLOCK;
    }
}
