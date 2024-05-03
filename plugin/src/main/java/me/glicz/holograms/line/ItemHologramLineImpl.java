package me.glicz.holograms.line;

import lombok.Getter;
import me.glicz.holograms.Hologram;
import me.glicz.holograms.loader.HologramLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
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
        this.content = Bukkit.getItemFactory().createItemStack(rawContent);
        updateEntityData();
    }

    @Override
    protected ItemDisplay entity() {
        return (ItemDisplay) super.entity();
    }

    @Override
    protected Class<? extends Display> entityClass() {
        return ItemDisplay.class;
    }

    @Override
    public @NotNull ItemStack content(@NotNull Player player) {
        return content;
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
