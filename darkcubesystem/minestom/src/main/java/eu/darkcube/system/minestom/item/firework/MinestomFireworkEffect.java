/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.item.firework;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.firework.FireworkEffect;

public interface MinestomFireworkEffect extends FireworkEffect {
    @NotNull net.minestom.server.item.firework.FireworkEffect minestomType();
}
