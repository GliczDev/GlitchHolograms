package me.glicz.holograms.nms;

import lombok.experimental.UtilityClass;
import me.glicz.holograms.nms.exception.UnsupportedVersionException;
import me.glicz.holograms.nms.v1_20_R1.NMS_v1_20_R1;
import org.bukkit.Bukkit;

@UtilityClass
public class NMSVersionHandler {
    public static NMS getNmsInstance(boolean useLatest) {
        if (useLatest)
            return new NMS_v1_20_R1();
        else {
            String bukkit = Bukkit.getServer().toString();
            String version = bukkit.substring(bukkit.indexOf("minecraftVersion") + 17, bukkit.length() - 1);
            return switch (version) {
                case "1.20", "1.20.1" -> new NMS_v1_20_R1();
                default -> throw new UnsupportedVersionException(version);
            };
        }
    }
}
