package me.glicz.holograms.line;

import lombok.Getter;
import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemHologramLineImpl extends HologramLineImpl<ItemStack> implements ItemHologramLine {
    @Getter
    private final ItemStack content;
    private Properties properties;

    public ItemHologramLineImpl(Hologram hologram, String rawContent, double offset) {
        super(hologram, rawContent, offset);
        this.properties = new PropertiesImpl();
        this.content = GlitchHolograms.getNms().deserializeItemStack(rawContent);
    }

    @Override
    public @NotNull ItemStack getContent(@NotNull Player player) {
        return content;
    }

    @Override
    public @NotNull Properties getProperties() {
        return properties.copy();
    }

    @Override
    public void setProperties(@NotNull Properties properties) {
        this.properties = properties;
        update();
    }
}
