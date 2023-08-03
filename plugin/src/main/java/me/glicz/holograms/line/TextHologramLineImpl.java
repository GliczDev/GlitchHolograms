package me.glicz.holograms.line;

import lombok.Getter;
import me.glicz.holograms.Hologram;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Display;

@Getter
public class TextHologramLineImpl extends HologramLineImpl<Component> implements TextHologramLine {
    private final Properties properties;

    public TextHologramLineImpl(Hologram hologram, Component content, double offset) {
        super(hologram, content, offset);
        this.properties = new PropertiesImpl();
        this.properties.setBillboard(Display.Billboard.CENTER);
    }
}
