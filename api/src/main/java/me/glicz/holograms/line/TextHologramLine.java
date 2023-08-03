package me.glicz.holograms.line;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface TextHologramLine extends HologramLine<Component> {
    @Override
    default @NotNull Type getType() {
        return Type.TEXT;
    }
}
