package me.glicz.holograms.nms;

import me.glicz.holograms.line.HologramLine;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface NMS {
    void sendHologramLine(Player player, int entityId, UUID uniqueId, HologramLine<?> line);

    void sendHologramLineData(Player player, int entityId, HologramLine<?> line);

    void sendHologramLineDestroy(Player player, int entityId);

    void sendHologramLineTeleport(Player player, int entityId, HologramLine<?> line);

    ItemStack deserializeItemStack(String itemStack);
}
