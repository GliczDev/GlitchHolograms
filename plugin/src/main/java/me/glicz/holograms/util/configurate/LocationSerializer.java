package me.glicz.holograms.util.configurate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationSerializer implements TypeSerializer<Location> {
    public static final LocationSerializer INSTANCE = new LocationSerializer();

    @Override
    public Location deserialize(Type type, ConfigurationNode node) {
        return new Location(
                Bukkit.getWorld(node.node("world").getString("world")),
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble(),
                node.node("yaw").getFloat(),
                node.node("pitch").getFloat()
        );
    }

    @Override
    public void serialize(Type type, @Nullable Location obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;

        node.node("world").set(obj.getWorld().getName());
        node.node("x").set(obj.getX());
        node.node("y").set(obj.getY());
        node.node("z").set(obj.getZ());
        node.node("yaw").set(obj.getYaw());
        node.node("pitch").set(obj.getPitch());
    }
}
