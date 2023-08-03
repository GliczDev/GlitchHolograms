package me.glicz.holograms;

import lombok.Getter;
import me.glicz.holograms.line.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class HologramImpl implements Hologram {
    private final Location location;
    private final List<HologramLine<?>> hologramLines = new ArrayList<>();
    private final List<Player> viewers = new ArrayList<>();

    public HologramImpl(Location location) {
        this.location = location;
    }

    @Override
    public @NotNull List<HologramLine<?>> getHologramLines() {
        return Collections.unmodifiableList(hologramLines);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <H extends HologramLine<T>, T> H addHologramLine(@NotNull Class<H> clazz, @NotNull T content, double offset, Consumer<H> modifier) {
        final H hologramLine;
        if (clazz.equals(BlockHologramLine.class))
            hologramLine = (H) new BlockHologramLineImpl(this, (BlockData) content, offset);
        else if (clazz.equals(ItemHologramLine.class))
            hologramLine = (H) new ItemHologramLineImpl(this, (ItemStack) content, offset);
        else if (clazz.equals(TextHologramLine.class))
            hologramLine = (H) new TextHologramLineImpl(this, (Component) content, offset);
        else throw new IllegalArgumentException(clazz.getName());
        modifier.accept(hologramLine);
        hologramLines.add(hologramLine);
        viewers.forEach(hologramLine::show);
        return hologramLine;
    }

    @Override
    public boolean removeHologramLine(int index) {
        if (hologramLines.size() <= index) return false;
        viewers.forEach(viewer -> hologramLines.get(index).hide(viewer));
        hologramLines.remove(index);
        hologramLines.forEach(HologramLine::updateLocation);
        return true;
    }

    @Override
    public @NotNull Location getLocation() {
        return location.clone();
    }

    @Override
    public @NotNull List<Player> getViewers() {
        return Collections.unmodifiableList(viewers);
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
}
