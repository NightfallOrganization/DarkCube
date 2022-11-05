package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.lobbysystem.inventory.abstraction.DefaultPagedInventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryGameServerSelection extends DefaultPagedInventory {

	public final PServerUserSlot psslot;
	public final int slot;

	public InventoryGameServerSelection(User user, PServerUserSlot psslot, int slot) {
		super(Item.GAME_PSERVER.getDisplayName(user), Item.GAME_PSERVER, InventoryType.GAMESERVER_SELECTION);
		this.psslot = psslot;
		this.slot = slot;
	}

	@Override
	protected Map<Integer, ItemStack> contents(User user) {
		Map<Integer, ItemStack> m = new HashMap<>();
		m.put(getPageSize() / 2, Item.GAMESERVER_SELECTION_WOOLBATTLE.getItem(user));
		return m;
	}
}
