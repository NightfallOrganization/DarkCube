/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.labymod.emotes.Emotes;
import eu.darkcube.system.libs.com.github.juliarn.npc.NPC;
import eu.darkcube.system.libs.com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import eu.darkcube.system.libs.com.github.juliarn.npc.modifier.LabyModModifier;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.InventoryDailyReward;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigInteger;
import java.util.*;

public class ListenerDailyReward extends BaseListener {

	private static int randomCubes(Calendar c) {
		int maxCubes = 200;
		int minCubes = 80;
		if (c.get(Calendar.MONTH) == Calendar.DECEMBER) {
			int day = c.get(Calendar.DAY_OF_MONTH);
			if (day == 24 || day == 25 || day == 26 || day == 27 || day == 28 || day == 29
					|| day == 30 || day == 31) {
				maxCubes *= 10;
				minCubes *= 10;
			}
		}

		int cubes = minCubes + new Random().nextInt(maxCubes - minCubes + 1);
		return cubes;
	}

	@EventHandler
	public void handle(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		LobbyUser user = UserWrapper.fromUser(UserAPI.getInstance().getUser(p));
		if (user.getOpenInventory().getType() != InventoryDailyReward.type_daily_reward) {
			return;
		}
		ItemStack item = e.getCurrentItem();
		if (item == null) {
			return;
		}
		int id = ItemBuilder.item(item).persistentDataStorage()
				.get(InventoryDailyReward.reward, PersistentDataTypes.INTEGER);
		if (id == 0) {
			return;
		}

		Set<Integer> used = user.getRewardSlotsUsed();
		// used.clear();
		if (used.contains(id)) {
			p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1, 1);
			return;
		}

		used.add(id);
		user.setRewardSlotsUsed(used);

		int cubes = randomCubes(Calendar.getInstance());
		user.getUser().setCubes(user.getUser().getCubes().add(BigInteger.valueOf(cubes)));
		item = new ItemStack(Material.SULPHUR);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§a" + cubes);
		item.setItemMeta(meta);
		user.setLastDailyReward(System.currentTimeMillis());
		e.setCurrentItem(item);
		AdventureSupport.audienceProvider().player(p).sendMessage(
				Message.REWARD_COINS.getMessage(user.getUser(), Integer.toString(cubes)));
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
	}

	@EventHandler
	public void handle(PlayerNPCInteractEvent e) {
		if (e.getHand() != PlayerNPCInteractEvent.Hand.MAIN_HAND) {
			return;
		}
		NPC npc = e.getNPC();
		if (npc.equals(Lobby.getInstance().getDailyRewardNpc())) {
			if (e.getUseAction() == PlayerNPCInteractEvent.EntityUseAction.ATTACK) {
				List<Emotes> emotes = new ArrayList<>(Arrays.asList(Emotes.values()));
				emotes.remove(Emotes.INFINITY_SIT);
				e.send(npc.labymod().queue(LabyModModifier.LabyModAction.EMOTE,
						emotes.get(new Random().nextInt(emotes.size())).getId()));
			} else {
				Player p = e.getPlayer();
				LobbyUser user = UserWrapper.fromUser(UserAPI.getInstance().getUser(p));
				user.setOpenInventory(new InventoryDailyReward(user.getUser()));
			}
		}
	}

}
