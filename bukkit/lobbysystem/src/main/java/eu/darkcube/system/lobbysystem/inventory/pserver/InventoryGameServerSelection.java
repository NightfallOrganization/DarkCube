package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryGameServerSelection extends LobbyAsyncPagedInventory {

	private static final InventoryType type_gameserver_selection = InventoryType.of("gameserver_selection");

	public final PServerUserSlot psslot;

	public final int slot;

	public InventoryGameServerSelection(User user, PServerUserSlot psslot, int slot) {
		super(InventoryGameServerSelection.type_gameserver_selection, Item.GAME_PSERVER.getDisplayName(user), user);
		this.psslot = psslot;
		this.slot = slot;
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5), Item.GAME_PSERVER.getItem(this.user));
		super.insertFallbackItems();
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		items.put(this.getPageSize() / 2, Item.GAMESERVER_SELECTION_WOOLBATTLE.getItem(this.user));
	}

}
