package me.glicz.holograms.command;

import io.papermc.paper.command.brigadier.Commands;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.command.subcommand.create.CreateSubCommand;
import me.glicz.holograms.command.subcommand.delete.DeleteSubCommand;
import me.glicz.holograms.command.subcommand.line.LineSubCommand;
import me.glicz.holograms.command.subcommand.reload.ReloadSubCommand;

import java.util.List;

public class GlitchHologramsCommand implements Command {
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void register(Commands registrar) {
        registrar.register(
                GlitchHolograms.get().getPluginMeta(),
                Commands.literal("glitchholograms")
                        .requires(ctx -> ctx.getSender().hasPermission("glitchholograms.command"))
                        .then(new CreateSubCommand().get())
                        .then(new DeleteSubCommand().get())
                        .then(new LineSubCommand().get())
                        .then(new ReloadSubCommand().get())
                        .build(),
                null,
                List.of("gholograms", "gholo", "gh")
        );
    }
}
