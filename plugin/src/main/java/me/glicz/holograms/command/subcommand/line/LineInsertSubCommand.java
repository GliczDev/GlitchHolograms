package me.glicz.holograms.command.subcommand.line;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.command.argument.MiniMessageArgumentType;
import me.glicz.holograms.command.subcommand.SubCommand;
import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.message.MessageKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import static io.papermc.paper.command.brigadier.Commands.argument;

@SuppressWarnings("UnstableApiUsage")
public class LineInsertSubCommand implements SubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("insert")
                .then(argument("index", IntegerArgumentType.integer(0))
                        .then(Commands.literal("block")
                                .then(argument("content", ArgumentTypes.blockState())
                                        .executes(ctx -> execute(
                                                ctx.getSource().getSender(),
                                                ctx.getArgument("id", String.class),
                                                ctx.getArgument("index", Integer.class),
                                                HologramLine.Type.BLOCK,
                                                ctx.getNodes().get(2).getRange().get(ctx.getInput()),
                                                GlitchHolograms.get().config().defaults().lineOffset()
                                        ))
                                        .then(argument("offset", DoubleArgumentType.doubleArg())
                                                .executes(ctx -> execute(
                                                        ctx.getSource().getSender(),
                                                        ctx.getArgument("id", String.class),
                                                        ctx.getArgument("index", Integer.class),
                                                        HologramLine.Type.BLOCK,
                                                        ctx.getNodes().get(2).getRange().get(ctx.getInput()),
                                                        ctx.getArgument("offset", Double.class)
                                                ))
                                        )
                                )
                        )
                        .then(Commands.literal("item")
                                .then(argument("content", ArgumentTypes.itemStack())
                                        .executes(ctx -> execute(
                                                ctx.getSource().getSender(),
                                                ctx.getArgument("id", String.class),
                                                ctx.getArgument("index", Integer.class),
                                                HologramLine.Type.ITEM,
                                                ctx.getNodes().get(2).getRange().get(ctx.getInput()),
                                                GlitchHolograms.get().config().defaults().lineOffset()
                                        ))
                                        .then(argument("offset", DoubleArgumentType.doubleArg())
                                                .executes(ctx -> execute(
                                                        ctx.getSource().getSender(),
                                                        ctx.getArgument("id", String.class),
                                                        ctx.getArgument("index", Integer.class),
                                                        HologramLine.Type.ITEM,
                                                        ctx.getNodes().get(2).getRange().get(ctx.getInput()),
                                                        ctx.getArgument("offset", Double.class)
                                                ))
                                        )
                                )
                        )
                        .then(Commands.literal("text")
                                .then(argument("content", MiniMessageArgumentType.miniMessage())
                                        .executes(ctx -> execute(
                                                ctx.getSource().getSender(),
                                                ctx.getArgument("id", String.class),
                                                ctx.getArgument("index", Integer.class),
                                                HologramLine.Type.TEXT,
                                                ctx.getNodes().get(2).getRange().get(ctx.getInput()),
                                                GlitchHolograms.get().config().defaults().lineOffset()
                                        ))
                                        .then(argument("offset", DoubleArgumentType.doubleArg())
                                                .executes(ctx -> execute(
                                                        ctx.getSource().getSender(),
                                                        ctx.getArgument("id", String.class),
                                                        ctx.getArgument("index", Integer.class),
                                                        HologramLine.Type.TEXT,
                                                        ctx.getNodes().get(2).getRange().get(ctx.getInput()),
                                                        ctx.getArgument("offset", Double.class)
                                                ))
                                        )
                                )
                        )
                );
    }

    public int execute(CommandSender sender, String id, int index, HologramLine.Type type, String content, double offset) throws CommandSyntaxException {
        Hologram hologram = GlitchHolograms.get().getHologram(id).orElseThrow(() ->
                new SimpleCommandExceptionType(MessageComponentSerializer.message()
                        .serialize(GlitchHolograms.get().messageProvider().get(
                                MessageKey.COMMAND_ERROR_UNKNOWN_HOLOGRAM,
                                Placeholder.parsed("id", id)
                        ))
                ).create()
        );

        hologram.insertHologramLine(index, type, content, offset);
        sender.sendMessage(GlitchHolograms.get().messageProvider().get(
                MessageKey.COMMAND_LINE_INSERT,
                Placeholder.parsed("id", String.valueOf(id)),
                Placeholder.parsed("index", String.valueOf(index))
        ));
        return 1;
    }
}
