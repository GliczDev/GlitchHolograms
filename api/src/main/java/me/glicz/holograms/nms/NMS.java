package me.glicz.holograms.nms;

import me.glicz.holograms.line.HologramLine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface NMS {
    void sendHologramLine(Player player, HologramLine<?> line);

    void sendHologramLineData(Player player, HologramLine<?> line);

    void sendHologramLineDestroy(Player player, HologramLine<?> line);

    void sendHologramLineTeleport(Player player, HologramLine<?> line);
}
