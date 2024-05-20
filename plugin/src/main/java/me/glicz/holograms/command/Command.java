package me.glicz.holograms.command;

import io.papermc.paper.command.brigadier.Commands;

@SuppressWarnings("UnstableApiUsage")
public interface Command {
    void register(Commands registrar);
}
