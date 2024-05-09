package me.glicz.holograms.command.subcommand.line;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.command.subcommand.SubCommand;
import me.glicz.holograms.message.MessageKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class LineRemoveSubCommand implements SubCommand {
    @Override
    public Argument<?> get() {
        return new LiteralArgument("action", "remove")
                .then(new IntegerArgument("index", 0)
                        .executesPlayer(this::execute));
    }

    @Override
    public void execute(Player sender, CommandArguments args) {
        String id = args.getUnchecked("id");
        GlitchHolograms.get().getHologram(id).ifPresentOrElse(
                hologram -> {
                    int index = args.<Integer>getOptionalUnchecked("index").orElseThrow();
                    if (hologram.removeHologramLine(index)) {
                        sender.sendMessage(GlitchHolograms.get().messageProvider().get(
                                MessageKey.COMMAND_LINE_REMOVE,
                                Placeholder.parsed("id", String.valueOf(id)),
                                Placeholder.parsed("index", String.valueOf(index))
                        ));
                    } else {
                        sender.sendMessage(GlitchHolograms.get().messageProvider().get(
                                MessageKey.COMMAND_ERROR_INVALID_LINE,
                                Placeholder.parsed("id", String.valueOf(id)),
                                Placeholder.parsed("index", String.valueOf(index))
                        ));
                    }
                },
                () -> sender.sendMessage(GlitchHolograms.get().messageProvider().get(
                        MessageKey.COMMAND_ERROR_UNKNOWN_HOLOGRAM,
                        Placeholder.parsed("id", String.valueOf(id))
                ))
        );
    }
}
