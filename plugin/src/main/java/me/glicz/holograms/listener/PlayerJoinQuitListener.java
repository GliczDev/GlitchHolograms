package me.glicz.holograms.listener;

import me.glicz.holograms.GlitchHologramsAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        GlitchHologramsAPI.get().getRegisteredHolograms()
                .forEach(hologram -> hologram.show(e.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        GlitchHologramsAPI.get().getRegisteredHolograms()
                .forEach(hologram -> hologram.hide(e.getPlayer()));
    }
}
