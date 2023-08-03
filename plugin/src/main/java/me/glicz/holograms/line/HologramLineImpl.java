package me.glicz.holograms.line;

import lombok.Getter;
import lombok.Setter;
import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public abstract class HologramLineImpl<T> implements HologramLine<T> {
    protected final int entityId;
    protected final UUID uniqueId;
    protected final T content;
    protected final Hologram hologram;
    protected final double offset;
    protected Location location;

    @SuppressWarnings("deprecation")
    public HologramLineImpl(Hologram hologram, T content, double offset) {
        this.entityId = Bukkit.getUnsafe().nextEntityId();
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (Bukkit.getEntity(uuid) != null);
        this.uniqueId = uuid;
        this.content = content;
        this.hologram = hologram;
        this.offset = offset;
        updateLocation();
    }

    @Override
    public @NotNull Location getLocation() {
        return location.clone();
    }

    @Override
    public void show(@NotNull Player player) {
        if (!hologram.getViewers().contains(player))
            throw new IllegalArgumentException(player.getName());
        GlitchHologramsAPI.get().getNms().sendHologramLine(player, this);
    }

    @Override
    public void hide(@NotNull Player player) {
        if (hologram.getViewers().contains(player))
            throw new IllegalArgumentException(player.getName());
        GlitchHologramsAPI.get().getNms().sendHologramLineDestroy(player, this);
    }

    @Override
    public void update() {
        hologram.getViewers().forEach(this::update);
    }

    @Override
    public void update(@NotNull Player player) {
        if (!hologram.getViewers().contains(player))
            throw new IllegalArgumentException(player.getName());
        GlitchHologramsAPI.get().getNms().sendHologramLineData(player, this);
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
        this.location = loc;
        hologram.getViewers().forEach(viewer -> GlitchHologramsAPI.get().getNms().sendHologramLineTeleport(viewer, this));
    }

    @Getter
    @Setter
    static class PropertiesImpl implements Properties {
        private Display.Billboard billboard = Display.Billboard.FIXED;
    }
}
