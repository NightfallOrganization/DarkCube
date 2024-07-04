/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.EquipmentSlot;

public interface MinestomEquipmentSlot extends EquipmentSlot {
    @NotNull net.minestom.server.entity.EquipmentSlot minestomType();
}
