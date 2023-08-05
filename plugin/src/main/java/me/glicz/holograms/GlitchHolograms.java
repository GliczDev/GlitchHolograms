package me.glicz.holograms;

import me.glicz.holograms.listener.PlayerJoinQuitListener;
import me.glicz.holograms.nms.NMS;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GlitchHolograms extends JavaPlugin implements GlitchHologramsAPI {
    private final Map<String, Hologram> registeredHolograms = new HashMap<>();

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
    public NMS getNms() {
        return new me.glicz.holograms.nms.v1_20_R1.NMS_v1_20_R1();
    }

    @Override
    public Hologram createHologram(@NotNull String id, @NotNull Location location, boolean save) {
        Hologram hologram = new HologramImpl(id, location);
        registeredHolograms.put(id, hologram);
        return hologram;
    }

    @Override
    public @NotNull Collection<Hologram> getRegisteredHolograms() {
        return Collections.unmodifiableCollection(registeredHolograms.values());
    }

    @Override
    public @NotNull Optional<Hologram> getHologram(String id) {
        return Optional.ofNullable(registeredHolograms.get(id));
    }
}
