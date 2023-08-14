package me.glicz.holograms.nms.exception;

public class UnsupportedVersionException extends RuntimeException {
    public UnsupportedVersionException(String version) {
        super("Unsupported version: %s".formatted(version));
    }
}
