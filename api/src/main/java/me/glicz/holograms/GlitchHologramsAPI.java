package me.glicz.holograms;

import me.glicz.holograms.line.HologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApiStatus.NonExtendable
public interface GlitchHologramsAPI {
    @UnknownNullability("returns null when GlitchHolograms is not enabled")
    static GlitchHologramsAPI get() {
        RegisteredServiceProvider<GlitchHologramsAPI> provider = Bukkit.getServicesManager().getRegistration(GlitchHologramsAPI.class);
        if (provider == null) {
            return null;
        }
        return provider.getProvider();
    }

    @NotNull
    default Hologram createHologram(@NotNull String id, @NotNull Location location) {
        return createHologram(id, location, false);
    }

    @NotNull
    Hologram createHologram(@NotNull String id, @NotNull Location location, boolean save);

    boolean removeHologram(@NotNull String id);

    @NotNull
    @Unmodifiable
    List<Hologram> getRegisteredHolograms();

    @NotNull
    @Unmodifiable
    Set<String> getRegisteredHologramKeys();

    @NotNull
    Optional<Hologram> getHologram(String id);

    @NotNull
    HologramLine.Properties createLineProperties();
}
