package me.glicz.holograms.command.subcommand.delete;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.command.subcommand.SubCommand;
import me.glicz.holograms.message.MessageKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import static io.papermc.paper.command.brigadier.Commands.argument;

@SuppressWarnings("UnstableApiUsage")
public class DeleteSubCommand implements SubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("delete")
                .then(argument("id", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            GlitchHologramsAPI.get().getRegisteredHologramKeys()
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> execute(
                                ctx.getSource().getSender(),
                                ctx.getArgument("id", String.class)
                        ))
                );
    }

    public int execute(CommandSender sender, String id) throws CommandSyntaxException {
        if (GlitchHologramsAPI.get().removeHologram(id)) {
            sender.sendMessage(GlitchHolograms.get().messageProvider().get(
                    MessageKey.COMMAND_DELETE,
                    Placeholder.parsed("id", id)
            ));
            return 1;
        } else {
            throw new SimpleCommandExceptionType(MessageComponentSerializer.message()
                    .serialize(GlitchHolograms.get().messageProvider().get(
                            MessageKey.COMMAND_ERROR_UNKNOWN_HOLOGRAM,
                            Placeholder.parsed("id", id)
                    ))
            ).create();
        }
    }
}
