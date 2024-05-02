package me.glicz.holograms.line;

import lombok.Getter;
import me.glicz.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BlockHologramLineImpl extends HologramLineImpl<BlockData> implements BlockHologramLine {
    @Getter
    private final BlockData content;
    private Properties properties;

    public BlockHologramLineImpl(Hologram hologram, String rawContent, double offset) {
        super(hologram, rawContent, offset);
        this.properties = new PropertiesImpl();
        this.content = Bukkit.createBlockData(rawContent);
        updateEntityData();
    }

    @Override
    protected BlockDisplay getEntity() {
        return (BlockDisplay) super.getEntity();
    }

    @Override
    protected Class<? extends Display> entityClass() {
        return BlockDisplay.class;
    }

    @Override
    public @NotNull BlockData content(@NotNull Player player) {
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
    }
}
