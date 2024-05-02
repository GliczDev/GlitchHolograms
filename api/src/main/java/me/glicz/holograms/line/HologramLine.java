package me.glicz.holograms.line;

import me.glicz.holograms.Hologram;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface HologramLine<T> {
    @NotNull
    Type type();

    @NotNull
    Location location();

    @NotNull
    T content(@NotNull Player player);

    @NotNull
    String getRawContent();

    @NotNull
    Hologram getHologram();

    @NotNull
    Properties properties();

    void properties(@NotNull Properties properties);

    default void modifyProperties(Consumer<@NotNull Properties> propertiesConsumer) {
        Properties properties = properties();
        propertiesConsumer.accept(properties);
        properties(properties);
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
        Display.Billboard billboard();

        void billboard(Display.Billboard billboard);

        Color glowColorOverride();

        void glowColorOverride(Color glowColorOverride);

        boolean glowing();

        void glowing(boolean glowing);

        float displayHeight();

        void displayHeight(float displayHeight);

        float displayWidth();

        void displayWidth(float displayWidth);

        float shadowRadius();

        void shadowRadius(float shadowRadius);

        float shadowStrength();

        void shadowStrength(float shadowStrength);

        float viewRange();

        void viewRange(float viewRange);

        Properties copy();
    }
}
