package me.glicz.holograms.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

@SuppressWarnings("UnstableApiUsage")
public interface SubCommand {
    LiteralArgumentBuilder<CommandSourceStack> get();
}
