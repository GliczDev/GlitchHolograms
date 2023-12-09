package me.glicz.holograms.command.subcommand.remove;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.command.subcommand.SubCommand;
import org.bukkit.entity.Player;

public class RemoveSubCommand implements SubCommand {
    @Override
    public Argument<?> get() {
        return new LiteralArgument("remove")
                .then(new StringArgument("id")
                        .replaceSuggestions(ArgumentSuggestions.strings((info) ->
                                GlitchHologramsAPI.get().getRegisteredHologramsKeys().toArray(String[]::new)
                        ))
                        .executesPlayer(this::execute)
                );
    }

    @Override
    public void execute(Player sender, CommandArguments args) {
        String id = args.<String>getOptionalUnchecked("id").orElseThrow();
        GlitchHologramsAPI.get().removeHologram(id);
    }
}
