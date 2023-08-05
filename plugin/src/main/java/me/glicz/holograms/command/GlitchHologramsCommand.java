package me.glicz.holograms.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.line.HologramLine;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class GlitchHologramsCommand implements Command {
    private final Function<Argument<?>, Argument<?>> lineCommandDefaultArgs = arg -> arg
            .then(new LiteralArgument("type", "block").then(
                    new BlockStateArgument("content")
                            .executesPlayer(this::executeLineCommand)
                            .then(new DoubleArgument("offset")
                                    .executesPlayer(this::executeLineCommand)
                            )
            ).setListed(true))
            .then(new LiteralArgument("type", "item").then(
                    new ItemStackArgument("content")
                            .executesPlayer(this::executeLineCommand)
                            .then(new DoubleArgument("offset")
                                    .executesPlayer(this::executeLineCommand)
                            )
            ).setListed(true))
            .then(new LiteralArgument("type", "text").then(
                    new AdventureChatComponentArgument("content")
                            .executesPlayer(this::executeLineCommand)
                            .then(new DoubleArgument("offset")
                                    .executesPlayer(this::executeLineCommand)
                            )
            ).setListed(true));

    @Override
    public void register() {
        new CommandAPICommand("glitchholograms")
                .withAliases("gholograms", "gholo", "gh")
                .withPermission("glitchholograms.command")
                .withSubcommands(getCreateCommand(), getRemoveCommand())
                .register();
        new CommandTree("glitchholograms")
                .withAliases("gholograms", "gholo", "gh")
                .then(new LiteralArgument("line").then(
                        new StringArgument("id")
                                .replaceSuggestions(ArgumentSuggestions.strings((info) ->
                                        GlitchHologramsAPI.get().getRegisteredHologramsKeys().toArray(String[]::new)
                                ))
                                .then(lineCommandDefaultArgs.apply(
                                        new LiteralArgument("action", "add")
                                ).setListed(true))
                                .then(new LiteralArgument("action", "insert").then(
                                        lineCommandDefaultArgs.apply(new IntegerArgument("index", 0))
                                ).setListed(true))
                                .then(new LiteralArgument("action", "remove").then(
                                        new IntegerArgument("index", 0)
                                                .executesPlayer(this::executeLineCommand)
                                ).setListed(true))
                ))
                .register();
    }

    public CommandAPICommand getCreateCommand() {
        return new CommandAPICommand("create")
                .withArguments(new StringArgument("id"))
                .withOptionalArguments(new LocationArgument("location"))
                .withOptionalArguments(new BooleanArgument("save"))
                .executesPlayer((sender, args) -> {
                    args.<String>getOptionalUnchecked("id").ifPresent(id -> {
                        Location location = args.getOrDefaultUnchecked("location", sender.getLocation());
                        boolean save = args.getOrDefaultUnchecked("save", true);
                        GlitchHologramsAPI.get().createHologram(id, location, save);
                    });
                });
    }

    public CommandAPICommand getRemoveCommand() {
        return new CommandAPICommand("remove")
                .withArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings((info) ->
                        GlitchHologramsAPI.get().getRegisteredHologramsKeys().toArray(String[]::new)
                )))
                .executesPlayer((sender, args) -> {
                    args.<String>getOptionalUnchecked("id").ifPresent(id -> {
                        GlitchHologramsAPI.get().removeHologram(id);
                    });
                });
    }

    public void executeLineCommand(Player sender, CommandArguments args) {
        String action = args.getUnchecked("action");
        Hologram hologram = GlitchHologramsAPI.get().getHologram(args.getUnchecked("id")).orElseThrow();
        if ("add".equals(action) || "insert".equals(action)) {
            HologramLine.Type type = EnumUtils.getEnumIgnoreCase(HologramLine.Type.class, args.getUnchecked("type"));
            Object content = args.getOptional("content").orElseThrow();
            double offset = args.getOrDefaultUnchecked("offset", 0.4);
            switch (action) {
                case "add" -> hologram.addHologramLine(type, content, offset);
                case "insert" -> {
                    int index = args.getOrDefaultUnchecked("index", Integer.MAX_VALUE);
                    hologram.insertHologramLine(index, type, content, offset);
                }
            }
        }
        if ("remove".equals(action)) {
            int index = args.getOrDefaultUnchecked("index", Integer.MAX_VALUE);
            hologram.removeHologramLine(index);
        }
    }
}
