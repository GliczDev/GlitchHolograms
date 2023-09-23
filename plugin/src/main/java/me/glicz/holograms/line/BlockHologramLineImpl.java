package me.glicz.holograms.line;

import lombok.Getter;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
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
    }

    @Override
    public @NotNull BlockData getContent(@NotNull Player player) {
        return content;
    }

    @Override
    public void updateLocation() {
        double y = hologram.getLocation().getY();
        int index = hologram.getHologramLines().contains(this)
                ? hologram.getHologramLines().indexOf(this)
                : hologram.getHologramLines().size();
        if (index > 0)
            y = hologram.getHologramLines().get(index - 1).getLocation().getY() + offset;
        Location loc = hologram.getLocation();
        loc.setY(y);
        this.location = loc.add(-0.5, 0, -0.5);
        hologram.getViewers().forEach(viewer -> GlitchHologramsAPI.get().getNms().sendHologramLineTeleport(viewer, this));
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
