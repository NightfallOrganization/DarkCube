package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.lobbysystem.gadget.Gadget;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;

public class ListenerGadget extends BaseListener {

	public static ListenerGadget instance;
	
	public ListenerGadget() {
		instance = this;
	}
	
	@EventHandler
	public void handle(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		if (item == null) {
			return;
		}
		String itemid = Item.getItemId(item);
		if (itemid == null || itemid.isEmpty()) {
			return;
		}
		User user = UserWrapper.getUser(p.getUniqueId());
		if(user.getOpenInventory().getType() != InventoryType.GADGET) {
			return;
		}
		boolean b = true;
		if (itemid.equals(Item.GADGET_GRAPPLING_HOOK.getItemId())) {
			user.setGadget(Gadget.GRAPPLING_HOOK);
		} else if (itemid.equals(Item.GADGET_HOOK_ARROW.getItemId())) {
			user.setGadget(Gadget.HOOK_ARROW);
		} else {
			b = false;
		}
		if (b) {
			p.closeInventory();
		}
	}
}
