package eu.darkcube.system.lobbysystem.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.Inventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryGadget extends Inventory {

	public InventoryGadget(User user) {
		super(Bukkit.createInventory(null, 6 * 9, Item.INVENTORY_GADGET.getDisplayName(user)), InventoryType.GADGET);
	}

	@Override
	public void playAnimation(User user) {
		animate(user, false);
	}

	@Override
	public void skipAnimation(User user) {
		animate(user, true);
	}

	private void animate(User user, boolean instant) {
		user.playSound(Sound.NOTE_STICKS, 1, 1);
		new BukkitRunnable() {
			private ItemStack i1 = Item.LIGHT_GRAY_GLASS_PANE.getItem(user);
			private ItemStack i2 = Item.DARK_GRAY_GLASS_PANE.getItem(user);
			private int count = 0;

			@Override
			public void run() {
				if (count >= 13 || user.getOpenInventory() != InventoryGadget.this) {
					this.cancel();
					return;
				}
				if (!instant)
					user.playSound(Sound.NOTE_STICKS, 1, 1);
				switch (count) {
				case 0:
					handle.setItem(3, i1);
					handle.setItem(4, Item.INVENTORY_GADGET.getItem(user));
					handle.setItem(5, i1);
					handle.setItem(13, i1);
					break;
				case 1:
					handle.setItem(2, i1);
					handle.setItem(12, i1);
					handle.setItem(14, i1);
					handle.setItem(6, i1);
					break;
				case 2:
					handle.setItem(1, i1);
					handle.setItem(11, i2);
					handle.setItem(15, i2);
					handle.setItem(7, i1);
					break;
				case 3:
					handle.setItem(0, i1);
					handle.setItem(10, i2);
					handle.setItem(16, i2);
					handle.setItem(8, i1);
					handle.setItem(30, Item.GADGET_HOOK_ARROW.getItem(user));
					handle.setItem(32, Item.GADGET_GRAPPLING_HOOK.getItem(user));
					break;
				case 4:
					handle.setItem(9, i2);
					handle.setItem(17, i2);
					break;
				case 5:
					handle.setItem(18, i2);
					handle.setItem(26, i2);
					break;
				case 6:
					handle.setItem(27, i2);
					handle.setItem(35, i2);
					break;
				case 7:
					handle.setItem(36, i1);
					handle.setItem(44, i1);
					break;
				case 8:
					handle.setItem(45, i1);
					handle.setItem(53, i1);
					break;
				case 9:
					handle.setItem(46, i1);
					handle.setItem(52, i1);
					break;
				case 10:
					handle.setItem(47, i2);
					handle.setItem(51, i2);
					break;
				case 11:
					handle.setItem(48, i2);
					handle.setItem(50, i2);
					break;
				case 12:
					handle.setItem(49, i2);
					break;
				default:
					break;
				}

				count++;
				if (instant)
					run();
			}
		}.runTaskTimer(Lobby.getInstance(), 1, 1);
	}
}
