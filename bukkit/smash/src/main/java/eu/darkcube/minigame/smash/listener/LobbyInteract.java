package eu.darkcube.minigame.smash.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.smash.user.User;
import eu.darkcube.minigame.smash.user.UserWrapper;
import eu.darkcube.minigame.smash.util.InventoryId;
import eu.darkcube.minigame.smash.util.Item;
import eu.darkcube.minigame.smash.util.ItemManager;

public class LobbyInteract extends BaseListener {

	@EventHandler
	public void handle(PlayerInteractEvent e) {
		e.setCancelled(true);
		ItemStack item = e.getItem();
		if(item == null || item.getType() == Material.AIR || (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		String itemid = ItemManager.getItemId(item);
		if(itemid == null) {
			return;
		}
		Player p = e.getPlayer();
		User user = UserWrapper.getUser(p);
		if(itemid.equals(Item.VOTING_LIFES.getItemId())) {
			user.setInv(InventoryId.VOTING_LIFES);
		} else if(itemid.equals(Item.VOTING_MAPS.getItemId())) {
			user.setInv(InventoryId.VOTING_MAPS);
		}
	}
}
