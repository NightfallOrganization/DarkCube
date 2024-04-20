package eu.darkcube.system.bukkit.impl.inventory;

import eu.darkcube.system.bukkit.inventory.BukkitInventoryType;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.bukkit.event.inventory.InventoryType;

public record BukkitInventoryTypeImpl(@NotNull InventoryType bukkitType) implements BukkitInventoryType {
}
