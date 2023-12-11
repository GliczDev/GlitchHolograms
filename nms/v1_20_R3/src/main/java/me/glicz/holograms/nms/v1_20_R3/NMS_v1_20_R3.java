package me.glicz.holograms.nms.v1_20_R3;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.adventure.PaperAdventure;
import me.glicz.holograms.line.HologramLine;
import me.glicz.holograms.nms.v1_20_R2.NMS_v1_20_R2;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.bukkit.entity.Player;

public class NMS_v1_20_R3 extends NMS_v1_20_R2 {
    @Override
    protected ServerPlayer getServerPlayer(Player player) {
        return MinecraftServer.getServer().getPlayerList().getPlayer(player.getUniqueId());
    }

    protected EntityType<?> getEntityType(HologramLine.Type type) {
        return switch (type) {
            case BLOCK -> EntityType.BLOCK_DISPLAY;
            case ITEM -> EntityType.ITEM_DISPLAY;
            case TEXT -> EntityType.TEXT_DISPLAY;
        };
    }

    @Override
    protected SynchedEntityData.DataValue<?> blockLineToEntityData(HologramLine<?> line) {
        try {
            return SynchedEntityData.DataValue.create(
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

    @Override
    protected SynchedEntityData.DataValue<?> itemLineToEntityData(Player player, HologramLine<?> line) {
        return SynchedEntityData.DataValue.create(
                new EntityDataAccessor<>(23, EntityDataSerializers.ITEM_STACK),
                ItemStack.fromBukkitCopy((org.bukkit.inventory.ItemStack) line.getContent(player))
        );
    }

    @Override
    protected SynchedEntityData.DataValue<?> textLineToEntityData(Player player, HologramLine<?> line) {
        return SynchedEntityData.DataValue.create(
                new EntityDataAccessor<>(23, EntityDataSerializers.COMPONENT),
                PaperAdventure.asVanilla((Component) line.getContent(player))
        );
    }
}
