package eu.darkcube.minigame.smash.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.smash.Main;
import eu.darkcube.minigame.smash.map.Map;
import eu.darkcube.minigame.smash.user.Message;
import eu.darkcube.minigame.smash.user.User;
import eu.darkcube.minigame.smash.user.UserWrapper;
import eu.darkcube.minigame.smash.util.InventoryId;
import eu.darkcube.minigame.smash.util.Item;
import eu.darkcube.minigame.smash.util.ItemBuilder;
import eu.darkcube.minigame.smash.util.ItemManager;

public class LobbyInventoryClick extends BaseListener {

	@EventHandler
	public void handle(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getWhoClicked();
		User user = UserWrapper.getUser(p);
		ItemStack item = e.getCurrentItem();
		e.setCancelled(true);
		if (item == null || item.getType() == Material.AIR) {
			return;
		}
		String itemid = ItemManager.getItemId(item);
		if (itemid == null) {
			return;
		}
		ItemBuilder builder = new ItemBuilder(item);
		if (itemid.equals("map")) {
			String mapname = builder.getUnsafe().getString("map");
			for(Map map : Map.MAPS) {
				if(map.isEnabled()) {
					if(mapname.equals(map.getName())) {
						Main.getInstance().getLobby().VOTE_MAPS.put(user, map);
						break;
					}
				}
			}
			p.sendMessage(Message.VOTED_FOR_MAP.getMessage(user, mapname));
			user.setInv(InventoryId.VOTING_MAPS);
		} else if (itemid.equals(Item.VOTING_LIFES_ITEM.getItemId())) {
			if (builder.getUnsafe().containsKey("lifes")) {
				int lifes = builder.getUnsafe().getInt("lifes");
				Main.getInstance().getLobby().VOTE_LIFES.put(user, lifes);
				p.sendMessage(Message.VOTED_FOR_LIFES.getMessage(user).replace("{0}", Integer.toString(lifes)));
				user.setInv(InventoryId.VOTING_LIFES);
			}
		}
	}
}
