/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.lobby.item;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.InventoryBuilder;
import eu.darkcube.minigame.woolbattle.util.InventoryId;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemBuilder;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.Vote;

public class ListenerItemVoting extends Listener<EventInteract> {

	@Override
	@EventHandler
	public void handle(EventInteract e) {
		try {
			ItemStack item = e.getItem();
			Player p = e.getPlayer();
			User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
			if (e.getItem().hasItemMeta()) {
				String itemid = ItemManager.getItemId(item);
				if (itemid.equals(ItemManager.getItemId(Item.LOBBY_VOTING))) {
					e.setCancelled(true);
					this.openInventory(p, user);
					return;
				}
				if (user.getOpenInventory() != null) {
					switch (user.getOpenInventory()) {
					case VOTING_EP_GLITCH:
						Boolean vote = null;
						if (itemid.equals(ItemManager.getItemId(Item.GENERAL_VOTING_FOR))) {
							vote = true;
						} else if (itemid.equals(ItemManager.getItemId(Item.GENERAL_VOTING_AGAINST))) {
							vote = false;
						}
						Vote<Boolean> old = WoolBattle.getInstance().getLobby().VOTES_EP_GLITCH.get(user);
						if (old != null) {
							if (old.vote == vote) {
								p.sendMessage(Message.ALREADY_VOTED_FOR_THIS.getMessage(user));
								e.setCancelled(true);
								break;
							}
						}
						if (vote != null) {
							WoolBattle.getInstance().getLobby().VOTES_EP_GLITCH.put(user,
									new Vote<>(System.currentTimeMillis(), vote));
							WoolBattle.getInstance().getLobby().recalculateEpGlitch();
							e.setCancelled(true);
							if (vote) {
								p.sendMessage(Message.VOTED_FOR_EP_GLITCH.getMessage(user));
								this.openEnderpearlGlitchInventory(p, user);
							} else {
								p.sendMessage(Message.VOTED_AGAINST_EP_GLITCH.getMessage(user));
								this.openEnderpearlGlitchInventory(p, user);
							}
						}
						break;
					case VOTING_MAP:
						if (!itemid.equals("map"))
							break;
						e.setCancelled(true);
						String mapname = ItemManager.getMapId(item);
						Map map = WoolBattle.getInstance().getMapManager().getMap(mapname);
						Vote<Map> vote1 = WoolBattle.getInstance().getLobby().VOTES_MAP.get(user);
						if (vote1 != null)
							if (map.equals(vote1.vote)) {
								p.sendMessage(Message.ALREADY_VOTED_FOR_MAP.getMessage(user, map.getName()));
								break;
							}
						vote1 = new Vote<>(System.currentTimeMillis(), map);
						WoolBattle.getInstance().getLobby().VOTES_MAP.put(user, vote1);
						WoolBattle.getInstance().getLobby().recalculateMap();
						p.sendMessage(Message.VOTED_FOR_MAP.getMessage(user, map.getName()));
						this.openMapsInventory(p, user);
						break;
					case VOTING_LIFES:
						if (!itemid.equals("lifes"))
							break;
						e.setCancelled(true);
						int lifes = ItemManager.getLifes(item);
						p.chat("/votelifes " + lifes);
						this.openLifesInventory(p, user);
						break;
					case VOTING:
						if (itemid.equals(ItemManager.getItemId(Item.LOBBY_VOTING_EP_GLITCH))) {
							e.setCancelled(true);
							this.openEnderpearlGlitchInventory(p, user);
						} else if (itemid.equals(ItemManager.getItemId(Item.LOBBY_VOTING_MAPS))) {
							e.setCancelled(true);
							this.openMapsInventory(p, user);
						} else if (itemid.equals(ItemManager.getItemId(Item.LOBBY_VOTING_LIFES))) {
							e.setCancelled(true);
							this.openLifesInventory(p, user);
						}
						break;
					default:
						break;
					}
				}
			}
		} catch (

		NullPointerException ex) {
			WoolBattle.getInstance()
					.sendConsole(
							"§cThe Item " + e.getItem().getItemMeta().getDisplayName() + " is not correctly set up");
			ex.printStackTrace();
		}
	}

