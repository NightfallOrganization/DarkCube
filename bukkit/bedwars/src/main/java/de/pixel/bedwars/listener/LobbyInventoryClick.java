package de.pixel.bedwars.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.map.Map;
import de.pixel.bedwars.state.Lobby;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.I18n;
import de.pixel.bedwars.util.Item;
import de.pixel.bedwars.util.ItemManager;
import de.pixel.bedwars.util.Message;

public class LobbyInventoryClick implements Listener {

	@EventHandler
	public void handle(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if (p.getGameMode() == GameMode.CREATIVE) {
				return;
			}
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			if (item == null) {
				return;
			}
			String itemid = ItemManager.getItemId(item);
			if (itemid == null) {
				return;
			}
			Lobby lobby = Main.getInstance().getLobby();
			boolean goldVote = false;
			if (itemid.equals(Item.LOBBY_VOTING_GOLD_YES.getItemId())) {
				p.sendMessage(Message.VOTED_FOR_GOLD.getMessage(p));
				lobby.VOTE_GOLD.put(p, true);
				goldVote = true;
			} else if (itemid.equals(Item.LOBBY_VOTING_GOLD_NO.getItemId())) {
				p.sendMessage(Message.VOTED_AGAINST_GOLD.getMessage(p));
				lobby.VOTE_GOLD.put(p, false);
				goldVote = true;
			}
			if (goldVote) {
//				lobby.setItemsVotingGoldInventory(e.getClickedInventory(), p);
//				for (Player pl : lobby.INVENTORY_OPEN_GOLD.keySet()) {
//					lobby.setItemsVotingGoldInventory(lobby.INVENTORY_OPEN_GOLD.get(pl), pl);
//				}
				lobby.recalculateItemsVotingGoldInventories();
				lobby.recalculateGold();
				return;
			}
			boolean ironVote = false;
			if (itemid.equals(Item.LOBBY_VOTING_IRON_YES.getItemId())) {
				p.sendMessage(Message.VOTED_FOR_IRON.getMessage(p));
				lobby.VOTE_IRON.put(p, true);
				ironVote = true;
			} else if (itemid.equals(Item.LOBBY_VOTING_IRON_NO.getItemId())) {
				p.sendMessage(Message.VOTED_AGAINST_IRON.getMessage(p));
				lobby.VOTE_IRON.put(p, false);
				ironVote = true;
			}
			if (ironVote) {
//				lobby.setItemsVotingIronInventory(e.getClickedInventory(), p);
//				for (Player pl : lobby.INVENTORY_OPEN_IRON.keySet()) {
//					lobby.setItemsVotingIronInventory(lobby.INVENTORY_OPEN_IRON.get(pl), pl);
//				}
				lobby.recalculateItemsVotingIronInventories();
				lobby.recalculateIron();
				return;
			}
			if (itemid.equals("team")) {
				String teamid = ItemManager.getTeamId(item);
				for (Team team : Team.getTeams()) {
					if (team.getTranslationName().equals(teamid)) {
						if (team.getPlayers().size() < Team.getMaxPlayers()) {
							p.sendMessage(Message.JOINED_TEAM.getMessage(p,
									"§" + team.getNamecolor() + I18n.translate(I18n.getPlayerLanguage(p), teamid)));
							team.addPlayer0(p);
						} else {
							p.sendMessage(Message.FULL_TEAM.getMessage(p));
						}
						return;
					}
				}
			}
			if(itemid.equals("map")) {
				String mapid = ItemManager.getMapId(item);
				for(Map map : Map.getMaps()) {
					if(map.getName().equals(mapid)) {
						p.sendMessage(Message.VOTED_FOR_MAP.getMessage(p, map.getName()));
						lobby.VOTE_MAP.put(p, map);
						lobby.recalculateItemsVotingMapsInventories();
						lobby.recalculateMap();
						return;
					}
				}
			}
		}
	}
}
