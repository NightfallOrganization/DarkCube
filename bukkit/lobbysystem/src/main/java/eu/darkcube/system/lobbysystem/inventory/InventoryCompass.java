package eu.darkcube.system.lobbysystem.inventory;

import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryCompass extends LobbyAsyncPagedInventory {

	private static final InventoryType type_compass = InventoryType.of("compass");

	public InventoryCompass(User user) {
//		super(InventoryCompass.type_compass,
//				Bukkit.createInventory(null, 6 * 9, ChatColor.stripColor(Item.INVENTORY_COMPASS.getDisplayName(user))));
		super(InventoryCompass.type_compass, Item.INVENTORY_COMPASS.getDisplayName(user), user);
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5), Item.INVENTORY_COMPASS_SPAWN.getItem(this.user));
		this.fallbackItems.put(IInventory.slot(4, 5), Item.INVENTORY_COMPASS_WOOLBATTLE.getItem(this.user));
		super.insertFallbackItems();
	}

}
