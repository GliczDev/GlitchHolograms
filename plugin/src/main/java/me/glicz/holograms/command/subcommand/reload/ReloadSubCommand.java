package me.glicz.holograms.command.subcommand.reload;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.command.subcommand.SubCommand;
import me.glicz.holograms.loader.HologramLoader;
import me.glicz.holograms.message.MessageKey;
import me.glicz.holograms.task.AsyncFileSaveTask;
import org.bukkit.entity.Player;

public class ReloadSubCommand implements SubCommand {
    @Override
    public Argument<?> get() {
        return new LiteralArgument("reload")
                .executesPlayer(this::execute);
    }

    @Override
    public void execute(Player sender, CommandArguments args) {
        GlitchHolograms.get().reloadConfig();
        GlitchHolograms.get().reloadMessageProvider();

        AsyncFileSaveTask.saveAll();
        GlitchHolograms.get().getRegisteredHolograms().forEach(hologram -> {
            if (hologram.shouldSave()) {
                GlitchHolograms.get().removeHologram(hologram.id());
            }
        });
        HologramLoader.loadAll();

        sender.sendMessage(GlitchHolograms.get().messageProvider().get(MessageKey.COMMAND_RELOAD));
    }
}
