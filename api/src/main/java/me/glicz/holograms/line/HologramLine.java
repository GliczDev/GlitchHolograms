package me.glicz.holograms.line;

import me.glicz.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

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

    default void modifyProperties(Consumer<@NotNull Properties> propertiesConsumer) {
        Properties properties = getProperties();
        propertiesConsumer.accept(properties);
        setProperties(properties);
    }

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

        float getViewRange();

        void setViewRange(float viewRange);

        float getShadowRadius();

        void setShadowRadius(float shadowRadius);

        float getShadowStrength();

        void setShadowStrength(float shadowStrength);

        Properties copy();
    }
}
