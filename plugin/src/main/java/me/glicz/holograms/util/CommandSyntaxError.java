package me.glicz.holograms.util;

import dev.jorel.commandapi.arguments.CustomArgument;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@UtilityClass
public class CommandSyntaxError {
    public static <B> Component build(String message, int cur, CustomArgument.CustomArgumentInfo<B> info) {
        String input = info.previousArgs().fullInput();

        StringBuilder builder = new StringBuilder();
        int cursor = Math.min(input.length(), cur);
        if (cursor > 10) {
            builder.append("...");
        }
        builder.append(input, Math.max(0, cursor - 10), cursor);

        return Component.text()
                .append(Component.text(message, NamedTextColor.RED))
                .appendNewline()
                .append(Component.text(builder.toString(), NamedTextColor.GRAY))
                .append(Component.text()
                        .color(NamedTextColor.RED)
                        .append(Component.text(input.substring(cursor))
                                .decorate(TextDecoration.UNDERLINED))
                        .append(Component.translatable("command.context.here", NamedTextColor.RED)
                                .decorate(TextDecoration.ITALIC))
                )
                .asComponent();
    }
}
