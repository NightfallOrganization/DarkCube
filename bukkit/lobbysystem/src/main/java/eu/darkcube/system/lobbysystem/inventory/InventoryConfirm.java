package eu.darkcube.system.lobbysystem.inventory;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryConfirm extends LobbyAsyncPagedInventory {

	private static final InventoryType type_confirm = InventoryType.of("confirm");

	public final Runnable onConfirm;

	public final Runnable onCancel;

	public InventoryConfirm(String title, User user, Runnable onConfirm, Runnable onCancel) {
		super(InventoryConfirm.type_confirm, title, user);
//		super(title, null, 3 * 9, InventoryConfirm.type_confirm, box(1, 1, 3, 9));
		this.onConfirm = onConfirm;
		this.onCancel = onCancel;
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		items.put(IInventory.slot(2, 3), Item.CANCEL.getItem(this.user));
		items.put(IInventory.slot(2, 7), Item.CONFIRM.getItem(this.user));
	}

	@Override
	protected void insertFallbackItems() {
		ItemStack l = Item.LIGHT_GRAY_GLASS_PANE.getItem(this.user);
		for (int i = 0; i < this.SLOTS.length; i++) {
			int slot = this.SLOTS[i];
			this.fallbackItems.put(slot, l);
		}
		super.insertFallbackItems();
	}

}
