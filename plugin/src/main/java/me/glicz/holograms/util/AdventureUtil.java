package me.glicz.holograms.util;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Function;

@UtilityClass
public class AdventureUtil {
    private static final Function<Player, TagResolver> placeholderResolver = player ->
            TagResolver.builder().tag("papi", (args, ctx) -> {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
                    throw ctx.newException("PlaceholderAPI plugin is not enabled", args);
                }

                String placeholder = args
                        .popOr("Placeholder value is required")
                        .value();
                return Tag.selfClosingInserting(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        PlaceholderAPI.setPlaceholders(player, placeholder)
                                .replace('§', '&')
                ));
            }).build();

    public static Component parseMiniMessage(Player player, String text) {
        return MiniMessage.miniMessage().deserialize(text, placeholderResolver.apply(player));
    }
}
