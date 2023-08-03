package me.glicz.holograms;

import me.glicz.holograms.line.BlockHologramLine;
import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.line.ItemHologramLine;
import me.glicz.holograms.line.TextHologramLine;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Consumer;

public interface Hologram {
    @NotNull
    Location getLocation();

    @NotNull
    @Unmodifiable
    List<HologramLine<?>> getHologramLines();

    default TextHologramLine addHologramLine(Component content) {
        return addHologramLine(TextHologramLine.class, content);
    }

    default HologramLine<?> addHologramLine(HologramLine.@NotNull Type lineType, @NotNull Object content) {
        return addHologramLine(lineType, content, 0.4);
    }

    default <H extends HologramLine<T>, T> H addHologramLine(@NotNull Class<H> clazz, @NotNull T content) {
        return addHologramLine(clazz, content, 0.4);
    }

    @SuppressWarnings("unchecked")
    default <T> HologramLine<?> addHologramLine(HologramLine.@NotNull Type lineType, @NotNull T content, double offset) {
        Class<? extends HologramLine<T>> clazz = (Class<? extends HologramLine<T>>) switch (lineType) {
            case BLOCK -> BlockHologramLine.class;
            case ITEM -> ItemHologramLine.class;
            case TEXT -> TextHologramLine.class;
        };
        return addHologramLine(clazz, content, offset);
    }

    default <H extends HologramLine<T>, T> H addHologramLine(@NotNull Class<H> clazz, @NotNull T content, double offset) {
        return addHologramLine(clazz, content, offset, line -> {
        });
    }

    default <H extends HologramLine<T>, T> H addHologramLine(@NotNull Class<H> clazz, @NotNull T content, Consumer<H> modifier) {
        return addHologramLine(clazz, content, 0.4, modifier);
    }

    <H extends HologramLine<T>, T> H addHologramLine(@NotNull Class<H> clazz, @NotNull T content, double offset, Consumer<H> modifier);

    boolean removeHologramLine(int index);

    @NotNull
    @Unmodifiable
    List<Player> getViewers();

    void show(@NotNull Player player);

    void hide(@NotNull Player player);
}
