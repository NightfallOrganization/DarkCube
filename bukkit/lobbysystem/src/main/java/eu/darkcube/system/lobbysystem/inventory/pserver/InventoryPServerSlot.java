package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.Map;
import org.bukkit.inventory.ItemStack;
import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;

public class InventoryPServerSlot extends LobbyAsyncPagedInventory {

	private static final InventoryType type_pserver_slot = InventoryType.of("pserver_slot");

	private int slot;

	public InventoryPServerSlot(User user, int psslot) {
		super(InventoryPServerSlot.type_pserver_slot,
				Item.PSERVER_SLOT.getDisplayName(user, Integer.toString(psslot)), user);
		this.slot = psslot;
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5),
				Item.PSERVER_SLOT.getItem(this.user.getUser()));
		super.insertFallbackItems();
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		for (int s = 0; s < this.getPageSize(); s++) {
			items.put(s, Item.LOADING.getItem(this.user.getUser()));
		}
	}

}
