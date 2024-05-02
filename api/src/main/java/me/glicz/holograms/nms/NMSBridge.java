package me.glicz.holograms.nms;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface NMSBridge {
    void moveEntity(Entity entity, Location location);

    void sendHologramLine(Player player, Entity entity, Object content);

    void sendHologramLineData(Player player, Entity entity, Object content);

    void sendHologramLineDestroy(Player player, int entityId);

    void sendHologramLineTeleport(Player player, Entity entity);
}
