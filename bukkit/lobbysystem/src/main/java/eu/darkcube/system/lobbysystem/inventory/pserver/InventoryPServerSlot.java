package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryPServerSlot extends LobbyAsyncPagedInventory {

	private static final InventoryType type_pserver_slot = InventoryType.of("pserver_slot");

	private PServerUserSlot slot;

	public InventoryPServerSlot(User user, PServerUserSlot psslot, int slot) {
		super(InventoryPServerSlot.type_pserver_slot, Item.PSERVER_SLOT.getDisplayName(user, Integer.toString(slot)),
				user);
		this.slot = psslot;
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5), Item.PSERVER_SLOT.getItem(this.user));
		super.insertFallbackItems();
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		if (this.slot.isUsed()) {

		} else {
			for (int s = 0; s < this.getPageSize(); s++) {
				items.put(s, Item.LOADING.getItem(this.user));
			}
		}
	}

}
