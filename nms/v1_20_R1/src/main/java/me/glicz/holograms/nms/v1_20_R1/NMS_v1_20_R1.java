package me.glicz.holograms.nms.v1_20_R1;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.buffer.Unpooled;
import io.papermc.paper.adventure.PaperAdventure;
import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.nms.NMS;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class NMS_v1_20_R1 implements NMS {
    protected ServerPlayer getServerPlayer(Player player) {
        return MinecraftServer.getServer().getPlayerList().getPlayer(player.getUniqueId());
    }

    protected void sendPacket(Player player, Packet<?> packet) {
        getServerPlayer(player).connection.connection.send(packet);
    }

    protected EntityType<?> getEntityType(HologramLine.Type type) {
        return switch (type) {
            case BLOCK -> EntityType.BLOCK_DISPLAY;
            case ITEM -> EntityType.ITEM_DISPLAY;
            case TEXT -> EntityType.TEXT_DISPLAY;
        };
    }

    @Override
    public void sendHologramLine(Player player, int entityId, UUID uniqueId, HologramLine<?> line) {
        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(
                entityId,
                uniqueId,
                line.getLocation().getX(),
                line.getLocation().getY(),
                line.getLocation().getZ(),
                line.getLocation().getPitch(),
                line.getLocation().getYaw(),
                getEntityType(line.getType()),
                0, new Vec3(0, 0, 0), 0
        );
        sendPacket(player, packet);
        sendHologramLineData(player, entityId, line);
    }

    @Override
    public void sendHologramLineData(Player player, int entityId, HologramLine<?> line) {
        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(
                entityId,
                List.of(
                        SynchedEntityData.DataValue.create(
                                new EntityDataAccessor<>(14, EntityDataSerializers.BYTE),
                                (byte) line.getProperties().getBillboard().ordinal()
                        ),
                        lineToEntityData(player, line)
                )
        );
        sendPacket(player, packet);
    }

    protected SynchedEntityData.DataValue<?> lineToEntityData(Player player, HologramLine<?> line) {
        return switch (line.getType()) {
            case BLOCK -> blockLineToEntityData(line);
            case ITEM -> itemLineToEntityData(player, line);
            case TEXT -> textLineToEntityData(player, line);
        };
    }

    protected SynchedEntityData.DataValue<?> blockLineToEntityData(HologramLine<?> line) {
        try {
            return SynchedEntityData.DataValue.create(
                    new EntityDataAccessor<>(22, EntityDataSerializers.BLOCK_STATE),
                    BlockStateParser.parseForBlock(
                            BuiltInRegistries.BLOCK.asLookup(),
                            line.getRawContent(),
                            false
                    ).blockState()
            );
        } catch (CommandSyntaxException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    protected SynchedEntityData.DataValue<?> itemLineToEntityData(Player player, HologramLine<?> line) {
        return SynchedEntityData.DataValue.create(
                new EntityDataAccessor<>(22, EntityDataSerializers.ITEM_STACK),
                ItemStack.fromBukkitCopy((org.bukkit.inventory.ItemStack) line.getContent(player))
        );
    }

    protected SynchedEntityData.DataValue<?> textLineToEntityData(Player player, HologramLine<?> line) {
        return SynchedEntityData.DataValue.create(
                new EntityDataAccessor<>(22, EntityDataSerializers.COMPONENT),
                PaperAdventure.asVanilla((Component) line.getContent(player))
        );
    }

    @Override
    public void sendHologramLineDestroy(Player player, int entityId) {
        sendPacket(player, new ClientboundRemoveEntitiesPacket(entityId));
    }

    @Override
    public void sendHologramLineTeleport(Player player, int entityId, HologramLine<?> line) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(entityId);
        buf.writeDouble(line.getLocation().getX());
        buf.writeDouble(line.getLocation().getY());
        buf.writeDouble(line.getLocation().getZ());
        buf.writeByte((byte) ((int) (line.getLocation().getPitch() * 256.0F / 360.0F)));
        buf.writeByte((byte) ((int) (line.getLocation().getYaw() * 256.0F / 360.0F)));
        buf.writeBoolean(false);
        sendPacket(player, new ClientboundTeleportEntityPacket(buf));
    }

    @Override
    public org.bukkit.inventory.ItemStack deserializeItemStack(String itemStack) {
        try {
            ItemParser.ItemResult itemResult = ItemParser.parseForItem(
                    BuiltInRegistries.ITEM.asLookup(),
                    new StringReader(itemStack)
            );
            return new ItemInput(itemResult.item(), itemResult.nbt())
                    .createItemStack(1, true)
                    .asBukkitCopy();
        } catch (CommandSyntaxException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
