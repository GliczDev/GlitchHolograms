package me.glicz.holograms;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.glicz.holograms.command.GlitchHologramsCommand;
import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.line.HologramLineImpl;
import me.glicz.holograms.listener.PlayerJoinQuitListener;
import me.glicz.holograms.listener.WorldUnloadListener;
import me.glicz.holograms.loader.HologramLoader;
import me.glicz.holograms.nms.NMSBridge;
import me.glicz.holograms.nms.NMSBridgeImpl;
import me.glicz.holograms.task.AsyncHologramUpdateTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class GlitchHolograms extends JavaPlugin implements GlitchHologramsAPI {
    private final Map<String, Hologram> registeredHolograms = new HashMap<>();
    @Getter
    @Accessors(fluent = true)
    private NMSBridge nmsBridge;

    public static GlitchHolograms get() {
        return (GlitchHolograms) GlitchHologramsAPI.get();
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
                .silentLogs(true)
                .useMojangMappings(true)
                .usePluginNamespace()
        );
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        this.nmsBridge = new NMSBridgeImpl();

        new GlitchHologramsCommand().register();

        Bukkit.getServicesManager().register(GlitchHologramsAPI.class, this, this, ServicePriority.Highest);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldUnloadListener(), this);

        HologramLoader.loadAll();

        new AsyncHologramUpdateTask().runTaskTimerAsynchronously(this, 20, 20);

        getLogger().info("Successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Successfully disabled!");
    }

    @Override
    public @NotNull Hologram createHologram(@NotNull String id, @NotNull Location location, boolean save) {
        if (registeredHolograms.containsKey(id)) {
            throw new IllegalArgumentException("Hologram with id %s already exists!".formatted(id));
        }

        Hologram hologram = new HologramImpl(id, location);
        registeredHolograms.put(id, hologram);
        return hologram;
    }

    @Override
    public boolean removeHologram(@NotNull String id) {
        Hologram hologram = registeredHolograms.remove(id);
        if (hologram == null) {
            return false;
        }

        List.copyOf(hologram.viewers()).forEach(hologram::hide);
        return true;
    }

    @Override
    public @NotNull @Unmodifiable List<Hologram> getRegisteredHolograms() {
        return List.copyOf(registeredHolograms.values());
    }

    @Override
    public @NotNull @Unmodifiable Set<String> getRegisteredHologramKeys() {
        return Set.copyOf(registeredHolograms.keySet());
    }

    @Override
    public @NotNull Optional<Hologram> getHologram(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(registeredHolograms.get(id));
    }

    @Override
    public @NotNull HologramLine.Properties createLineProperties() {
        return new HologramLineImpl.PropertiesImpl();
    }
}
