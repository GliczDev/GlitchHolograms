package me.glicz.holograms.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
@NoArgsConstructor(staticName = "miniMessage")
public class MiniMessageArgumentType implements CustomArgumentType.Converted<String, String> {
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
            "nbt",
            "papi"
    ));
    private static final Map<String, TagArguments> tagArgsMap = new HashMap<>(Map.of(
            "nbt", new TagArguments(Set.of("block", "entity", "storage"), false),
            "click", new TagArguments(ClickEvent.Action.NAMES.keys(), false),
            "hover", new TagArguments(HoverEvent.Action.NAMES.keys(), false)
    ));

    static {
        tags.addAll(NamedTextColor.NAMES.keys());
        Set.copyOf(tags).forEach(arg -> {
            if (StandardTags.decorations().has(arg)) {
                tags.add("!" + arg);
            }
        });

        List.of("colour", "color", "c").forEach(tag -> tagArgsMap.put(tag, new TagArguments(NamedTextColor.NAMES.keys(), false)));
        List.of("gradient", "transition").forEach(tag -> tagArgsMap.put(tag, new TagArguments(NamedTextColor.NAMES.keys(), true)));
    }

    @Override
    public @NotNull String convert(@NotNull String nativeType) {
        return nativeType;
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        int tagStart = builder.getRemaining().lastIndexOf('<') + 1;
        String rawArgument = builder.getRemaining().substring(tagStart);
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
                    builder = builder.createOffset(builder.getStart() + builder.getRemaining().lastIndexOf(':') + 1);
                    suggestions = StringUtil.copyPartialMatches(arg, tagArguments.arguments(), new HashSet<>());
                }
            }

            for (String suggestion : suggestions) {
                builder.suggest(suggestion);
            }
        }
        return builder.buildFuture();
    }

    private record TagArguments(Set<String> arguments, boolean chain) {
    }
}
