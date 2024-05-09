package me.glicz.holograms.command.subcommand.line;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.command.subcommand.SubCommand;
import me.glicz.holograms.line.HologramLineImpl;
import me.glicz.holograms.message.MessageKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class LineModifySubCommand implements SubCommand {
    @Override
    public Argument<?> get() {
        Argument<?> argument = new IntegerArgument("index", 0);
        for (HologramLineImpl.Property property : HologramLineImpl.Property.values()) {
            argument.then(new LiteralArgument("property", property.name().toLowerCase())
                    .then(property.getCommandArgument()
                            .executesPlayer(this::execute))
                    .setListed(true)
            );
        }
        return new LiteralArgument("modify").then(argument);
    }

    @Override
    public void execute(Player sender, CommandArguments args) {
        String id = args.getUnchecked("id");
        GlitchHolograms.get().getHologram(id).ifPresentOrElse(
                hologram -> {
                    int index = args.<Integer>getOptionalUnchecked("index").orElseThrow();
                    if (hologram.hologramLines().size() <= index) {
                        sender.sendMessage(GlitchHolograms.get().messageProvider().get(
                                MessageKey.COMMAND_ERROR_INVALID_LINE,
                                Placeholder.parsed("id", String.valueOf(id)),
                                Placeholder.parsed("index", String.valueOf(index))
                        ));
                        return;
                    }

                    String rawProperty = args.<String>getOptionalUnchecked("property").orElseThrow();
                    HologramLineImpl.Property property = HologramLineImpl.Property.valueOf(rawProperty.toUpperCase());
                    Object value = args.getOptional("value").orElseThrow();

                    hologram.hologramLines().get(index).modifyProperties(properties ->
                            ((HologramLineImpl.PropertiesImpl) properties).set(property, value)
                    );

                    sender.sendMessage(GlitchHolograms.get().messageProvider().get(
                            MessageKey.COMMAND_LINE_MODIFY,
                            Placeholder.parsed("id", String.valueOf(id)),
                            Placeholder.parsed("index", String.valueOf(index)),
                            Placeholder.parsed("property", rawProperty),
                            Placeholder.parsed("value", String.valueOf(value))
                    ));
                },
                () -> sender.sendMessage(GlitchHolograms.get().messageProvider().get(
                        MessageKey.COMMAND_ERROR_UNKNOWN_HOLOGRAM,
                        Placeholder.parsed("id", String.valueOf(id))
                ))
        );
    }
}
