package eu.darkcube.system.lobbysystem.inventory;

import java.util.Set;

import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.MinigameInventory;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryWoolBattle extends MinigameInventory {

	private static final InventoryType type_woolbattle = InventoryType.of("woolbattle");

	public InventoryWoolBattle(User user) {
		super(Item.INVENTORY_COMPASS_WOOLBATTLE.getDisplayName(user), Item.INVENTORY_COMPASS_WOOLBATTLE,
				InventoryWoolBattle.type_woolbattle, user);
	}

//	@Override
//	protected ItemStack getMinigameItem(User user) {
//		return Item.INVENTORY_COMPASS_WOOLBATTLE.getItem(user);
//	}

	@Override
	protected Set<String> getCloudTasks() {
		return Lobby.getInstance().getDataManager().getWoolBattleTasks();
	}

}