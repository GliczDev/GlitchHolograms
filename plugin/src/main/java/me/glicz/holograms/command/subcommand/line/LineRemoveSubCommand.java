package me.glicz.holograms.command.subcommand.line;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.command.subcommand.SubCommand;
import me.glicz.holograms.message.MessageKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import static io.papermc.paper.command.brigadier.Commands.argument;

@SuppressWarnings("UnstableApiUsage")
public class LineRemoveSubCommand implements SubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("remove")
                .then(argument("index", IntegerArgumentType.integer(0))
                        .executes(ctx -> execute(
                                ctx.getSource().getSender(),
                                ctx.getArgument("id", String.class),
                                ctx.getArgument("index", Integer.class)
                        ))
                );
    }

    public int execute(CommandSender sender, String id, int index) throws CommandSyntaxException {
        Hologram hologram = GlitchHolograms.get().getHologram(id).orElseThrow(() ->
                new SimpleCommandExceptionType(MessageComponentSerializer.message()
                        .serialize(GlitchHolograms.get().messageProvider().get(
                                MessageKey.COMMAND_ERROR_UNKNOWN_HOLOGRAM,
                                Placeholder.parsed("id", id)
                        ))
                ).create()
        );

        if (hologram.removeHologramLine(index)) {
            sender.sendMessage(GlitchHolograms.get().messageProvider().get(
                    MessageKey.COMMAND_LINE_REMOVE,
                    Placeholder.parsed("id", String.valueOf(id)),
                    Placeholder.parsed("index", String.valueOf(index))
            ));
            return 1;
        } else {
            throw new SimpleCommandExceptionType(MessageComponentSerializer.message()
                    .serialize(GlitchHolograms.get().messageProvider().get(
                            MessageKey.COMMAND_ERROR_INVALID_LINE,
                            Placeholder.parsed("id", String.valueOf(id)),
                            Placeholder.parsed("index", String.valueOf(index))
                    ))
            ).create();
        }
    }
}
