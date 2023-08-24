package me.glicz.holograms;

import me.glicz.holograms.line.BlockHologramLine;
import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.line.ItemHologramLine;
import me.glicz.holograms.line.TextHologramLine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Consumer;

public interface Hologram {
    @NotNull
    Location getLocation();

    @NotNull
    @Unmodifiable
    List<HologramLine<?>> getHologramLines();

    default TextHologramLine addHologramLine(@NotNull String content) {
        return addHologramLine(TextHologramLine.class, content);
    }

    default HologramLine<?> addHologramLine(HologramLine.@NotNull Type lineType, @NotNull String content) {
        return addHologramLine(lineType, content, 0.4);
    }

    default <H extends HologramLine<?>> H addHologramLine(@NotNull Class<H> clazz, @NotNull String content) {
        return addHologramLine(clazz, content, 0.4);
    }

    default HologramLine<?> addHologramLine(HologramLine.@NotNull Type lineType, @NotNull String content, double offset) {
        Class<? extends HologramLine<?>> clazz = switch (lineType) {
            case BLOCK -> BlockHologramLine.class;
            case ITEM -> ItemHologramLine.class;
            case TEXT -> TextHologramLine.class;
        };
        return addHologramLine(clazz, content, offset);
    }

    default <H extends HologramLine<?>> H addHologramLine(@NotNull Class<H> clazz, @NotNull String content, double offset) {
        return addHologramLine(clazz, content, offset, line -> {
        });
    }

    default <H extends HologramLine<?>> H addHologramLine(@NotNull Class<H> clazz, @NotNull String content, @NotNull Consumer<H> modifier) {
        return addHologramLine(clazz, content, 0.4, modifier);
    }

    <H extends HologramLine<?>> H addHologramLine(@NotNull Class<H> clazz, @NotNull String content, double offset, @NotNull Consumer<H> modifier);

    default TextHologramLine insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull String content) {
        return insertHologramLine(index, TextHologramLine.class, content);
    }

    default HologramLine<?> insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, HologramLine.@NotNull Type lineType, @NotNull String content) {
        return insertHologramLine(index, lineType, content, 0.4);
    }

    default <H extends HologramLine<?>> H insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull Class<H> clazz, @NotNull String content) {
        return insertHologramLine(index, clazz, content, 0.4);
    }

    default HologramLine<?> insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, HologramLine.@NotNull Type lineType, @NotNull String content, double offset) {
        Class<? extends HologramLine<?>> clazz = switch (lineType) {
            case BLOCK -> BlockHologramLine.class;
            case ITEM -> ItemHologramLine.class;
            case TEXT -> TextHologramLine.class;
        };
        return insertHologramLine(index, clazz, content, offset);
    }

    default <H extends HologramLine<?>> H insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull Class<H> clazz, @NotNull String content, double offset) {
        return insertHologramLine(index, clazz, content, offset, line -> {
        });
    }

    default <H extends HologramLine<?>> H insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull Class<H> clazz, @NotNull String content, @NotNull Consumer<H> modifier) {
        return insertHologramLine(index, clazz, content, 0.4, modifier);
    }

    <H extends HologramLine<?>> H insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull Class<H> clazz, @NotNull String content, double offset, @NotNull Consumer<H> modifier);

    boolean removeHologramLine(int index);

    @NotNull
    @Unmodifiable
    List<Player> getViewers();

    void show(@NotNull Player player);

    void hide(@NotNull Player player);

    void update();

    void update(@NotNull Player player);
}
