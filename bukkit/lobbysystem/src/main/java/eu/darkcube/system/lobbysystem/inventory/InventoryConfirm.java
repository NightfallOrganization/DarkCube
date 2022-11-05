package eu.darkcube.system.lobbysystem.inventory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.PagedInventory;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryConfirm extends PagedInventory {

	public final Runnable onConfirm;
	public final Runnable onCancel;

	public InventoryConfirm(String title, Runnable onConfirm, Runnable onCancel) {
		super(title, null, 3 * 9, InventoryType.CONFIRM, box(1, 1, 3, 9));
		this.onConfirm = onConfirm;
		this.onCancel = onCancel;
	}

	@Override
	protected void onOpen(User user) {

	}

	@Override
	protected void onClose(User user) {

	}

	@Override
	protected Map<Integer, ItemStack> getContents(User user) {
		Map<Integer, ItemStack> map = new HashMap<>();
		map.put(s(2, 3), Item.CANCEL.getItem(user));
		map.put(s(2, 7), Item.CONFIRM.getItem(user));
		return map;
	}

	@Override
	protected Map<Integer, ItemStack> getStaticContents(User user) {
		return new HashMap<>();
	}

	@Override
	protected void insertDefaultItems(InventoryManager manager) {
		ItemStack l = Item.LIGHT_GRAY_GLASS_PANE.getItem(manager.user);
		for (int i = 0; i < this.TOTAL_SLOTS.length; i++) {
			int slot = this.TOTAL_SLOTS[i];
			manager.setFallbackItem(slot, l);
		}
		super.insertDefaultItems(manager);
	}
}
