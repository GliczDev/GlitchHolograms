package me.glicz.holograms.command.subcommand.line;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.command.subcommand.SubCommand;
import me.glicz.holograms.line.HologramLineImpl;
import me.glicz.holograms.message.MessageKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import static io.papermc.paper.command.brigadier.Commands.argument;

@SuppressWarnings("UnstableApiUsage")
public class LineModifySubCommand implements SubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        ArgumentBuilder<CommandSourceStack, ?> argument = argument("index", IntegerArgumentType.integer(0));
        for (HologramLineImpl.Property property : HologramLineImpl.Property.values()) {
            argument.then(Commands.literal(property.name().toLowerCase())
                    .then(property.getCommandArgument()
                            .executes(ctx -> execute(
                                    ctx.getSource().getSender(),
                                    ctx.getArgument("id", String.class),
                                    ctx.getArgument("index", Integer.class),
                                    property,
                                    ctx.getArgument("value", Object.class)
                            ))
                    )
            );
        }
        return Commands.literal("modify").then(argument);
    }

    public int execute(CommandSender sender, String id, int index, HologramLineImpl.Property property, Object value) throws CommandSyntaxException {
        Hologram hologram = GlitchHolograms.get().getHologram(id).orElseThrow(() ->
                new SimpleCommandExceptionType(MessageComponentSerializer.message()
                        .serialize(GlitchHolograms.get().messageProvider().get(
                                MessageKey.COMMAND_ERROR_UNKNOWN_HOLOGRAM,
                                Placeholder.parsed("id", id)
                        ))
                ).create()
        );

        if (hologram.hologramLines().size() <= index) {
            throw new SimpleCommandExceptionType(MessageComponentSerializer.message()
                    .serialize(GlitchHolograms.get().messageProvider().get(
                            MessageKey.COMMAND_ERROR_INVALID_LINE,
                            Placeholder.parsed("id", String.valueOf(id)),
                            Placeholder.parsed("index", String.valueOf(index))
                    ))
            ).create();
        }

        hologram.hologramLines().get(index).modifyProperties(properties ->
                ((HologramLineImpl.PropertiesImpl) properties).set(property, value)
        );

        HologramLineImpl.PropertiesImpl properties = (HologramLineImpl.PropertiesImpl) hologram.hologramLines().get(index).properties();

        sender.sendMessage(GlitchHolograms.get().messageProvider().get(
                MessageKey.COMMAND_LINE_MODIFY,
                Placeholder.parsed("id", String.valueOf(id)),
                Placeholder.parsed("index", String.valueOf(index)),
                Placeholder.parsed("property", property.name()),
                Placeholder.parsed("value", String.valueOf(properties.getSerialized(property)))
        ));
        return 1;
    }
}
