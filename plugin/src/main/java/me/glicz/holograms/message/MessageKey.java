package me.glicz.holograms.message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageKey {
    COMMAND_ERROR_UNKNOWN_HOLOGRAM("command.error.unknown-hologram"),
    COMMAND_ERROR_INVALID_LINE("command.error.invalid-line"),
    COMMAND_CREATE("command.create"),
    COMMAND_DELETE("command.delete"),
    COMMAND_LINE_ADD("command.line.add"),
    COMMAND_LINE_INSERT("command.line.insert"),
    COMMAND_LINE_MODIFY("command.line.modify"),
    COMMAND_LINE_REMOVE("command.line.remove"),
    COMMAND_RELOAD("command.reload"),
    ;

    final String path;
}
