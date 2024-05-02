package me.glicz.holograms.loader;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.line.HologramLineImpl;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Location;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class HologramLoader_v1 extends HologramLoader {
    static final HologramLoader INSTANCE = new HologramLoader_v1();

    @Override
    void load(String id, CommentedConfigurationNode conf) throws SerializationException {
        Location location = Objects.requireNonNull(
                conf.node("location").get(Location.class),
                "Location is not specified"
        );
        Hologram hologram = GlitchHologramsAPI.get().createHologram(id, location, true, holo -> {
            if (!conf.node("update-range").virtual()) {
                holo.updateRange(conf.node("update-range").getInt());
            }
        });

        List<CommentedConfigurationNode> lines = conf.node("lines").childrenList().reversed();
        lines.forEach(node -> {
            try {
                loadLine(hologram, node);
            } catch (Exception ex) {
                GlitchHolograms.get().getSLF4JLogger().atError()
                        .setCause(ex)
                        .log("Something went wrong while trying to load line {} in hologram '{}'", lines.indexOf(node), id);
            }
        });
    }

    @Override
    void loadLine(Hologram hologram, CommentedConfigurationNode node) {
        HologramLine.Type type = Objects.requireNonNull(
                EnumUtils.getEnumIgnoreCase(HologramLine.Type.class, node.node("type").getString()),
                "No valid type is present"
        );
        String content = Objects.requireNonNull(
                node.node("content").getString(),
                "No content is present"
        );
        double offset = node.node("offset").getDouble(0.35);

        hologram.addHologramLine(type.getHologramLineClass(), content, offset, line -> {
            HologramLineImpl.PropertiesImpl properties = (HologramLineImpl.PropertiesImpl) line.properties();

            node.node("properties").childrenMap().forEach((key, value) -> {
                HologramLineImpl.Property property = EnumUtils.getEnumIgnoreCase(HologramLineImpl.Property.class, String.valueOf(key));
                if (property == null) {
                    GlitchHolograms.get().getSLF4JLogger().warn("Unknown property: {}", key);
                    return;
                }

                properties.set(property, value.raw());
            });

            line.properties(properties);
        });
    }
}