	public void openLifesInventory(Player p, User user) {
		InventoryBuilder b = new InventoryBuilder(Message.INVENTORY_VOTING_LIFES.getMessage(user));
		b.size(9);
//		int[] array = new int[] { 1, 3, 3, 10, 5, 20, 7, 30 };
//		for (int i = 0; i < array.length; i++, i++) {
//			ItemBuilder bu = getLifesBuilder(user, array[i + 1]);
//			b.setItem(array[i], bu.build());
//		}
		b.setItem(1, this.getLifesBuilder(user, 3).build());
		b.setItem(3, this.getLifesBuilder(user, 10).build());
		b.setItem(5, this.getLifesBuilder(user, 20).build());
		b.setItem(7, this.getLifesBuilder(user, 30).build());
		p.openInventory(b.build());
		user.setOpenInventory(InventoryId.VOTING_LIFES);
	}

	public ItemBuilder getLifesBuilder(User user, int lifes) {
		ItemBuilder builder = new ItemBuilder(Material.NAME_TAG);
		builder.unsafeStackSize(true);
		builder.setAmount(lifes);
		builder.setDisplayName(ChatColor.GREEN.toString() + lifes + ChatColor.GRAY + " Leben");
		builder.getUnsafe().setString("lifes", Integer.toString(lifes)).setString("itemId", "lifes");

		int lifeVotes = 0;
		if (WoolBattle.getInstance().getLobby().VOTES_LIFES.containsKey(user)) {
			lifeVotes = WoolBattle.getInstance().getLobby().VOTES_LIFES.get(user);
		}
		if (lifeVotes == lifes) {
			builder.glow();
		}
		return builder;
	}

	public void openMapsInventory(Player p, User user) {
		InventoryBuilder b = new InventoryBuilder(Message.INVENTORY_VOTING_MAPS.getMessage(user));
		List<Map> maps = WoolBattle.getInstance()
				.getMapManager()
				.getMaps()
				.stream()
				.filter(m -> m.isEnabled())
				.collect(Collectors.toList());
		b.size(maps.size());
		Vote<Map> vote = WoolBattle.getInstance().getLobby().VOTES_MAP.get(user);
		for (int i = 0; i < maps.size(); i++) {
			Map map = maps.get(i);
			ItemBuilder bu = new ItemBuilder(map.getIcon().getMaterial()).setDurability(map.getIcon().getId());
			bu.getUnsafe().setString("itemId", "map").setString("map", map.getName());
			if (vote != null)
				if (map.equals(vote.vote))
					bu.glow();
			bu.setDisplayName(ChatColor.GREEN + map.getName());
			b.setItem(i, bu.build());
		}
		p.openInventory(b.build());
		user.setOpenInventory(InventoryId.VOTING_MAP);
	}

	public void openEnderpearlGlitchInventory(Player p, User user) {
		ItemBuilder b1 = new ItemBuilder(Item.GENERAL_VOTING_FOR.getItem(user));
		ItemBuilder b2 = new ItemBuilder(Item.GENERAL_VOTING_AGAINST.getItem(user));
		Vote<Boolean> vote = WoolBattle.getInstance().getLobby().VOTES_EP_GLITCH.get(user);
		if (vote != null)
			if (true == vote.vote) {
				b1.glow();
			} else if (false == vote.vote) {
				b2.glow();
			}
		p.openInventory(new InventoryBuilder(Message.INVENTORY_VOTING_EP_GLITCH.getMessage(user)).size(9)
				.setItem(3, b1.build())
				.setItem(5, b2.build())
				.build());
		user.setOpenInventory(InventoryId.VOTING_EP_GLITCH);
	}

	public void openInventory(Player p, User user) {
		InventoryBuilder b = new InventoryBuilder(Message.INVENTORY_VOTING.getMessage(user)).size(9);
		b.setItem(2, Item.LOBBY_VOTING_EP_GLITCH.getItem(user))
				.setItem(6, Item.LOBBY_VOTING_MAPS.getItem(user))
				.setItem(4, Item.LOBBY_VOTING_LIFES.getItem(user));
		p.openInventory(b.build());
		user.setOpenInventory(InventoryId.VOTING);
	}

}
