package eu.darkcube.system.server.inventory;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface InventoryProvider {
    @NotNull
    InventoryTemplate createTemplate(@NotNull Key key, @NotNull InventoryType inventoryType);

    @NotNull
    InventoryTemplate createChestTemplate(@NotNull Key key, int size);

    @NotNull
    PreparedInventory prepare(@NotNull InventoryType inventoryType, @NotNull Component title);

    @NotNull
    PreparedInventory prepareChest(int size, @NotNull Component title);
}
