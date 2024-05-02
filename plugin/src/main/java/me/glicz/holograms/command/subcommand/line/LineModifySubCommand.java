package me.glicz.holograms.command.subcommand.line;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.command.subcommand.SubCommand;
import me.glicz.holograms.line.HologramLineImpl;
import org.bukkit.entity.Player;

public class LineModifySubCommand implements SubCommand {
    @Override
    public Argument<?> get() {
        Argument<?> argument = new IntegerArgument("index", 0);
        for (HologramLineImpl.Property property : HologramLineImpl.Property.values()) {
            argument.then(new LiteralArgument("type", property.name().toLowerCase())
                    .then(property.getCommandArgument()
                            .executesPlayer(this::execute))
                    .setListed(true)
            );
        }
        return new LiteralArgument("modify").then(argument);
    }

    @Override
    public void execute(Player sender, CommandArguments args) {
        GlitchHologramsAPI.get().getHologram(args.getUnchecked("id")).ifPresentOrElse(
                hologram -> {
                    int index = args.<Integer>getOptionalUnchecked("index").orElseThrow();
                    HologramLineImpl.Property property = HologramLineImpl.Property.valueOf(
                            args.<String>getOptionalUnchecked("type").orElseThrow().toUpperCase()
                    );
                    Object value = args.getOptional("value").orElseThrow();
                    if (hologram.hologramLines().size() <= index) return;
                    hologram.hologramLines().get(index).modifyProperties(properties ->
                            ((HologramLineImpl.PropertiesImpl) properties).set(property, value)
                    );
                },
                () -> {

                }
        );
    }
}
