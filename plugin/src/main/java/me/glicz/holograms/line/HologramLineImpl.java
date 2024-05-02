package me.glicz.holograms.line;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
                Color.class::cast,
                null,
                () -> ColorArgument.colorArgument("value")),
        GLOWING(
                Boolean.class::cast,
                false,
                () -> new BooleanArgument("value")),
        DISPLAY_HEIGHT(
                Float.class::cast,
                0F,
                () -> new FloatArgument("value")),
        DISPLAY_WIDTH(
                Float.class::cast,
                0F,
                () -> new FloatArgument("value")),
        SHADOW_RADIUS(
                Float.class::cast,
                0F,
                () -> new FloatArgument("value")),
        SHADOW_STRENGTH(
                Float.class::cast,
                1F,
                () -> new FloatArgument("value")),
        VIEW_RANGE(
                Float.class::cast,
                1F,
                () -> new FloatArgument("value")),
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
            for (Property property : Property.values()) {
                propertyMap.put(property, property.defaultValue);
            }
        }

        protected PropertiesImpl(Map<Property, Object> propertyMap) {
            this.propertyMap.putAll(propertyMap);
        }

        public void set(Property property, Object rawValue) {
            propertyMap.put(property, property.converter.apply(rawValue));
        }

        @Override
        public Display.Billboard billboard() {
            return (Display.Billboard) propertyMap.get(Property.BILLBOARD);
        }

        @Override
        public void billboard(Display.Billboard billboard) {
            propertyMap.put(Property.BILLBOARD, billboard);
        }

        @Override
        public Color glowColorOverride() {
            return (Color) propertyMap.get(Property.GLOW_COLOR_OVERRIDE);
        }

        @Override
        public void glowColorOverride(Color glowColorOverride) {
            propertyMap.put(Property.GLOW_COLOR_OVERRIDE, glowColorOverride);
        }

        @Override
        public boolean glowing() {
            return (boolean) propertyMap.get(Property.GLOWING);
        }

        @Override
        public void glowing(boolean glowing) {
            propertyMap.put(Property.GLOWING, glowing);
        }

        @Override
        public float displayHeight() {
            return (float) propertyMap.get(Property.DISPLAY_HEIGHT);
        }

        @Override
        public void displayHeight(float displayHeight) {
            propertyMap.put(Property.DISPLAY_HEIGHT, displayHeight);
        }

        @Override
        public float displayWidth() {
            return (float) propertyMap.get(Property.DISPLAY_WIDTH);
        }

        @Override
        public void displayWidth(float displayWidth) {
            propertyMap.put(Property.DISPLAY_WIDTH, displayWidth);
        }

        @Override
        public float shadowRadius() {
            return (float) propertyMap.get(Property.SHADOW_RADIUS);
        }

        @Override
        public void shadowRadius(float shadowRadius) {
            propertyMap.put(Property.SHADOW_RADIUS, shadowRadius);
        }

        @Override
        public float shadowStrength() {
            return (float) propertyMap.get(Property.SHADOW_STRENGTH);
        }

        @Override
        public void shadowStrength(float shadowStrength) {
            propertyMap.put(Property.SHADOW_STRENGTH, shadowStrength);
        }

        @Override
        public float viewRange() {
            return (float) propertyMap.get(Property.VIEW_RANGE);
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
