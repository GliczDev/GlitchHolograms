package me.glicz.holograms.loader;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.line.HologramLineImpl;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class HologramLoader {
    public static void loadAll(GlitchHolograms glitchHolograms) {
        File[] files = getHolograms(glitchHolograms);
        if (files == null) return;
        for (File file : files) {
            String fileName = file.getName();
            String id = fileName.substring(0, fileName.lastIndexOf('.'));
            try {
                load(glitchHolograms, id, YamlConfiguration.loadConfiguration(file));
            } catch (Throwable throwable) {
                glitchHolograms.getSLF4JLogger().error(
                        "Something went wrong while trying to load hologram '%s'".formatted(id), throwable
                );
            }
        }
    }

    private static File[] getHolograms(GlitchHolograms glitchHolograms) {
        File hologramsDir = new File(glitchHolograms.getDataFolder(), "holograms");
        if (!hologramsDir.exists() && !hologramsDir.mkdirs())
            return null;
        return hologramsDir.listFiles((dir, name) -> {
            if (name.endsWith(".yml") && !dir.getPath().equals(hologramsDir.getPath())) {
                glitchHolograms.getLogger().warning(
                        "Please move '%s' directly to the 'holograms' folder in order to load it!".formatted(name)
                );
                return false;
            }
            return name.endsWith(".yml");
        });
    }

    public static void load(GlitchHolograms glitchHolograms, String id, ConfigurationSection section) {
        int version = section.getInt("_version");
        switch (version) {
            case 1 -> load_v1(glitchHolograms, id, section);
            default -> {
                glitchHolograms.getLogger().warning(
                        "Invalid '_version' value (%s) in '%s.yml', loader defaulted to the latest version".formatted(version, id)
                );
                load_v1(glitchHolograms, id, section);
            }
        }
    }

    private static void load_v1(GlitchHolograms glitchHolograms, String id, ConfigurationSection section) {
        Location location = getLocation(Preconditions.checkNotNull(
                section.getConfigurationSection("location"), "Location is not specified"
        ));
        Hologram hologram = GlitchHologramsAPI.get().createHologram(id, location, true);
        List<Map<?, ?>> lines = section.getMapList("lines");
        Collections.reverse(lines);
        lines.forEach(map -> {
            int lineIndex = lines.indexOf(map);
            try {
                loadLine_v1(glitchHolograms, hologram, map);
            } catch (Throwable throwable) {
                glitchHolograms.getSLF4JLogger().error(
                        "Something went wrong while trying to load line of index %s (counted from the bottom) in hologram '%s'"
                                .formatted(lineIndex, id),
                        throwable
                );
            }
        });
    }

    private static void loadLine_v1(GlitchHolograms glitchHolograms, Hologram hologram, Map<?, ?> map) {
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
                properties.set(property, String.valueOf(value));
            });
            line.setProperties(properties);
        });
    }

    private static Location getLocation(ConfigurationSection section) {
        String worldName = section.getString("world", "");
        World world = Preconditions.checkNotNull(Bukkit.getWorld(worldName), "Unknown world: %s".formatted(worldName));
        return new Location(
                world,
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z")
        );
    }
}
