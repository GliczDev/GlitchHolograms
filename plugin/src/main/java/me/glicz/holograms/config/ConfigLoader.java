package me.glicz.holograms.config;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.glicz.holograms.GlitchHolograms;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

@UtilityClass
public class ConfigLoader {
    @SneakyThrows
    public static ConfigImpl load() {
        File file = new File(GlitchHolograms.get().getDataFolder(), "config.yml");

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .file(file)
                .build();
        CommentedConfigurationNode conf = loader.load();

        ConfigImpl config = conf.get(ConfigImpl.class);
        conf.set(config);

        loader.save(conf);

        return config;
    }
}