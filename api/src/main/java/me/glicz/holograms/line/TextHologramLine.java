package me.glicz.holograms.line;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface TextHologramLine extends HologramLine<Component> {
    @Override
    default @NotNull Type getType() {
        return Type.TEXT;
    }
}
