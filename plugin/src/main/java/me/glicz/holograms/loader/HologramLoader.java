package me.glicz.holograms.loader;

import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.util.configurate.LocationSerializer;
import org.bukkit.Location;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public abstract sealed class HologramLoader permits HologramLoader_v1 {
    private static File[] hologramFiles() {
        File hologramsDir = new File(GlitchHolograms.get().getDataFolder(), "holograms");
        if (!hologramsDir.exists() && !hologramsDir.mkdirs()) {
            return new File[0];
        }

        return hologramsDir.listFiles((dir, name) -> {
            if (name.endsWith(".yml") && !dir.getPath().equals(hologramsDir.getPath())) {
                GlitchHolograms.get().getSLF4JLogger().warn(
                        "Please move '{}' directly to the 'holograms' folder in order to load it!",
                        name
                );
                return false;
            }

            return name.endsWith(".yml");
        });
    }

    public static void loadAll() {
        for (File file : hologramFiles()) {
            String fileName = file.getName();
            String id = fileName.substring(0, fileName.lastIndexOf('.'));

            try {
                CommentedConfigurationNode conf = YamlConfigurationLoader.builder()
                        .defaultOptions(options -> options.serializers(builder -> builder
                                .register(Location.class, LocationSerializer.INSTANCE)
                        ))
                        .file(file)
                        .build()
                        .load();

                int version = conf.node("_version").getInt();
                HologramLoader loader = switch (version) {
                    case 1 -> HologramLoader_v1.INSTANCE;
                    default -> {
                        GlitchHolograms.get().getSLF4JLogger().warn(
                                "Invalid '_version' value ({}) in '{}.yml', loader defaulted to the latest version",
                                version, id
                        );
                        yield HologramLoader_v1.INSTANCE;
                    }
                };

                loader.load(id, conf);
            } catch (Exception ex) {
                GlitchHolograms.get().getSLF4JLogger().atError()
                        .setCause(ex)
                        .log("Something went wrong while trying to load hologram '{}'", id);
            }
        }
    }

    abstract void load(String id, CommentedConfigurationNode conf) throws SerializationException;

    abstract void loadLine(Hologram hologram, CommentedConfigurationNode node) throws SerializationException;
}
