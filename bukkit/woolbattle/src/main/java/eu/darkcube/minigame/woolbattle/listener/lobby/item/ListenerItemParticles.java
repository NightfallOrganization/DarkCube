package eu.darkcube.minigame.woolbattle.listener.lobby.item;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;

public class ListenerItemParticles extends Listener<EventInteract> {

	@Override
	@EventHandler
	public void handle(EventInteract e) {
		ItemStack item = e.getItem();
		if (item.hasItemMeta()) {
			String itemid = ItemManager.getItemId(item);
			Player p = e.getPlayer();
			User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
			if (itemid.equals(Item.LOBBY_PARTICLES_ON.getItemId())) {
				e.setCancelled(true);
				user.getData().setParticles(false);
				e.setItem(Item.LOBBY_PARTICLES_OFF.getItem(user));
			} else if (itemid.equals(Item.LOBBY_PARTICLES_OFF.getItemId())) {
				e.setCancelled(true);
				user.getData().setParticles(true);
				e.setItem(Item.LOBBY_PARTICLES_ON.getItem(user));
			}
		}
	}
}