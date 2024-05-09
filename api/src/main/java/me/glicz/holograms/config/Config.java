package me.glicz.holograms.config;

public interface Config {
    Defaults defaults();

    interface Defaults {
        int updateRange();

        double lineOffset();
    }
}
