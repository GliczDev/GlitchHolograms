package me.glicz.holograms.command.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
@NoArgsConstructor(staticName = "color")
public class ColorArgumentType implements CustomArgumentType<Color, String> {
    @Override
    public @NotNull Color parse(@NotNull StringReader reader) throws CommandSyntaxException {
        try {
            int color = Integer.parseInt(getNativeType().parse(reader), 16);
            return color >> 24 == 0 ? Color.fromRGB(color) : Color.fromARGB(color);
        } catch (Exception ex) {
            throw new SimpleCommandExceptionType(
                    new LiteralMessage("Invalid hex (a)rgb color")
            ).createWithContext(reader);
        }
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }
}
