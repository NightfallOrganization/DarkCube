package eu.darkcube.system.server.inventory;

import eu.cloudnetservice.driver.inject.InjectionLayer;

class InventoryTypeProviderImpl {
    private static final InventoryTypeProvider provider = InjectionLayer.ext().instance(InventoryTypeProvider.class);

    public static InventoryTypeProvider inventoryTypeProvider() {
        return provider;
    }
}
