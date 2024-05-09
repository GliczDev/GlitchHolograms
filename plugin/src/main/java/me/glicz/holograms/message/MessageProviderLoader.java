package me.glicz.holograms.message;

import com.google.common.base.Charsets;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.glicz.holograms.GlitchHolograms;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

@UtilityClass
public class MessageProviderLoader {
    @SneakyThrows
    public static MessageProvider load() {
        File file = new File(GlitchHolograms.get().getDataFolder(), "messages.yml");

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .file(file)
                .build();
        CommentedConfigurationNode conf = loader.load();
        conf.mergeFrom(loadDefaults());
        loader.save(conf);

        return new MessageProvider(conf);
    }

    private static ConfigurationNode loadDefaults() throws ConfigurateException {
        InputStream resource = GlitchHolograms.get().getResource("messages.yml");
        if (resource == null) return CommentedConfigurationNode.root();

        return YamlConfigurationLoader.builder()
                .source(() -> new BufferedReader(new InputStreamReader(resource, Charsets.UTF_8)))
                .build()
                .load();
    }
}
