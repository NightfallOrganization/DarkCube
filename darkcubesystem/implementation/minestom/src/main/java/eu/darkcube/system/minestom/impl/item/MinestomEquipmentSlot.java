/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.impl.item;

import eu.darkcube.system.server.item.EquipmentSlot;

public record MinestomEquipmentSlot(net.minestom.server.entity.EquipmentSlot minestomType) implements EquipmentSlot {
}
