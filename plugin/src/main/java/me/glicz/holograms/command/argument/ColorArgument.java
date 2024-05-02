package me.glicz.holograms.command.argument;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.experimental.UtilityClass;
import me.glicz.holograms.util.CommandSyntaxError;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Color;

@UtilityClass
public class ColorArgument {
    public static Argument<Color> colorArgument(String nodeName) {
        return new CustomArgument<>(
                new StringArgument(nodeName),
                info -> {
                    try {
                        int color = Integer.parseInt(info.input(), 16);
                        return color >> 24 == 0 ? Color.fromRGB(color) : Color.fromARGB(color);
                    } catch (Exception ex) {
                        throw CustomArgument.CustomArgumentException.fromAdventureComponent(CommandSyntaxError.build(
                                "Invalid hex (a)rgb color",
                                StringUtils.removeEnd(info.previousArgs().fullInput(), info.input()).length(),
                                info
                        ));
                    }
                }
        );
    }
}
