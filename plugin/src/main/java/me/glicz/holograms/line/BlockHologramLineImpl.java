package me.glicz.holograms.line;

import lombok.Getter;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

@Getter
public class BlockHologramLineImpl extends HologramLineImpl<BlockData> implements BlockHologramLine {
    private final Properties properties;

    public BlockHologramLineImpl(Hologram hologram, BlockData content, double offset) {
        super(hologram, content, offset);
        this.properties = new PropertiesImpl();
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
}
