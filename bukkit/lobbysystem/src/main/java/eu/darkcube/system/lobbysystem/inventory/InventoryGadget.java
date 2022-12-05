package eu.darkcube.system.lobbysystem.inventory;

import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;

public class InventoryGadget extends LobbyAsyncPagedInventory {

	public static final InventoryType type_gadget = InventoryType.of("gadget");

	public InventoryGadget(User user) {
		super(InventoryGadget.type_gadget, Item.INVENTORY_GADGET.getDisplayName(user), user);
		// super(Bukkit.createInventory(null, 6 * 9, Item.INVENTORY_GADGET.getDisplayName(user)),
		// InventoryType.GADGET);
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5),
				Item.INVENTORY_GADGET.getItem(this.user.getUser()));
		this.fallbackItems.put(IInventory.slot(4, 4),
				Item.GADGET_HOOK_ARROW.getItem(this.user.getUser()));
		this.fallbackItems.put(IInventory.slot(4, 6),
				Item.GADGET_GRAPPLING_HOOK.getItem(this.user.getUser()));
		super.insertFallbackItems();
	}

	// @Override
	// public void playAnimation(User user) {
	// this.animate(user, false);
	// }
	//
	// @Override
	// public void skipAnimation(User user) {
	// this.animate(user, true);
	// }
	//
	// private void animate(User user, boolean instant) {
	// user.playSound(Sound.NOTE_STICKS, 1, 1);
	// new BukkitRunnable() {
	// private ItemStack i1 = Item.LIGHT_GRAY_GLASS_PANE.getItem(user);
	// private ItemStack i2 = Item.DARK_GRAY_GLASS_PANE.getItem(user);
	// private int count = 0;
	//
	// @Override
	// public void run() {
	// if (this.count >= 13 || user.getOpenInventory() != InventoryGadget.this) {
	// this.cancel();
	// return;
	// }
	// if (!instant)
	// user.playSound(Sound.NOTE_STICKS, 1, 1);
	// switch (this.count) {
	// case 0:
	// InventoryGadget.this.handle.setItem(3, this.i1);
	// InventoryGadget.this.handle.setItem(4, Item.INVENTORY_GADGET.getItem(user));
	// InventoryGadget.this.handle.setItem(5, this.i1);
	// InventoryGadget.this.handle.setItem(13, this.i1);
	// break;
	// case 1:
	// InventoryGadget.this.handle.setItem(2, this.i1);
	// InventoryGadget.this.handle.setItem(12, this.i1);
	// InventoryGadget.this.handle.setItem(14, this.i1);
	// InventoryGadget.this.handle.setItem(6, this.i1);
	// break;
	// case 2:
	// InventoryGadget.this.handle.setItem(1, this.i1);
	// InventoryGadget.this.handle.setItem(11, this.i2);
	// InventoryGadget.this.handle.setItem(15, this.i2);
	// InventoryGadget.this.handle.setItem(7, this.i1);
	// break;
	// case 3:
	// InventoryGadget.this.handle.setItem(0, this.i1);
	// InventoryGadget.this.handle.setItem(10, this.i2);
	// InventoryGadget.this.handle.setItem(16, this.i2);
	// InventoryGadget.this.handle.setItem(8, this.i1);
	// InventoryGadget.this.handle.setItem(30, Item.GADGET_HOOK_ARROW.getItem(user));
	// InventoryGadget.this.handle.setItem(32, Item.GADGET_GRAPPLING_HOOK.getItem(user));
	// break;
	// case 4:
	// InventoryGadget.this.handle.setItem(9, this.i2);
	// InventoryGadget.this.handle.setItem(17, this.i2);
	// break;
	// case 5:
	// InventoryGadget.this.handle.setItem(18, this.i2);
	// InventoryGadget.this.handle.setItem(26, this.i2);
	// break;
	// case 6:
	// InventoryGadget.this.handle.setItem(27, this.i2);
	// InventoryGadget.this.handle.setItem(35, this.i2);
	// break;
	// case 7:
	// InventoryGadget.this.handle.setItem(36, this.i1);
	// InventoryGadget.this.handle.setItem(44, this.i1);
	// break;
	// case 8:
	// InventoryGadget.this.handle.setItem(45, this.i1);
	// InventoryGadget.this.handle.setItem(53, this.i1);
	// break;
	// case 9:
	// InventoryGadget.this.handle.setItem(46, this.i1);
	// InventoryGadget.this.handle.setItem(52, this.i1);
	// break;
	// case 10:
	// InventoryGadget.this.handle.setItem(47, this.i2);
	// InventoryGadget.this.handle.setItem(51, this.i2);
	// break;
	// case 11:
	// InventoryGadget.this.handle.setItem(48, this.i2);
	// InventoryGadget.this.handle.setItem(50, this.i2);
	// break;
	// case 12:
	// InventoryGadget.this.handle.setItem(49, this.i2);
	// break;
	// default:
	// break;
	// }
	//
	// this.count++;
	// if (instant)
	// this.run();
	// }
	// }.runTaskTimer(Lobby.getInstance(), 1, 1);
	// }
}
