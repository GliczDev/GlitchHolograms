package me.glicz.holograms.line;

import me.glicz.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface HologramLine<T> {
    @ApiStatus.Internal
    int getEntityId();

    @ApiStatus.Internal
    UUID getUniqueId();

    @NotNull
    Type getType();

    @NotNull
    Location getLocation();

    @NotNull
    T getContent();

    @NotNull
    Hologram getHologram();

    @NotNull
    Properties getProperties();

    @ApiStatus.Internal
    void show(@NotNull Player player);

    @ApiStatus.Internal
    void hide(@NotNull Player player);

    void update();

    void update(@NotNull Player player);

    @ApiStatus.Internal
    void updateLocation();

    enum Type {
        BLOCK,
        ITEM,
        TEXT
    }

    interface Properties {
        Display.Billboard getBillboard();

        void setBillboard(Display.Billboard billboard);
    }
}
