package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryNewPServerSlot extends LobbyAsyncPagedInventory {

	private static final InventoryType type_pserver_slot = InventoryType.of("pserver_slot");

	public final PServerUserSlot psslot;

	public final int slot;

	public InventoryNewPServerSlot(User user, PServerUserSlot psslot, int slot) {
		super(InventoryNewPServerSlot.type_pserver_slot,
				Item.PSERVER_NEW_SLOT.getDisplayName(user, Integer.toString(slot)), user);
		this.psslot = psslot;
		this.slot = slot;
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5), Item.PSERVER_NEW_SLOT.getItem(this.user));
		super.insertFallbackItems();
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		items.put(this.getPageSize() / 2 - 1, Item.WORLD_PSERVER.getItem(this.user));
		items.put(this.getPageSize() / 2 + 1, Item.GAME_PSERVER.getItem(this.user));
	}

}
