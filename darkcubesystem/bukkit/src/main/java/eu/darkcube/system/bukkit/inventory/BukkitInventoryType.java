package eu.darkcube.system.bukkit.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryType;

public interface BukkitInventoryType extends InventoryType {
    @NotNull
    org.bukkit.event.inventory.InventoryType bukkitType();
}
