package me.glicz.holograms.nms;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NMSBridgeImpl implements NMSBridge {
    private ServerPlayer serverPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    @Override
    public void moveEntity(Entity entity, Location location) {
        ((CraftEntity) entity).getHandle().moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public void sendHologramLine(Player player, Entity entity, Object content) {
        net.minecraft.world.entity.Entity handle = ((CraftEntity) entity).getHandle();
        serverPlayer(player).connection.send(new ClientboundAddEntityPacket(
                handle.getId(),
                handle.getUUID(),
                handle.trackingPosition().x(),
                handle.trackingPosition().y(),
                handle.trackingPosition().z(),
                handle.getXRot(),
                handle.getYRot(),
                handle.getType(),
                0,
                handle.getDeltaMovement(),
                handle.getYHeadRot()
        ));
        sendHologramLineData(player, entity, content);
    }

    @Override
    public void sendHologramLineData(Player player, Entity entity, Object content) {
        List<SynchedEntityData.DataValue<?>> dataValues = ((CraftEntity) entity).getHandle().getEntityData().packAll();
        if (dataValues == null) return;

        dataValues.add(switch (content) {
            case BlockData blockData -> SynchedEntityData.DataValue.create(
                    new EntityDataAccessor<>(23, EntityDataSerializers.BLOCK_STATE),
                    ((CraftBlockData) blockData).getState()
            );
            case ItemStack itemStack -> SynchedEntityData.DataValue.create(
                    new EntityDataAccessor<>(23, EntityDataSerializers.ITEM_STACK),
                    net.minecraft.world.item.ItemStack.fromBukkitCopy(itemStack)
            );
            case Component component -> SynchedEntityData.DataValue.create(
                    new EntityDataAccessor<>(23, EntityDataSerializers.COMPONENT),
                    PaperAdventure.asVanilla(component)
            );
            default -> throw new IllegalStateException("Unexpected value: " + content);
        });

        serverPlayer(player).connection.send(new ClientboundSetEntityDataPacket(entity.getEntityId(), dataValues));
    }

    @Override
    public void sendHologramLineDestroy(Player player, int entityId) {
        serverPlayer(player).connection.send(new ClientboundRemoveEntitiesPacket(entityId));
    }

    @Override
    public void sendHologramLineTeleport(Player player, Entity entity) {
        serverPlayer(player).connection.send(new ClientboundTeleportEntityPacket(((CraftEntity) entity).getHandle()));
    }
}
