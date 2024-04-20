package eu.darkcube.system.server.inventory;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface InventoryTemplateSettings {
    /**
     * Gets the {@link InventoryTemplate} these settings are associated with.
     *
     * @return the {@link InventoryTemplate}
     */
    @NotNull
    InventoryTemplate inventoryTemplate();
}
