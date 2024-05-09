package me.glicz.holograms.loader;

import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.line.HologramLineImpl;
import me.glicz.holograms.task.AsyncFileSaveTask;
import me.glicz.holograms.util.configurate.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public abstract sealed class HologramLoader permits HologramLoader_v1 {
    private static final int LATEST_LOADER_VERSION = 1;

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
        Bukkit.getScheduler().runTaskLaterAsynchronously(GlitchHolograms.get(), AsyncFileSaveTask::saveAll, 1);
    }

    public static void save(Hologram hologram) {
        if (!hologram.shouldSave()) return;
        Bukkit.getScheduler().runTaskAsynchronously(GlitchHolograms.get(), () -> {
            try {
                ConfigurationOptions options = ConfigurationOptions.defaults().serializers(builder -> builder
                        .register(Location.class, LocationSerializer.INSTANCE)
                );
                CommentedConfigurationNode conf = CommentedConfigurationNode.root(options);

                conf.node("_version").set(LATEST_LOADER_VERSION);
                conf.node("location").set(hologram.location());
                conf.node("update-range").set(hologram.updateRange());

                for (HologramLine<?> line : hologram.hologramLines().reversed()) {
                    CommentedConfigurationNode node = conf.node("lines").appendListNode();

                    node.node("type").set(line.type().name());
                    node.node("content").set(line.rawContent());
                    node.node("offset").set(line.offset());

                    HologramLineImpl.PropertiesImpl properties = (HologramLineImpl.PropertiesImpl) line.properties();
                    for (HologramLineImpl.Property property : HologramLineImpl.Property.values()) {
                        node.node("properties").node(property.name()).set(properties.getSerialized(property));
                    }
                }

                AsyncFileSaveTask.save(new File(GlitchHolograms.get().getDataFolder(), "holograms/" + hologram.id() + ".yml"), conf);
            } catch (Exception ex) {
                GlitchHolograms.get().getSLF4JLogger().atError()
                        .setCause(ex)
                        .log("Something went wrong while trying to save hologram '{}'", hologram.id());
            }
        });
    }

    abstract void load(String id, CommentedConfigurationNode conf) throws SerializationException;

    abstract void loadLine(Hologram hologram, CommentedConfigurationNode node) throws SerializationException;
}
