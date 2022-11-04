package eu.darkcube.minigame.woolbattle.listener.lobby.item;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemBuilder;

public class ListenerItemGadgets extends Listener<EventInteract> {

	@Override
	@EventHandler
	public void handle(EventInteract e) {
		ItemBuilder item = new ItemBuilder(e.getItem());
		ItemBuilder compare = new ItemBuilder(Item.LOBBY_GADGET.getItem(e.getUser()));
		Player p = e.getPlayer();
		User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
		try {
			if (e.getItem().hasItemMeta()) {
				if (item.getUnsafe().getString("itemId").equals(compare.getUnsafe().getString("itemId"))) {
					p.sendMessage(Message.NOT_IMPLEMENTED.getMessage(user));
					if (!e.isInteract())
						e.setCancelled(true);
				}
			}
		} catch (NullPointerException ex) {
			Main.getInstance().sendConsole("§cThe Item " + item.toJson() + " is not correctly set up");
		}
	}
}
