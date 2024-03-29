package eu.darkcube.minigame.woolbattle.api.user;

import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;

public enum WoolSubtractDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT;

    public static final PersistentDataType<WoolSubtractDirection> TYPE = PersistentDataTypes.enumType(WoolSubtractDirection.class);

    public static WoolSubtractDirection getDefault() {
        return RIGHT_TO_LEFT;
    }
}
