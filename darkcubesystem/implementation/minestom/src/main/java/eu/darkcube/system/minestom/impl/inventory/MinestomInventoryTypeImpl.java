package eu.darkcube.system.minestom.impl.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import net.minestom.server.inventory.InventoryType;

public record MinestomInventoryTypeImpl(@NotNull InventoryType minestomType) implements MinestomInventoryType {
}
