/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
