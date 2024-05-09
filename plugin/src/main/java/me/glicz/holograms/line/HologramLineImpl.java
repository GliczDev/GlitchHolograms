package me.glicz.holograms.line;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.command.argument.ColorArgument;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
@Accessors(fluent = true)
public abstract class HologramLineImpl<T> implements HologramLine<T> {
    protected final String rawContent;
    protected final Hologram hologram;
    protected final double offset;
    @Getter(AccessLevel.PROTECTED)
    private final Display entity;
    protected Location location;

    public HologramLineImpl(Hologram hologram, String rawContent, double offset) {
        this.rawContent = rawContent;
        this.hologram = hologram;
        this.offset = offset;

        updateLocation();
        this.entity = location.getWorld().createEntity(location, entityClass());
    }

    protected abstract Class<? extends Display> entityClass();

    @Override
    public @NotNull Location location() {
        return location.clone();
    }

    public void show(@NotNull Player player) {
        if (!hologram.viewers().contains(player)) {
            throw new IllegalArgumentException(player.getName());
        }

        if (player.getWorld() == location.getWorld()) {
            GlitchHolograms.get().nmsBridge().sendHologramLine(player, entity, content(player));
        }
    }

    public void hide(@NotNull Player player) {
        GlitchHolograms.get().nmsBridge().sendHologramLineDestroy(player, entity.getEntityId());
    }

    @Override
    public void update() {
        hologram.viewers().forEach(this::update);
    }

    @Override
    public void update(@NotNull Player player) {
        if (!hologram.hologramLines().contains(this)) return;
        if (!hologram.viewers().contains(player)) {
            throw new IllegalArgumentException(player.getName());
        }

        if (hologram.isInUpdateRange(player)) {
            GlitchHolograms.get().nmsBridge().sendHologramLineData(player, entity, content(player));
        }
    }

    public void updateLocation() {
        double y = hologram.location().getY();
        int index = hologram.hologramLines().contains(this)
                ? hologram.hologramLines().indexOf(this)
                : hologram.hologramLines().size();
        if (index > 0) {
            y = hologram.hologramLines().get(index - 1).location().getY() + offset;
        }
        Location loc = hologram.location();
        loc.setY(y);

        if (this instanceof BlockHologramLine) {
            loc.add(-0.5, 0, -0.5);
        }

        this.location = loc;

        if (entity != null) {
            GlitchHolograms.get().nmsBridge().moveEntity(entity, location);
            hologram.viewers().forEach(viewer -> {
                if (hologram.isInUpdateRange(viewer)) {
                    GlitchHolograms.get().nmsBridge().sendHologramLineTeleport(viewer, entity);
                }
            });
        }
    }

    protected void updateEntityData() {
        Properties properties = properties();
        entity.setBillboard(properties.billboard());
        entity.setGlowColorOverride(properties.glowColorOverride());
        entity.setGlowing(properties.glowing());
        entity.setDisplayHeight(properties.displayHeight());
        entity.setDisplayWidth(properties.displayWidth());
        entity.setShadowRadius(properties.shadowRadius());
        entity.setShadowStrength(properties.shadowStrength());
        entity.setViewRange(properties.viewRange());
    }

