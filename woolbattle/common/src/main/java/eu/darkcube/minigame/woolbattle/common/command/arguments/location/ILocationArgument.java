/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.arguments.location;

import eu.darkcube.minigame.woolbattle.api.command.CommandSource;
import eu.darkcube.system.commandapi.util.Vector2f;
import eu.darkcube.system.commandapi.util.Vector3d;

public interface ILocationArgument {

    Vector3d getPosition(CommandSource source);

    Vector2f getRotation(CommandSource source);

    boolean isXRelative();

    boolean isYRelative();

    boolean isZRelative();
}
