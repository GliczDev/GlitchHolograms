package me.glicz.holograms.line;

import lombok.Getter;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.util.AdventureUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public class TextHologramLineImpl extends HologramLineImpl<Component> implements TextHologramLine {
    private final Properties properties;

    public TextHologramLineImpl(Hologram hologram, String rawContent, double offset) {
        super(hologram, rawContent, offset);
        this.properties = new PropertiesImpl();
        this.properties.setBillboard(Display.Billboard.CENTER);
    }

    @Override
    public @NotNull Component getContent(@NotNull Player player) {
        return AdventureUtil.parseMiniMessage(player, rawContent);
    }
}
