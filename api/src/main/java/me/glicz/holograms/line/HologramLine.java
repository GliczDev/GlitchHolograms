package me.glicz.holograms.line;

import me.glicz.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface HologramLine<T> {
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

    void update();

    void update(@NotNull Player player);

    enum Type {
        BLOCK(BlockHologramLine.class),
        ITEM(ItemHologramLine.class),
        TEXT(TextHologramLine.class);

        private final Class<? extends HologramLine<?>> hologramLineClass;

        Type(Class<? extends HologramLine<?>> hologramLineClass) {
            this.hologramLineClass = hologramLineClass;
        }

        @SuppressWarnings("unchecked")
        @ApiStatus.Internal
        public <T> Class<? extends HologramLine<T>> getHologramLineClass() {
            return (Class<? extends HologramLine<T>>) hologramLineClass;
        }
    }

    @ApiStatus.NonExtendable
    interface Properties {
        Display.Billboard getBillboard();

        void setBillboard(Display.Billboard billboard);

        Properties copy();
    }
}
