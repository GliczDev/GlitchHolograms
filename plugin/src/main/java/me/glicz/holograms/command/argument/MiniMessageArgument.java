package me.glicz.holograms.command.argument;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.util.StringUtil;

import java.util.*;

@UtilityClass
public class MiniMessageArgument {
    private static final Set<String> tags = new HashSet<>(Set.of(
            "/",
            "colour",
            "color",
            "c",
            "bold",
            "b",
            "italic",
            "em",
            "i",
            "underlined",
            "u",
            "strikethrough",
            "st",
            "obfuscated",
            "obf",
            "reset",
            "click",
            "hover",
            "key",
            "lang",
            "insertion",
            "rainbow",
            "gradient",
            "transition",
            "font",
            "newline",
            "br",
            "selector",
            "score",
            "nbt"
    ));
    private final Map<String, TagArguments> tagArgsMap = new HashMap<>(Map.of(
            "nbt", new TagArguments(Set.of("block", "entity", "storage"), false)
    ));

    static {
        tags.addAll(NamedTextColor.NAMES.keys());
        Set.copyOf(tags).forEach(arg -> {
            if (StandardTags.decorations().has(arg))
                tags.add("!" + arg);
        });
        tagArgsMap.put("click", new TagArguments(ClickEvent.Action.NAMES.keys(), false));
        tagArgsMap.put("hover", new TagArguments(HoverEvent.Action.NAMES.keys(), false));
        List.of("colour", "color", "c").forEach(tag -> tagArgsMap.put(tag, new TagArguments(NamedTextColor.NAMES.keys(), false)));
        List.of("gradient", "transition").forEach(tag -> tagArgsMap.put(tag, new TagArguments(NamedTextColor.NAMES.keys(), true)));
    }

    public static Argument<Component> miniMessageArgument(String nodeName) {
        return new CustomArgument<>(
                new TextArgument(nodeName),
                info -> MiniMessage.miniMessage().deserialize(info.currentInput())
        ).replaceSuggestions((info, builder) -> {
            int tagStart = info.currentArg().lastIndexOf('<') + 1;
            String rawArgument = info.currentArg().substring(tagStart);
            String argument = rawArgument.replaceFirst("/", "");
            if (argument.lastIndexOf('>') == -1 && tagStart > 0) {
                Set<String> suggestions = Set.of();
                int firstArgStart = argument.indexOf(':');
                int lastArgStart = argument.lastIndexOf(':') + 1;
                String tag = argument.substring(0, (firstArgStart > -1) ? firstArgStart : argument.length());
                if (!TagResolver.standard().has(tag) && lastArgStart == 0) {
                    builder = builder.createOffset(builder.getStart() + tagStart + (rawArgument.startsWith("/") ? 1 : 0));
                    suggestions = StringUtil.copyPartialMatches(tag, tags, new HashSet<>());
                } else if (TagResolver.standard().has(tag) && !rawArgument.startsWith("/") && tagArgsMap.containsKey(tag)) {
                    TagArguments tagArguments = tagArgsMap.get(tag);
                    if (tagArguments.chain() || lastArgStart - 1 == firstArgStart) {
                        String arg = argument.substring(lastArgStart);
                        builder = builder.createOffset(builder.getStart() + info.currentArg().lastIndexOf(':') + 1);
                        suggestions = StringUtil.copyPartialMatches(arg, tagArguments.arguments(), new HashSet<>());
                    }
                }
                for (String suggestion : suggestions)
                    builder.suggest(suggestion);
            }
            return builder.buildFuture();
        });
    }

    private record TagArguments(Set<String> arguments, boolean chain) {
    }
}
