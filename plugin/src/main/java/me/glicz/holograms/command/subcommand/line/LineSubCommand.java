package me.glicz.holograms.command.subcommand.line;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.command.subcommand.SubCommand;

import static io.papermc.paper.command.brigadier.Commands.argument;

@SuppressWarnings("UnstableApiUsage")
public class LineSubCommand implements SubCommand {
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("line")
                .then(argument("id", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            GlitchHologramsAPI.get().getRegisteredHologramKeys()
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .then(new LineAddSubCommand().get())
                        .then(new LineInsertSubCommand().get())
                        .then(new LineModifySubCommand().get())
                        .then(new LineRemoveSubCommand().get())
                );
    }
}
