package me.glicz.holograms.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AsyncFileSaveTask extends BukkitRunnable {
    private static final Map<File, CommentedConfigurationNode> SAVE_QUEUE = new HashMap<>();

    public static void save(File file, CommentedConfigurationNode conf) {
        SAVE_QUEUE.put(file, conf);
    }

    public static void saveAll() {
        Map.copyOf(SAVE_QUEUE).forEach((file, conf) -> {
            try {
                YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                        .indent(2)
                        .nodeStyle(NodeStyle.BLOCK)
                        .file(file)
                        .build();
                loader.save(conf);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SAVE_QUEUE.remove(file);
        });
    }

    @Override
    public void run() {
        saveAll();
    }
}
