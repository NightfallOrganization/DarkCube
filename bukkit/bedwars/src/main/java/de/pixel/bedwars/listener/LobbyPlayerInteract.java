package de.pixel.bedwars.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.util.Item;
import de.pixel.bedwars.util.ItemManager;

public class LobbyPlayerInteract implements Listener {

	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
			e.setCancelled(true);
		ItemStack item = e.getItem();
		if (item == null) {
			return;
		}
		String itemid = ItemManager.getItemId(item);
		if (itemid == null) {
			return;
		}
		Player p = e.getPlayer();
		if (itemid.equals(Item.LOBBY_VOTING_GOLD.getItemId())) {
			Main.getInstance().getLobby().openVotingGoldInventory(p);
		} else if (itemid.equals(Item.LOBBY_VOTING_IRON.getItemId())) {
			Main.getInstance().getLobby().openVotingIronInventory(p);
		} else if(itemid.equals(Item.LOBBY_MAPS.getItemId())) {
			Main.getInstance().getLobby().openVotingMapsInventory(p);
		} else if(itemid.equals(Item.LOBBY_TEAMS.getItemId())) {
			Main.getInstance().getLobby().openTeamsInventory(p);
		}
	}
}
