package me.glicz.holograms.loader;

import com.google.common.base.Preconditions;
import me.glicz.holograms.GlitchHolograms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class HologramLoader {
    public static void loadAll(GlitchHolograms glitchHolograms) {
        File[] files = getHolograms(glitchHolograms);
        if (files == null) return;
        for (File file : files) {
            String fileName = file.getName();
            String id = fileName.substring(0, fileName.lastIndexOf('.'));
            try {
                ConfigurationSection section = YamlConfiguration.loadConfiguration(file);
                int version = section.getInt("_version");
                HologramLoader hologramLoader = switch (version) {
                    case 1 -> new HologramLoader_v1();
                    default -> {
                        glitchHolograms.getLogger().warning(
                                "Invalid '_version' value (%s) in '%s.yml', loader defaulted to the latest version".formatted(version, id)
                        );
                        yield new HologramLoader_v1();
                    }
                };
                hologramLoader.load(glitchHolograms, id, section);
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

    public abstract void load(GlitchHolograms glitchHolograms, String id, ConfigurationSection section);

    protected Location getLocation(ConfigurationSection section) {
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
