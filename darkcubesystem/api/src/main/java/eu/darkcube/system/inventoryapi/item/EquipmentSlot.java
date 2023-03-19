/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.inventoryapi.item;

import eu.darkcube.system.version.SinceMinecraft;

public enum EquipmentSlot {
	HAND, @SinceMinecraft(minor = 9, patch = 1) OFF_HAND, FEET, LEGS, CHEST, HEAD
}
