package me.glicz.holograms.line;

import me.glicz.holograms.Hologram;
import me.glicz.holograms.loader.HologramLoader;
import me.glicz.holograms.util.AdventureUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

public class TextHologramLineImpl extends HologramLineImpl<Component> implements TextHologramLine {
    private Properties properties;

    public TextHologramLineImpl(Hologram hologram, String rawContent, double offset) {
        super(hologram, rawContent, offset);
        this.properties = new PropertiesImpl();
        if (((PropertiesImpl) properties).get(Property.BILLBOARD) == null) {
            this.properties.billboard(Display.Billboard.CENTER);
        }
        updateEntityData();
    }

    @Override
    protected TextDisplay entity() {
        return (TextDisplay) super.entity();
    }

    @Override
    protected Class<? extends Display> entityClass() {
        return TextDisplay.class;
    }

    @Override
    public @NotNull Component content(@NotNull Player player) {
        return AdventureUtil.parseMiniMessage(player, rawContent);
    }

    @Override
    public @NotNull Properties properties() {
        return properties.copy();
    }

    @Override
    public void properties(@NotNull Properties properties) {
        this.properties = properties;
        updateEntityData();
        update();
        HologramLoader.save(hologram);
    }
}
