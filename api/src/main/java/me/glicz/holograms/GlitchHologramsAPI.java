package me.glicz.holograms;

import me.glicz.holograms.nms.NMS;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface GlitchHologramsAPI {
    @UnknownNullability("returns null when GlitchHolograms is not enabled")
    static GlitchHologramsAPI get() {
        RegisteredServiceProvider<GlitchHologramsAPI> provider = Bukkit.getServicesManager().getRegistration(GlitchHologramsAPI.class);
        if (provider == null)
            return null;
        return provider.getProvider();
    }

    @NotNull
    @Unmodifiable
    Set<Hologram> getRegisteredHolograms();

    @ApiStatus.Internal
    NMS getNms();

    default Hologram createHologram(Location location) {
        return createHologram(location, false);
    }

    Hologram createHologram(Location location, boolean save);
}
