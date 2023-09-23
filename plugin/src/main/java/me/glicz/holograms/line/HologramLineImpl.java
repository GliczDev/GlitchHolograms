package me.glicz.holograms.line;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.Hologram;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Getter
public abstract class HologramLineImpl<T> implements HologramLine<T> {
    protected final int entityId;
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

    @Override
    public void show(@NotNull Player player) {
        if (!hologram.getViewers().contains(player))
            throw new IllegalArgumentException(player.getName());
        GlitchHologramsAPI.get().getNms().sendHologramLine(player, this);
    }

    @Override
    public void hide(@NotNull Player player) {
        GlitchHologramsAPI.get().getNms().sendHologramLineDestroy(player, this);
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
        GlitchHologramsAPI.get().getNms().sendHologramLineData(player, this);
    }

    @Override
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
        hologram.getViewers().forEach(viewer -> GlitchHologramsAPI.get().getNms().sendHologramLineTeleport(viewer, this));
    }

    @AllArgsConstructor
    public enum Property {
        BILLBOARD(rawValue -> EnumUtils.getEnumIgnoreCase(Display.Billboard.class, rawValue, Display.Billboard.FIXED), Display.Billboard.FIXED);

        private final Function<String, Object> converter;
        private final Object defaultValue;
    }

    public static class PropertiesImpl implements Properties {
        private final Map<Property, Object> propertyMap = new HashMap<>();

        public PropertiesImpl() {
            for (Property property : Property.values())
                propertyMap.put(property, property.defaultValue);
        }

        private PropertiesImpl(Map<Property, Object> propertyMap) {
            this.propertyMap.putAll(propertyMap);
        }

        public void set(Property property, String rawValue) {
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
        public Properties copy() {
            return new PropertiesImpl(propertyMap);
        }
    }
}
