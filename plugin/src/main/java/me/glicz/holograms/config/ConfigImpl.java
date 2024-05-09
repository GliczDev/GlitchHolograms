package me.glicz.holograms.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Getter
@Accessors(fluent = true)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ConfigImpl implements Config {
    private DefaultsImpl defaults;

    @Getter
    @Accessors(fluent = true)
    @ConfigSerializable
    public static class DefaultsImpl implements Defaults {
        private int updateRange = 48;
        private double lineOffset = 0.35;
    }
}