package me.glicz.holograms.task;

import me.glicz.holograms.GlitchHologramsAPI;
import me.glicz.holograms.Hologram;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncHologramUpdateTask extends BukkitRunnable {
    @Override
    public void run() {
        GlitchHologramsAPI.get().getRegisteredHolograms().forEach(Hologram::update);
    }
}
