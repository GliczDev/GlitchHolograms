package me.glicz.holograms.loader;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.line.HologramLineImpl;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HologramLoader_v1 extends HologramLoader {
    protected static void loadLine(GlitchHolograms glitchHolograms, Hologram hologram, Map<?, ?> map) {
        HologramLine.Type type = Preconditions.checkNotNull(
                EnumUtils.getEnum(HologramLine.Type.class, String.valueOf(map.get("type"))),
                "No valid type is present"
        );
        String content = Preconditions.checkNotNull(
                String.valueOf(map.get("content")),
                "No content is present"
        );
        double offset = Objects.requireNonNullElse(((Number) map.get("offset")), 0.35).doubleValue();
        hologram.addHologramLine(type.getHologramLineClass(), content, offset, line -> {
            HologramLineImpl.PropertiesImpl properties = (HologramLineImpl.PropertiesImpl) line.getProperties();
            Objects.requireNonNullElse((Map<?, ?>) map.get("properties"), Map.of()).forEach((key, value) -> {
                HologramLineImpl.Property property = EnumUtils.getEnumIgnoreCase(HologramLineImpl.Property.class, String.valueOf(key));
                if (property == null) {
                    glitchHolograms.getLogger().warning(
                            "Unknown property: %s".formatted(key)
                    );
                    return;
                }
                properties.set(property, value);
            });
            line.setProperties(properties);
        });
    }

    @Override
    public void load(GlitchHolograms glitchHolograms, String id, ConfigurationSection section) {
        Location location = getLocation(Preconditions.checkNotNull(
                section.getConfigurationSection("location"), "Location is not specified"
        ));
        Hologram hologram = GlitchHologramsAPI.get().createHologram(id, location, true);
        List<Map<?, ?>> lines = section.getMapList("lines");
        Collections.reverse(lines);
        lines.forEach(map -> {
            int lineIndex = lines.indexOf(map);
            try {
                loadLine(glitchHolograms, hologram, map);
            } catch (Throwable throwable) {
                glitchHolograms.getSLF4JLogger().error(
                        "Something went wrong while trying to load line of index %s (counted from the bottom) in hologram '%s'"
                                .formatted(lineIndex, id),
                        throwable
                );
            }
        });
    }
}
