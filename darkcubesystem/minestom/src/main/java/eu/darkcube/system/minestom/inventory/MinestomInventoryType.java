package eu.darkcube.system.minestom.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryType;

public interface MinestomInventoryType extends InventoryType {
    @NotNull
    net.minestom.server.inventory.InventoryType minestomType();
}
