package eu.darkcube.system.server.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface InventoryType {
    static @NotNull InventoryType of(@NotNull Object inventoryType) {
        return InventoryTypeProviderImpl.inventoryTypeProvider().of(inventoryType);
    }
}
