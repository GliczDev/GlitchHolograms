package me.glicz.holograms.command.subcommand.delete;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.command.subcommand.SubCommand;
import me.glicz.holograms.message.MessageKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class DeleteSubCommand implements SubCommand {
    @Override
    public Argument<?> get() {
        return new LiteralArgument("delete")
                .then(new StringArgument("id")
                        .replaceSuggestions(ArgumentSuggestions.strings((info) ->
                                GlitchHologramsAPI.get().getRegisteredHologramKeys().toArray(String[]::new)
                        ))
                        .executesPlayer(this::execute)
                );
    }

    @Override
    public void execute(Player sender, CommandArguments args) {
        String id = args.<String>getOptionalUnchecked("id").orElseThrow();
        if (GlitchHologramsAPI.get().removeHologram(id)) {
            sender.sendMessage(GlitchHolograms.get().messageProvider().get(
                    MessageKey.COMMAND_DELETE,
                    Placeholder.parsed("id", id)
            ));
        } else {
            sender.sendMessage(GlitchHolograms.get().messageProvider().get(
                    MessageKey.COMMAND_ERROR_UNKNOWN_HOLOGRAM,
                    Placeholder.parsed("id", id)
            ));
        }
    }
}
