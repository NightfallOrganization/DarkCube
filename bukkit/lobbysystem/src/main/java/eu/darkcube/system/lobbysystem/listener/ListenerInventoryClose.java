package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.potion.PotionEffectType;

import eu.darkcube.system.lobbysystem.inventory.InventoryPlayer;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;

public class ListenerInventoryClose extends BaseListener {

	@EventHandler
	public void handle(InventoryCloseEvent e) {
		e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
		e.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
		User user = UserWrapper.getUser(e.getPlayer().getUniqueId());
		user.setOpenInventory(new InventoryPlayer());
//		InventoryPServer.PAGE.remove(user);
	}
}
