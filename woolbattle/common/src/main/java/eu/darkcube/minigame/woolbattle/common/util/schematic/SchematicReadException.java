package eu.darkcube.minigame.woolbattle.common.util.schematic;

public class SchematicReadException extends RuntimeException {
    public SchematicReadException() {
    }

    public SchematicReadException(String message) {
        super(message);
    }

    public SchematicReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchematicReadException(Throwable cause) {
        super(cause);
    }

    public SchematicReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
