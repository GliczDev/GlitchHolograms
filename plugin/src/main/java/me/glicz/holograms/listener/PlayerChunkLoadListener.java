package me.glicz.holograms.listener;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import me.glicz.holograms.GlitchHologramsAPI;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerChunkLoadListener implements Listener {
    @EventHandler
    public void onPlayerChunkLoad(PlayerChunkLoadEvent e) {
        GlitchHologramsAPI.get().getRegisteredHolograms().forEach(hologram -> {
            if (!hologram.viewers().contains(e.getPlayer())) return;

            Location location = hologram.location();
            if (e.getChunk().getX() == location.getBlockX() >> 4 && e.getChunk().getZ() == location.getBlockZ() >> 4) {
                hologram.show(e.getPlayer());
            }
        });
    }
}
