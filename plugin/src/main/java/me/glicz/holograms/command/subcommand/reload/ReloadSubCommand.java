package me.glicz.holograms.command.subcommand.reload;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.command.subcommand.SubCommand;
import me.glicz.holograms.loader.HologramLoader;
import me.glicz.holograms.message.MessageKey;
import me.glicz.holograms.task.AsyncFileSaveTask;
import org.bukkit.command.CommandSender;

@SuppressWarnings("UnstableApiUsage")
public class ReloadSubCommand implements SubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("reload")
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }

    public int execute(CommandSender sender) {
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
        return 1;
    }
}
