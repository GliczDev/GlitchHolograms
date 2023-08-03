package me.glicz.holograms.line;

import lombok.Getter;
import me.glicz.holograms.Hologram;
import org.bukkit.inventory.ItemStack;

@Getter
public class ItemHologramLineImpl extends HologramLineImpl<ItemStack> implements ItemHologramLine {
    private final Properties properties;

    public ItemHologramLineImpl(Hologram hologram, ItemStack content, double offset) {
        super(hologram, content, offset);
        this.properties = new PropertiesImpl();
    }
}
