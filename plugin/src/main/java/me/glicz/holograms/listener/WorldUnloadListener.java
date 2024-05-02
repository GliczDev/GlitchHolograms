package me.glicz.holograms.listener;

import me.glicz.holograms.GlitchHolograms;
import me.glicz.holograms.GlitchHologramsAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldUnloadListener implements Listener {
    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        GlitchHologramsAPI.get().getRegisteredHolograms().forEach(hologram -> {
            if (hologram.location().getWorld() == e.getWorld()) {
                GlitchHologramsAPI.get().removeHologram(hologram.id());
                GlitchHolograms.get().getSLF4JLogger().error("Hologram {} was unloaded with it's world!", hologram.id());
            }
        });
    }
}
