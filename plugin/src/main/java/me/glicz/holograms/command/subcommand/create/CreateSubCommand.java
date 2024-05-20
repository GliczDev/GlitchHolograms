package me.glicz.holograms.command.subcommand.create;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.command.subcommand.SubCommand;
import me.glicz.holograms.message.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.papermc.paper.command.brigadier.Commands.argument;

@SuppressWarnings("UnstableApiUsage")
public class CreateSubCommand implements SubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("create")
                .then(argument("id", StringArgumentType.word())
                        .executes(ctx -> execute(
                                ctx.getSource().getSender(),
                                ctx.getArgument("id", String.class),
                                ctx.getSource().getLocation().toBlock(),
                                true
                        ))
                        .then(argument("position", ArgumentTypes.blockPosition())
                                .executes(ctx -> execute(
                                        ctx.getSource().getSender(),
                                        ctx.getArgument("id", String.class),
                                        ctx.getArgument("position", BlockPositionResolver.class)
                                                .resolve(ctx.getSource()),
                                        true
                                ))
                                .then(argument("save", BoolArgumentType.bool())
                                        .executes(ctx -> execute(
                                                ctx.getSource().getSender(),
                                                ctx.getArgument("id", String.class),
                                                ctx.getArgument("position", BlockPositionResolver.class)
                                                        .resolve(ctx.getSource()),
                                                ctx.getArgument("save", Boolean.class)
                                        ))
                                )
                        )
                );
    }

    public int execute(CommandSender sender, String id, BlockPosition position, boolean save) throws CommandSyntaxException {
        if (!(sender instanceof Player player)) {
            throw new SimpleCommandExceptionType(MessageComponentSerializer.message()
                    .serialize(Component.translatable("permissions.requires.player"))
            ).create();
        }

        GlitchHolograms.get().createHologram(id, position.toLocation(player.getWorld()), save);
        player.sendMessage(GlitchHolograms.get().messageProvider().get(
                MessageKey.COMMAND_CREATE,
                Placeholder.parsed("id", id)
        ));
        return 1;
    }
}
