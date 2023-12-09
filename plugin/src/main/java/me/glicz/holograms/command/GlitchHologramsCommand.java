package me.glicz.holograms.command;

import dev.jorel.commandapi.CommandTree;
import me.glicz.holograms.command.subcommand.create.CreateSubCommand;
import me.glicz.holograms.command.subcommand.line.LineSubCommand;
import me.glicz.holograms.command.subcommand.remove.RemoveSubCommand;

public class GlitchHologramsCommand implements Command {
    @Override
    public void register() {
        new CommandTree("glitchholograms")
                .withAliases("gholograms", "gholo", "gh")
                .withPermission("glitchholograms.command")
                .then(new CreateSubCommand().get())
                .then(new LineSubCommand().get())
                .then(new RemoveSubCommand().get())
                .register();
    }
}
