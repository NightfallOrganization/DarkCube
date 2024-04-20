package eu.darkcube.system.bukkit.impl.version.latest.inventory;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryProvider;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.server.inventory.PreparedInventory;

public class BukkitInventoryProviderImpl implements InventoryProvider {
    @Override
    public @NotNull InventoryTemplate createTemplate(@NotNull Key key, @NotNull InventoryType inventoryType) {
        return null;
    }

    @Override
    public @NotNull InventoryTemplate createChestTemplate(@NotNull Key key, int size) {
        return null;
    }

    @Override
    public @NotNull PreparedInventory prepare(@NotNull InventoryType inventoryType, @NotNull Component title) {
        return null;
    }

    @Override
    public @NotNull PreparedInventory prepareChest(int size, @NotNull Component title) {
        return null;
    }
}
