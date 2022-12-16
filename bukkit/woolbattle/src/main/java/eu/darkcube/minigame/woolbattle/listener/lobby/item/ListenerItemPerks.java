/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.lobby.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.mysql.MySQL;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.perk.PlayerPerks;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.InventoryBuilder;
import eu.darkcube.minigame.woolbattle.util.InventoryId;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemBuilder;
import eu.darkcube.minigame.woolbattle.util.ItemManager;

public class ListenerItemPerks extends Listener<EventInteract> {

	@Override
	@EventHandler
	public void handle(EventInteract e) {
		try {
			if (e.getItem().hasItemMeta()) {
				String itemid = ItemManager.getItemId(e.getItem());
				Player p = e.getPlayer();
				User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
				InventoryId inv = null;
				if (itemid.equals(Item.LOBBY_PERKS.getItemId())) {
					if (user.getOpenInventory() != InventoryId.PERKS) {
						openInventory(p, user);
					}
					e.setCancelled(true);
				} else if (itemid.contentEquals(Item.LOBBY_PERKS_1.getItemId())) {
					inv = InventoryId.PERKS_1;
				} else if (itemid.contentEquals(Item.LOBBY_PERKS_2.getItemId())) {
					inv = InventoryId.PERKS_2;
				} else if (itemid.contentEquals(Item.LOBBY_PERKS_3.getItemId())) {
					inv = InventoryId.PERKS_3;
				}
				if (inv != null) {
					e.setCancelled(true);
					openInventory(inv, p, user);
					return;
				}
				if (user.getOpenInventory() == InventoryId.PERKS_1 || user.getOpenInventory() == InventoryId.PERKS_2
						|| user.getOpenInventory() == InventoryId.PERKS_3) {
					for (PerkType item : PerkType.values()) {
						String perkItemId = item.getItem().getItemId();
						if (perkItemId.equals(itemid)) {
							e.setCancelled(true);
							PlayerPerks temp = user.getData().getPerks();
							Collection<PerkType> userPerks = Arrays.asSet(temp.getActivePerk1().toType(),
									temp.getActivePerk2().toType(), temp.getPassivePerk().toType());
							if (userPerks.contains(item)) {
								p.sendMessage(Message.ALREADY_SELECTED_PERK.getMessage(user));
								return;
							}
							switch (user.getOpenInventory()) {
							case PERKS_1:
								user.getData().getPerks().setActivePerk1(item.getPerkName());
								break;
							case PERKS_2:
								user.getData().getPerks().setActivePerk2(item.getPerkName());
								break;
							case PERKS_3:
								user.getData().getPerks().setPassivePerk(item.getPerkName());
								break;
							default:
								throw new ConcurrentModificationException(
										"Cant modify users inventory while in this method");
							}
							MySQL.saveUserData(user);
							openInventory(user.getOpenInventory(), p, user);
							break;
						}
					}
				}
			}
		} catch (NullPointerException ex) {
			WoolBattle.getInstance().sendConsole(
					"§cThe Item " + e.getItem().getItemMeta().getDisplayName() + " is not correctly set up: ");
			ex.printStackTrace();
		}
	}

	public void openInventory(InventoryId perkInventory, Player p, User user) {
		InventoryBuilder builder = null;
		switch (perkInventory) {
		case PERKS_1:
			builder = new InventoryBuilder(Item.LOBBY_PERKS_1.getDisplayName(user));
			break;
		case PERKS_2:
			builder = new InventoryBuilder(Item.LOBBY_PERKS_2.getDisplayName(user));
			break;
		case PERKS_3:
			builder = new InventoryBuilder(Item.LOBBY_PERKS_3.getDisplayName(user));
			break;
		default:
			p.sendMessage("§cERROR WHILE OPENING PERK INVENTORY: " + perkInventory.name());
			return;
		}
		List<PerkType> perks = null;
		switch (perkInventory) {
		case PERKS_1:
		case PERKS_2:
			perks = Arrays.asList(PerkType.values()).stream().filter(type -> !type.isPassive())
					.collect(Collectors.toList());
			break;
		case PERKS_3:
			perks = Arrays.asList(PerkType.values()).stream().filter(type -> type.isPassive())
					.collect(Collectors.toList());
			break;
		default:
			p.sendMessage("§cERROR WHILE OPENING PERK INVENTORY: " + perkInventory.name());
			return;
		}
		perks = new ArrayList<>(perks);
		builder.size(perks.size());

		PlayerPerks temp = user.getData().getPerks();
		Collection<PerkType> userPerks = Arrays.asSet(temp.getActivePerk1().toType(), temp.getActivePerk2().toType(),
				temp.getPassivePerk().toType());
		Collections.sort(perks);
		for (int i = 0; i < perks.size(); i++) {
			PerkType type = perks.get(i);
			ItemBuilder bu = new ItemBuilder(type.getItem().getItem(user));
			if (userPerks.contains(type)) {
				boolean con = false;
				switch (perkInventory) {
				case PERKS_1:
					con = temp.getActivePerk1().equals(type.getPerkName());
					break;
				case PERKS_2:
					con = temp.getActivePerk2().equals(type.getPerkName());
					break;
				case PERKS_3:
					con = temp.getPassivePerk().equals(type.getPerkName());
					break;
				default:
					break;
				}
				if (con) {
					bu.glow();
				}
				bu.addLore(Message.SELECTED.getMessage(user));
			}
			builder.setItem(i, bu.build());
		}
		p.openInventory(builder.build());
		user.setOpenInventory(perkInventory);
	}

	public void openInventory(Player p, User user) {
		InventoryBuilder builder = new InventoryBuilder(Message.INVENTORY_PERKS.getMessage(user));
//		builder.size(18);
//		PerkType[] types = PerkType.values();
//		for (int i = 0; i < types.length; i++) {
//			PerkType type = types[i];
//			builder.setItem(i, type.newPerkTypePerk(user, PerkNumber.DISPLAY).getItem().getItem(user));
//		}
		builder.size(9).setItem(0, Item.LOBBY_PERKS_1.getItem(user)).setItem(1, Item.LOBBY_PERKS_2.getItem(user))
				.setItem(2, Item.LOBBY_PERKS_3.getItem(user));
		user.setOpenInventory(InventoryId.PERKS);
		p.openInventory(builder.build());
	}
}
