package eu.darkcube.system.minestom.impl.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.server.inventory.InventoryTypeProvider;

public class MinestomInventoryTypeProvider implements InventoryTypeProvider {
    @Override
    public @NotNull InventoryType of(@NotNull Object inventoryType) {
        if (inventoryType instanceof net.minestom.server.inventory.InventoryType minestomType) {
            return new MinestomInventoryTypeImpl(minestomType);
        } else if (inventoryType instanceof InventoryType type) {
            return type;
        } else {
            throw new IllegalArgumentException("Bad InventoryType: " + inventoryType);
        }
    }
}
