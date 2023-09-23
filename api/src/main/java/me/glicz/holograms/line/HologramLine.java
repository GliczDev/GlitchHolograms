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
    T getContent(@NotNull Player player);

    @NotNull
    String getRawContent();

    @NotNull
    Hologram getHologram();

    @NotNull
    Properties getProperties();

    void setProperties(@NotNull Properties properties);

    @ApiStatus.Internal
    void show(@NotNull Player player);

    @ApiStatus.Internal
    void hide(@NotNull Player player);

    void update();

    void update(@NotNull Player player);

    @ApiStatus.Internal
    void updateLocation();

    enum Type {
        BLOCK(BlockHologramLine.class),
        ITEM(ItemHologramLine.class),
        TEXT(TextHologramLine.class);

        private final Class<? extends HologramLine<?>> hologramLineClass;

        Type(Class<? extends HologramLine<?>> hologramLineClass) {
            this.hologramLineClass = hologramLineClass;
        }

        public Class<? extends HologramLine<?>> getHologramLineClass() {
            return hologramLineClass;
        }
    }

    interface Properties {
        Display.Billboard getBillboard();

        void setBillboard(Display.Billboard billboard);

        Properties copy();
    }
}
