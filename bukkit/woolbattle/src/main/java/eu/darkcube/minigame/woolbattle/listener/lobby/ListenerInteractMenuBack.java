package eu.darkcube.minigame.woolbattle.listener.lobby;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerInteractMenuBack extends Listener<EventInteract> {

	@Override
	@EventHandler(priority = EventPriority.LOW)
	public void handle(EventInteract e) {
		Player p = e.getPlayer();
		User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
		if (!e.getItem().hasItemMeta() && e.getClick() == ClickType.RIGHT) {
			if (user.getOpenInventory() != null) {
				switch (user.getOpenInventory()) {
				case PERKS_1:
				case PERKS_2:
				case PERKS_3:
					e.setCancelled(true);
					WoolBattle.getInstance().getLobby().listenerItemPerks.openInventory(p, user);
					break;
				case TEAMS:
				case VOTING:
				case GADGETS:
				case PERKS:
				case COMPASS_TELEPORT:
					break;
				case VOTING_EP_GLITCH:
				case VOTING_MAP:
				case VOTING_LIFES:
					e.setCancelled(true);
					WoolBattle.getInstance().getLobby().listenerItemVoting.openInventory(p, user);
					break;
				default:
					break;
				}
			}
		}
	}
}