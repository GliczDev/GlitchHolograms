package me.glicz.holograms;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.glicz.holograms.command.GlitchHologramsCommand;
import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.line.HologramLineImpl;
import me.glicz.holograms.listener.PlayerJoinQuitListener;
import me.glicz.holograms.loader.HologramLoader;
import me.glicz.holograms.nms.NMS;
import me.glicz.holograms.nms.NMSVersionHandler;
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
    private NMS nms;

    public static NMS getNms() {
        return ((GlitchHolograms) GlitchHologramsAPI.get()).nms;
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).silentLogs(true));
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        this.nms = NMSVersionHandler.getNmsInstance(false);

        new GlitchHologramsCommand().register();

        Bukkit.getServicesManager().register(GlitchHologramsAPI.class, this, this, ServicePriority.Highest);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);

        HologramLoader.loadAll(this);

        new AsyncHologramUpdateTask().runTaskTimerAsynchronously(this, 20, 20);

        getLogger().info("Successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Successfully disabled!");
    }

    @Override
    public @NotNull Hologram createHologram(@NotNull String id, @NotNull Location location, boolean save) {
        if (registeredHolograms.containsKey(id))
            throw new IllegalArgumentException("Hologram with id %s already exists!".formatted(id));
        Hologram hologram = new HologramImpl(id, location);
        registeredHolograms.put(id, hologram);
        return hologram;
    }

    @Override
    public boolean removeHologram(@NotNull String id) {
        Hologram hologram = registeredHolograms.remove(id);
        if (hologram == null)
            return false;
        List.copyOf(hologram.getViewers()).forEach(hologram::hide);
        return true;
    }

    @Override
    public @NotNull Collection<Hologram> getRegisteredHolograms() {
        return Collections.unmodifiableCollection(registeredHolograms.values());
    }

    @Override
    public @NotNull @Unmodifiable Set<String> getRegisteredHologramsKeys() {
        return Collections.unmodifiableSet(registeredHolograms.keySet());
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
