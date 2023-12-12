package me.glicz.holograms.line;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.Hologram;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
public abstract class HologramLineImpl<T> implements HologramLine<T> {
    @Getter(AccessLevel.NONE)
    protected final int entityId;
    @Getter(AccessLevel.NONE)
    protected final UUID uniqueId;
    protected final String rawContent;
    protected final Hologram hologram;
    protected final double offset;
    protected Location location;

    @SuppressWarnings("deprecation")
    public HologramLineImpl(Hologram hologram, String rawContent, double offset) {
        this.entityId = Bukkit.getUnsafe().nextEntityId();
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (Bukkit.getEntity(uuid) != null);
        this.uniqueId = uuid;
        this.rawContent = rawContent;
        this.hologram = hologram;
        this.offset = offset;
        updateLocation();
    }

    @Override
    public @NotNull Location getLocation() {
        return location.clone();
    }

    public void show(@NotNull Player player) {
        if (!hologram.getViewers().contains(player))
            throw new IllegalArgumentException(player.getName());
        GlitchHolograms.getNms().sendHologramLine(player, entityId, uniqueId, this);
    }

    public void hide(@NotNull Player player) {
        GlitchHolograms.getNms().sendHologramLineDestroy(player, entityId);
    }

    @Override
    public void update() {
        hologram.getViewers().forEach(this::update);
    }

    @Override
    public void update(@NotNull Player player) {
        if (!hologram.getHologramLines().contains(this)) return;
        if (!hologram.getViewers().contains(player))
            throw new IllegalArgumentException(player.getName());
        GlitchHolograms.getNms().sendHologramLineData(player, entityId, this);
    }

    public void updateLocation() {
        double y = hologram.getLocation().getY();
        int index = hologram.getHologramLines().contains(this)
                ? hologram.getHologramLines().indexOf(this)
                : hologram.getHologramLines().size();
        if (index > 0)
            y = hologram.getHologramLines().get(index - 1).getLocation().getY() + offset;
        Location loc = hologram.getLocation();
        loc.setY(y);
        this.location = loc;
        hologram.getViewers().forEach(viewer -> GlitchHolograms.getNms().sendHologramLineTeleport(viewer, entityId, this));
    }

    @AllArgsConstructor
    public enum Property {
        BILLBOARD(
                rawValue -> EnumUtils.getEnumIgnoreCase(
                        Display.Billboard.class, String.valueOf(rawValue), Display.Billboard.FIXED
                ),
                Display.Billboard.FIXED,
                () -> {
                    String[] values = Arrays.stream(Display.Billboard.values())
                            .map(billboard -> billboard.name().toLowerCase())
                            .toArray(String[]::new);
                    return new MultiLiteralArgument("value", values);
                }),
        SHADOW_RADIUS(
                Float.class::cast,
                0,
                () -> new FloatArgument("value", 0, 64)),
        SHADOW_STRENGTH(
                Float.class::cast,
                0,
                () -> new FloatArgument("value", 0)),
        VIEW_RANGE(
                Float.class::cast,
                1,
                () -> new FloatArgument("value", 0)),
        ;

        private final Function<Object, Object> converter;
        private final Object defaultValue;
        private final Supplier<Argument<?>> argumentSupplier;

        public Argument<?> getCommandArgument() {
            return argumentSupplier.get();
        }
    }

    public static class PropertiesImpl implements Properties {
        protected final Map<Property, Object> propertyMap = new HashMap<>();

        public PropertiesImpl() {
            for (Property property : Property.values())
                propertyMap.put(property, property.defaultValue);
        }

        protected PropertiesImpl(Map<Property, Object> propertyMap) {
            this.propertyMap.putAll(propertyMap);
        }

        public void set(Property property, Object rawValue) {
            propertyMap.put(property, property.converter.apply(rawValue));
        }

        @Override
        public Display.Billboard getBillboard() {
            return (Display.Billboard) propertyMap.get(Property.BILLBOARD);
        }

        @Override
        public void setBillboard(Display.Billboard billboard) {
            propertyMap.put(Property.BILLBOARD, billboard);
        }

        @Override
        public float getViewRange() {
            return (float) propertyMap.get(Property.VIEW_RANGE);
        }

        @Override
        public void setViewRange(float viewRange) {
            propertyMap.put(Property.VIEW_RANGE, viewRange);
        }

        @Override
        public float getShadowRadius() {
            return (float) propertyMap.get(Property.SHADOW_RADIUS);
        }

        @Override
        public void setShadowRadius(float shadowRadius) {
            propertyMap.put(Property.SHADOW_RADIUS, shadowRadius);
        }

        @Override
        public float getShadowStrength() {
            return (float) propertyMap.get(Property.SHADOW_STRENGTH);
        }

        @Override
        public void setShadowStrength(float shadowStrength) {
            propertyMap.put(Property.SHADOW_STRENGTH, shadowStrength);
        }

        @Override
        public Properties copy() {
            return new PropertiesImpl(propertyMap);
        }
    }
}
