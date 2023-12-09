package me.glicz.holograms.command.subcommand.line;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.command.subcommand.SubCommand;
import org.bukkit.entity.Player;

public class LineRemoveSubCommand implements SubCommand {
    @Override
    public Argument<?> get() {
        return new LiteralArgument("action", "remove")
                .then(new IntegerArgument("index", 0)
                        .executesPlayer(this::execute));
    }

    @Override
    public void execute(Player sender, CommandArguments args) {
        Hologram hologram = GlitchHologramsAPI.get().getHologram(args.getUnchecked("id")).orElseThrow();
        int index = args.<Integer>getOptionalUnchecked("index").orElseThrow();
        hologram.removeHologramLine(index);
    }
}
