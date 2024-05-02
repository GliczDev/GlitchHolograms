package me.glicz.holograms;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.glicz.holograms.line.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HologramImpl implements Hologram {
    @Getter
    @Accessors(fluent = true)
    private final String id;
    private final Location location;
    private final List<HologramLineImpl<?>> hologramLines = new ArrayList<>();
    private final List<Player> viewers = new ArrayList<>();
    private int updateRange = 48;

    public HologramImpl(String id, Location location) {
        this.id = id;
        this.location = location;
        this.location.setPitch(0);
        this.location.setYaw(Math.round(this.location.getYaw() / 45) * 45);
    }

    @Override
    public @NotNull Location location() {
        return location.clone();
    }

    @Override
    public int updateRange() {
        return updateRange;
    }

    @Override
    public void updateRange(int updateRange) {
        this.updateRange = updateRange;
    }

    @Override
    public @NotNull List<HologramLine<?>> hologramLines() {
        return List.copyOf(hologramLines);
    }

    private <H extends HologramLine<T>, T> HologramLineImpl<?> classToImpl(Class<H> clazz, String content, double offset) {
        if (clazz.equals(BlockHologramLine.class)) {
            return new BlockHologramLineImpl(this, content, offset);
        } else if (clazz.equals(ItemHologramLine.class)) {
            return new ItemHologramLineImpl(this, content, offset);
        } else if (clazz.equals(TextHologramLine.class)) {
            return new TextHologramLineImpl(this, content, offset);
        } else {
            throw new IllegalArgumentException(clazz.getName());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <H extends HologramLine<T>, T> H addHologramLine(@NotNull Class<H> clazz, @NotNull String content, double offset, @NotNull Consumer<H> modifier) {
        HologramLineImpl<?> hologramLine = classToImpl(clazz, content, offset);
        modifier.accept((H) hologramLine);
        hologramLines.add(hologramLine);
        viewers.forEach(hologramLine::show);
        return (H) hologramLine;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <H extends HologramLine<T>, T> H insertHologramLine(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull Class<H> clazz, @NotNull String content, double offset, @NotNull Consumer<H> modifier) {
        HologramLineImpl<?> hologramLine = classToImpl(clazz, content, offset);
        modifier.accept((H) hologramLine);
        if (hologramLines.size() <= index) {
            hologramLines.add(hologramLine);
        } else {
            hologramLines.add(index, hologramLine);
        }
        hologramLines.forEach(HologramLineImpl::updateLocation);
        viewers.forEach(hologramLine::show);
        return (H) hologramLine;
    }

    @Override
    public boolean removeHologramLine(int index) {
        if (hologramLines.size() <= index) return false;
        viewers.forEach(viewer -> hologramLines.get(index).hide(viewer));
        hologramLines.remove(index);
        hologramLines.forEach(HologramLineImpl::updateLocation);
        return true;
    }

    @Override
    public @NotNull List<Player> viewers() {
        return List.copyOf(viewers);
    }

    @Override
    public void show(@NotNull Player player) {
        viewers.add(player);
        hologramLines.forEach(line -> line.show(player));
    }

    @Override
    public void hide(@NotNull Player player) {
        viewers.remove(player);
        hologramLines.forEach(line -> line.hide(player));
    }

    @Override
    public void update() {
        viewers.forEach(this::update);
    }

    @Override
    public void update(@NotNull Player player) {
        hologramLines.forEach(line -> line.update(player));
    }

    @Override
    public boolean isInUpdateRange(Player player) {
        if (player.getWorld() == location.getWorld()) {
            return player.getLocation().distanceSquared(location) <= updateRange * updateRange;
        }

        return false;
    }
}
