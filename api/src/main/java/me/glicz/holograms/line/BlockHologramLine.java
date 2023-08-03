package me.glicz.holograms.line;

import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public interface BlockHologramLine extends HologramLine<BlockData> {
    @Override
    default @NotNull Type getType() {
        return Type.BLOCK;
    }
}
