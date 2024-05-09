package me.glicz.holograms;

import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.line.TextHologramLine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface Hologram {
    @NotNull
    String id();

    @NotNull
    Location location();

    boolean shouldSave();

    int updateRange();

    void updateRange(int updateRange);

    @NotNull
    @Unmodifiable
    List<HologramLine<?>> hologramLines();

    default TextHologramLine addHologramLine(@NotNull String content) {
        return addHologramLine(TextHologramLine.class, content);
    }

    default HologramLine<?> addHologramLine(HologramLine.@NotNull Type lineType, @NotNull String content) {
        return addHologramLine(lineType, content, GlitchHologramsAPI.get().config().defaults().lineOffset());
    }

    default <H extends HologramLine<T>, T> H addHologramLine(@NotNull Class<H> clazz, @NotNull String content) {
        return addHologramLine(clazz, content, GlitchHologramsAPI.get().config().defaults().lineOffset());
    }

    default HologramLine<?> addHologramLine(HologramLine.@NotNull Type lineType, @NotNull String content, double offset) {
        return addHologramLine(lineType.getHologramLineClass(), content, offset);
    }

    default <H extends HologramLine<T>, T> H addHologramLine(@NotNull Class<H> clazz, @NotNull String content, double offset) {
        return addHologramLine(clazz, content, offset, line -> {
        });
    }

    default <H extends HologramLine<T>, T> H addHologramLine(@NotNull Class<H> clazz, @NotNull String content, @NotNull Consumer<H> modifier) {
        return addHologramLine(clazz, content, GlitchHologramsAPI.get().config().defaults().lineOffset(), modifier);
    }

    <H extends HologramLine<T>, T> H addHologramLine(@NotNull Class<H> clazz, @NotNull String content, double offset, @NotNull Consumer<H> modifier);

    default TextHologramLine insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull String content) {
        return insertHologramLine(index, TextHologramLine.class, content);
    }

    default HologramLine<?> insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, HologramLine.@NotNull Type lineType, @NotNull String content) {
        return insertHologramLine(index, lineType, content, GlitchHologramsAPI.get().config().defaults().lineOffset());
    }

    default <H extends HologramLine<T>, T> H insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull Class<H> clazz, @NotNull String content) {
        return insertHologramLine(index, clazz, content, GlitchHologramsAPI.get().config().defaults().lineOffset());
    }

    default HologramLine<?> insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, HologramLine.@NotNull Type lineType, @NotNull String content, double offset) {
        return insertHologramLine(index, lineType.getHologramLineClass(), content, offset);
    }

    default <H extends HologramLine<T>, T> H insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull Class<H> clazz, @NotNull String content, double offset) {
        return insertHologramLine(index, clazz, content, offset, line -> {
        });
    }

    default <H extends HologramLine<T>, T> H insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull Class<H> clazz, @NotNull String content, @NotNull Consumer<H> modifier) {
        return insertHologramLine(index, clazz, content, GlitchHologramsAPI.get().config().defaults().lineOffset(), modifier);
    }

    <H extends HologramLine<T>, T> H insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull Class<H> clazz, @NotNull String content, double offset, @NotNull Consumer<H> modifier);

    boolean removeHologramLine(int index);

    @NotNull
    Set<Player> viewers();

    void show(@NotNull Player player);

    void hide(@NotNull Player player);

    void update();

    void update(@NotNull Player player);

    boolean isInUpdateRange(Player player);
}
