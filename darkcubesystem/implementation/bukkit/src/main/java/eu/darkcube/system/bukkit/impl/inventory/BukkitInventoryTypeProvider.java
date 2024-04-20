package eu.darkcube.system.bukkit.impl.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.server.inventory.InventoryTypeProvider;

public class BukkitInventoryTypeProvider implements InventoryTypeProvider {
    @Override
    public @NotNull InventoryType of(@NotNull Object inventoryType) {
        if (inventoryType instanceof InventoryType type) {
            return type;
        } else if (inventoryType instanceof org.bukkit.event.inventory.InventoryType type) {
            return new BukkitInventoryTypeImpl(type);
        }
        throw new IllegalArgumentException("Bad InventoryType input: " + inventoryType);
    }
}
