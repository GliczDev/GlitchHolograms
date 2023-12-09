package me.glicz.holograms.command.subcommand;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

public interface SubCommand {
    Argument<?> get();

    void execute(Player sender, CommandArguments args);
}
