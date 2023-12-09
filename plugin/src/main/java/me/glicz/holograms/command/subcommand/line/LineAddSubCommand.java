package me.glicz.holograms.command.subcommand.line;

import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.command.argument.MiniMessageArgument;
import me.glicz.holograms.command.subcommand.SubCommand;
import me.glicz.holograms.line.HologramLine;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.entity.Player;

class LineAddSubCommand implements SubCommand {
    @Override
    public Argument<?> get() {
        return new LiteralArgument("action", "add")
                .then(new LiteralArgument("type", "block").then(
                        new BlockStateArgument("content")
                                .executesPlayer(this::execute)
                                .then(new DoubleArgument("offset")
                                        .executesPlayer(this::execute))
                ).setListed(true))
                .then(new LiteralArgument("type", "item").then(
                        new ItemStackArgument("content")
                                .executesPlayer(this::execute)
                                .then(new DoubleArgument("offset")
                                        .executesPlayer(this::execute))
                ).setListed(true))
                .then(new LiteralArgument("type", "text").then(
                        MiniMessageArgument.miniMessageArgument("content")
                                .executesPlayer(this::execute)
                                .then(new DoubleArgument("offset")
                                        .executesPlayer(this::execute))
                ).setListed(true));
    }

    @Override
    public void execute(Player sender, CommandArguments args) {
        GlitchHologramsAPI.get().getHologram(args.getUnchecked("id")).ifPresentOrElse(
                hologram -> {
                    HologramLine.Type type = EnumUtils.getEnumIgnoreCase(HologramLine.Type.class, args.getUnchecked("type"));
                    String content = LineSubCommand.getLineContent(type, args);
                    double offset = args.getOrDefaultUnchecked("offset", 0.35);
                    hologram.addHologramLine(type, content, offset);
                },
                () -> {
                    // doesn't exist :(
                }
        );
    }
}
