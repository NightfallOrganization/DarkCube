package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.lobbysystem.inventory.abstraction.DefaultPagedInventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryNewPServerSlot extends DefaultPagedInventory {

	public final PServerUserSlot psslot;
	public final int slot;

	public InventoryNewPServerSlot(User user, PServerUserSlot psslot, int slot) {
		super(Item.PSERVER_NEW_SLOT.getDisplayName(user, Integer.toString(slot)), Item.PSERVER_NEW_SLOT,
				InventoryType.PSERVER_SLOT);
		this.psslot = psslot;
		this.slot = slot;
	}

	@Override
	protected Map<Integer, ItemStack> contents(User user) {
		Map<Integer, ItemStack> m = new HashMap<>();
		m.put(getPageSize() / 2 - 1, Item.WORLD_PSERVER.getItem(user));
		m.put(getPageSize() / 2 + 1, Item.GAME_PSERVER.getItem(user));
		return m;
	}
}
