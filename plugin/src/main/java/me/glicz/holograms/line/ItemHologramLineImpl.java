package me.glicz.holograms.line;

import lombok.Getter;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public class ItemHologramLineImpl extends HologramLineImpl<ItemStack> implements ItemHologramLine {
    private final Properties properties;
    private final ItemStack content;

    public ItemHologramLineImpl(Hologram hologram, String rawContent, double offset) {
        super(hologram, rawContent, offset);
        this.properties = new PropertiesImpl();
        this.content = GlitchHologramsAPI.get().getNms().deserializeItemStack(rawContent);
    }

    @Override
    public @NotNull ItemStack getContent(@NotNull Player player) {
        return content;
    }
}
