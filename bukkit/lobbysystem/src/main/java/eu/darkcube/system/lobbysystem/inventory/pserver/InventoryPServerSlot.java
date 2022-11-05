package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.lobbysystem.inventory.abstraction.DefaultPagedInventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryPServerSlot extends DefaultPagedInventory {

	private PServerUserSlot slot;

	public InventoryPServerSlot(User user, PServerUserSlot psslot, int slot) {
		super(Item.PSERVER_SLOT.getDisplayName(user, Integer.toString(slot)), Item.PSERVER_SLOT,
				InventoryType.PSERVER_SLOT);
		this.slot = psslot;
	}

	@Override
	protected Map<Integer, ItemStack> contents(User user) {
		Map<Integer, ItemStack> m = new HashMap<>();
		if (slot.isUsed()) {
			
		} else {
			for (int s = 0; s < getPageSize(); s++) {
				m.put(s, Item.LOADING.getItem(user));
			}
		}
		return m;
	}
}
