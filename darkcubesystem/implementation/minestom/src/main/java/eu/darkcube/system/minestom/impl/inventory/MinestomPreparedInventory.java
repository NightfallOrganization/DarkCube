package eu.darkcube.system.minestom.impl.inventory;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import eu.darkcube.system.server.impl.inventory.PreparedInventoryImpl;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryType;

public class MinestomPreparedInventory extends PreparedInventoryImpl {
    public MinestomPreparedInventory(@NotNull Component title, InventoryType type) {
        super(title, type);
    }

    @Override
    public @NotNull Inventory open(@NotNull Object player) {
        var inventory = new MinestomInventory(title, (MinestomInventoryType) type);
        inventory.open(player);
        return inventory;
    }
}
