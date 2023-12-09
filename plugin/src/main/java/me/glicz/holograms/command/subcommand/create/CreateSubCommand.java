package me.glicz.holograms.command.subcommand.create;

import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.command.subcommand.SubCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CreateSubCommand implements SubCommand {
    @Override
    public Argument<?> get() {
        return new LiteralArgument("create")
                .then(new StringArgument("id")
                        .executesPlayer(this::execute)
                        .then(new LocationArgument("location")
                                .executesPlayer(this::execute)
                                .then(new BooleanArgument("save")
                                        .executesPlayer(this::execute)))
                );
    }

    @Override
    public void execute(Player sender, CommandArguments args) {
        String id = args.<String>getOptionalUnchecked("id").orElseThrow();
        Location location = args.getOrDefaultUnchecked("location", sender.getLocation());
        boolean save = args.getOrDefaultUnchecked("save", true);
        GlitchHologramsAPI.get().createHologram(id, location, save);
    }
}
