package eu.darkcube.system.server.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface InventoryTypeProvider {
    @NotNull
    InventoryType of(@NotNull Object inventoryType);
}
