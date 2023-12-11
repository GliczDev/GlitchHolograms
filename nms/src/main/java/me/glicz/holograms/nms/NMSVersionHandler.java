package me.glicz.holograms.nms;

import lombok.experimental.UtilityClass;
import me.glicz.holograms.nms.exception.UnsupportedVersionException;
import me.glicz.holograms.nms.v1_20_R1.NMS_v1_20_R1;
import me.glicz.holograms.nms.v1_20_R2.NMS_v1_20_R2;
import me.glicz.holograms.nms.v1_20_R3.NMS_v1_20_R3;
import org.bukkit.Bukkit;

@UtilityClass
public class NMSVersionHandler {
    public static NMS getNmsInstance(boolean useLatest) {
        if (useLatest)
            return new NMS_v1_20_R3();
        else {
            String version = Bukkit.getMinecraftVersion();
            return switch (version) {
                case "1.20.3", "1.20.4" -> new NMS_v1_20_R3();
                case "1.20.2" -> new NMS_v1_20_R2();
                case "1.20", "1.20.1" -> new NMS_v1_20_R1();
                default -> throw new UnsupportedVersionException(version);
            };
        }
    }
}
