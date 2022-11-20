package eu.darkcube.minigame.woolbattle.listener.lobby.item;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.UserSettings;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerItemSettings extends Listener<EventInteract> {

	@Override
	@EventHandler
	public void handle(EventInteract e) {
		ItemStack item = e.getItem();
		Player p = e.getPlayer();
		if (e.getItem().hasItemMeta()) {
			if (ItemManager.getItemId(item).equals(Item.SETTINGS.getItemId())) {
				new Scheduler(
						() -> UserSettings.openSettings(WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId())))
								.runTask();
			}
		}
	}
}
