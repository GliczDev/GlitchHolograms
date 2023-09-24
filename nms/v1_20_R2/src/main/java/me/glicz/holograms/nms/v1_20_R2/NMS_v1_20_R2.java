package me.glicz.holograms.nms.v1_20_R2;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.buffer.Unpooled;
import io.papermc.paper.adventure.PaperAdventure;
import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.nms.v1_20_R1.NMS_v1_20_R1;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.item.ItemStack;
import org.bukkit.entity.Player;

import java.util.List;

public class NMS_v1_20_R2 extends NMS_v1_20_R1 {
    @Override
    protected void sendPacket(Player player, Packet<?> packet) {
        getNmsPlayer(player).connection.connection.send(packet);
    }

    @Override
    public void sendHologramLineData(Player player, int entityId, HologramLine<?> line) {
        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(
                entityId,
                List.of(
                        SynchedEntityData.DataValue.create(
                                new EntityDataAccessor<>(15, EntityDataSerializers.BYTE),
                                (byte) line.getProperties().getBillboard().ordinal()
                        ),
                        switch (line.getType()) {
                            case BLOCK -> {
                                try {
                                    yield SynchedEntityData.DataValue.create(
                                            new EntityDataAccessor<>(23, EntityDataSerializers.BLOCK_STATE),
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
                            case ITEM -> SynchedEntityData.DataValue.create(
                                    new EntityDataAccessor<>(23, EntityDataSerializers.ITEM_STACK),
                                    ItemStack.fromBukkitCopy((org.bukkit.inventory.ItemStack) line.getContent(player))
                            );
                            case TEXT -> SynchedEntityData.DataValue.create(
                                    new EntityDataAccessor<>(23, EntityDataSerializers.COMPONENT),
                                    PaperAdventure.asVanilla((Component) line.getContent(player))
                            );
                        }
                )
        );
        sendPacket(player, packet);
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
}
