package eu.darkcube.system.server.inventory;

import eu.cloudnetservice.driver.inject.InjectionLayer;

class InventoryProviderImpl {
    private static final InventoryProvider provider = InjectionLayer.ext().instance(InventoryProvider.class);

    static InventoryProvider inventoryProvider() {
        return provider;
    }
}
