package me.glicz.holograms.message;

import me.glicz.holograms.GlitchHolograms;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Objects;

public class MessageProvider {
    private final ConfigurationNode node;
    private final TagResolver[] placeholders;

    MessageProvider(ConfigurationNode node) {
        this.node = node;

        this.placeholders = this.node.node("placeholders").childrenMap().entrySet().stream()
                .map(entry -> {
                    //noinspection PatternValidation
                    String key = String.valueOf(entry.getKey());
                    try {
                        String value = entry.getValue().getString();
                        if (value != null) {
                            //noinspection PatternValidation
                            return Placeholder.parsed(key, value);
                        }
                    } catch (IllegalArgumentException ex) {
                        GlitchHolograms.get().getSLF4JLogger().atError()
                                .setCause(ex)
                                .log("Failed to load messages placeholder '{}'", key);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toArray(TagResolver[]::new);
    }

    public Component get(MessageKey key, TagResolver... tags) {
        return MiniMessage.miniMessage().deserialize(
                get0(key), ArrayUtils.addAll(placeholders, tags)
        );
    }

    private String get0(MessageKey key) {
        return node.node((Object[]) key.path.split("\\.")).getString();
    }
}
