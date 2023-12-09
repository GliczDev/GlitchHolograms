package me.glicz.holograms.command.subcommand.line;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.line.HologramLine;

public class LineSubCommand {
    protected static String getLineContent(HologramLine.Type type, CommandArguments args) {
        return type == HologramLine.Type.TEXT
                ? args.<String>getOptionalUnchecked("content").orElseThrow()
                : args.getRawOptional("content").orElseThrow();
    }

    public Argument<String> get() {
        return new LiteralArgument("line")
                .then(new StringArgument("id")
                        .replaceSuggestions(ArgumentSuggestions.strings((info) ->
                                GlitchHologramsAPI.get().getRegisteredHologramsKeys().toArray(String[]::new)
                        ))
                        .then(new LineAddSubCommand().get())
                        .then(new LineInsertSubCommand().get())
                        .then(new LineModifySubCommand().get())
                        .then(new LineRemoveSubCommand().get())
                );
    }
}
