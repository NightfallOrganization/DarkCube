/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.impl.inventory;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.bukkit.event.inventory.InventoryType;

public record BukkitInventoryTypeImpl(@NotNull InventoryType bukkitType) implements BukkitInventoryType {
}
