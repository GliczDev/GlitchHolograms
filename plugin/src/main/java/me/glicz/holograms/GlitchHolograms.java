package me.glicz.holograms;

import me.glicz.holograms.listener.PlayerJoinQuitListener;
import me.glicz.holograms.nms.NMS;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GlitchHolograms extends JavaPlugin implements GlitchHologramsAPI {
    private final Set<Hologram> registeredHolograms = new HashSet<>();

    @Override
    public void onEnable() {
        Bukkit.getServicesManager().register(GlitchHologramsAPI.class, this, this, ServicePriority.Highest);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);
        getLogger().info("Successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Successfully disabled!");
    }

    @Override
    public @NotNull Set<Hologram> getRegisteredHolograms() {
        return Collections.unmodifiableSet(registeredHolograms);
    }

    @Override
    public NMS getNms() {
        return new me.glicz.holograms.nms.v1_20_R1.NMS_v1_20_R1();
    }

    @Override
    public Hologram createHologram(Location location, boolean save) {
        Hologram hologram = new HologramImpl(location);
        registeredHolograms.add(hologram);
        return hologram;
    }
}