    @AllArgsConstructor
    public enum Property {
        BILLBOARD(
                billboard -> ((Display.Billboard) billboard).name(),
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
        GLOW_COLOR_OVERRIDE(
                color -> Integer.toHexString(((Color) color).asARGB()),
                rawValue -> {
                    if (rawValue instanceof Color color) {
                        return color;
                    }

                    return Color.fromARGB(Integer.parseInt(String.valueOf(rawValue)));
                },
                null,
                () -> ColorArgument.colorArgument("value")),
        GLOWING(
                Function.identity(),
                Boolean.class::cast,
                false,
                () -> new BooleanArgument("value")),
        DISPLAY_HEIGHT(
                Function.identity(),
                rawValue -> ((Number) rawValue).floatValue(),
                0F,
                () -> new FloatArgument("value")),
        DISPLAY_WIDTH(
                Function.identity(),
                rawValue -> ((Number) rawValue).floatValue(),
                0F,
                () -> new FloatArgument("value")),
        SHADOW_RADIUS(
                Function.identity(),
                rawValue -> ((Number) rawValue).floatValue(),
                0F,
                () -> new FloatArgument("value")),
        SHADOW_STRENGTH(
                Function.identity(),
                rawValue -> ((Number) rawValue).floatValue(),
                1F,
                () -> new FloatArgument("value")),
        VIEW_RANGE(
                Function.identity(),
                rawValue -> ((Number) rawValue).floatValue(),
                1F,
                () -> new FloatArgument("value")),
        ;

        private final Function<Object, Object> serializer, deserializer;
        private final Object defaultValue;
        private final Supplier<Argument<?>> argumentSupplier;

        public Argument<?> getCommandArgument() {
            return argumentSupplier.get();
        }
    }

    @NoArgsConstructor
    public static class PropertiesImpl implements Properties {
        protected final Map<Property, Object> propertyMap = new HashMap<>();

        protected PropertiesImpl(Map<Property, Object> propertyMap) {
            this.propertyMap.putAll(propertyMap);
        }

        public Object get(Property property) {
            return propertyMap.get(property);
        }

        public Object getOrDefault(Property property) {
            return propertyMap.getOrDefault(property, property.defaultValue);
        }

        public Object getSerialized(Property property) {
            Object o = propertyMap.get(property);
            return o != null ? property.serializer.apply(o) : null;
        }

        public void set(Property property, Object rawValue) {
            propertyMap.put(property, property.deserializer.apply(rawValue));
        }

        @Override
        public Display.Billboard billboard() {
            return (Display.Billboard) getOrDefault(Property.BILLBOARD);
        }

        @Override
        public void billboard(Display.Billboard billboard) {
            propertyMap.put(Property.BILLBOARD, billboard);
        }

        @Override
        public Color glowColorOverride() {
            return (Color) getOrDefault(Property.GLOW_COLOR_OVERRIDE);
        }

        @Override
        public void glowColorOverride(Color glowColorOverride) {
            propertyMap.put(Property.GLOW_COLOR_OVERRIDE, glowColorOverride);
        }

        @Override
        public boolean glowing() {
            return (boolean) getOrDefault(Property.GLOWING);
        }

        @Override
        public void glowing(boolean glowing) {
            propertyMap.put(Property.GLOWING, glowing);
        }

        @Override
        public float displayHeight() {
            return (float) getOrDefault(Property.DISPLAY_HEIGHT);
        }

        @Override
        public void displayHeight(float displayHeight) {
            propertyMap.put(Property.DISPLAY_HEIGHT, displayHeight);
        }

        @Override
        public float displayWidth() {
            return (float) getOrDefault(Property.DISPLAY_WIDTH);
        }

        @Override
        public void displayWidth(float displayWidth) {
            propertyMap.put(Property.DISPLAY_WIDTH, displayWidth);
        }

        @Override
        public float shadowRadius() {
            return (float) getOrDefault(Property.SHADOW_RADIUS);
        }

        @Override
        public void shadowRadius(float shadowRadius) {
            propertyMap.put(Property.SHADOW_RADIUS, shadowRadius);
        }

        @Override
        public float shadowStrength() {
            return (float) getOrDefault(Property.SHADOW_STRENGTH);
        }

        @Override
        public void shadowStrength(float shadowStrength) {
            propertyMap.put(Property.SHADOW_STRENGTH, shadowStrength);
        }

        @Override
        public float viewRange() {
            return (float) getOrDefault(Property.VIEW_RANGE);
        }

        @Override
        public void viewRange(float viewRange) {
            propertyMap.put(Property.VIEW_RANGE, viewRange);
        }

        @Override
        public Properties copy() {
            return new PropertiesImpl(propertyMap);
        }
    }
}
