/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Listener;

import eu.darkcube.system.skyland.Equipment.PlayerStats;
import eu.darkcube.system.skyland.Equipment.PlayerStatsType;
import eu.darkcube.system.skyland.Skyland;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandClassTemplate;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandEntity;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayer;
import eu.darkcube.system.skyland.inventoryUI.UINewClassSelect;
import eu.darkcube.system.userapi.UserAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class SkylandListener implements Listener {

	Skyland skyland;
	NamespacedKey skylandPlayer = new NamespacedKey(Skyland.getInstance(), "SkylandPlayer");

	public SkylandListener(Skyland skyland) {
		this.skyland = skyland;
	}



	public int calculateDmgAfterDefense(SkylandEntity attacker, SkylandEntity defender) {

		PlayerStats[] e1Stats = attacker.getStats();
		PlayerStats[] e2Stats = defender.getStats();

		int dmgE1 = attacker.getAttackDmg();

		int armorE2 = 0;

		for (PlayerStats ps : e2Stats) {
			if (ps.getType() == PlayerStatsType.ARMOR) {
				armorE2 += ps.getMenge();
			}
		}

		return dmgE1 / (1 + (armorE2 / 100));
	}

	@EventHandler
	public void handleDmg(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = ((Player) e.getEntity());

			if (e.getEntity().getMetadata("spawnProt").isEmpty()) {
				System.out.println("No Spawn prot meta found for: " + p.getUniqueId().toString());
			} else {
				if (p.getMetadata("spawnProt").get(0).asBoolean()) {
					e.setCancelled(true);
				} else {
					SkylandPlayer sp = Skyland.getInstance().getSkylandPlayers(p);

					//todo dmg claculation
					int dmg = 0;

					e.setDamage(dmg);

				}
			}

		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().sendMessage("welcome");
		e.getPlayer().setMetadata("spawnProt",
				new FixedMetadataValue(Skyland.getInstance(), true));//todo 9

		if (e.getPlayer().getMetadata("spawnProt").isEmpty()) {
			e.getPlayer().sendMessage("no meta");
		} else {
			e.getPlayer().sendMessage(e.getPlayer().getMetadata("spawnProt").get(0).asString());
			//e.getPlayer().sendMessage(e.getPlayer().getMetadata("spawnProt").get(1).asString());
		}

		if (e.getPlayer().getPersistentDataContainer().has(skylandPlayer)) {
			e.getPlayer().sendMessage("Welcome back to Skyland!");
			e.getPlayer().sendMessage("loaded: " + e.getPlayer().getPersistentDataContainer().get(skylandPlayer, PersistentDataType.STRING));//todo rem
			SkylandPlayer skp = SkylandPlayer.parseFromString(
					e.getPlayer().getPersistentDataContainer().get(skylandPlayer, PersistentDataType.STRING), e.getPlayer());
			Skyland.getInstance().addSkylandPlayer(skp);
			System.out.println("curr" + skp.toString());//todo rem

			//todo make player select a class
		} else {
			e.getPlayer().sendMessage("Welcome to Skyland!");
			SkylandPlayer skp = new SkylandPlayer(e.getPlayer());
			e.getPlayer().getPersistentDataContainer().set(skylandPlayer, PersistentDataType.STRING, skp.toString());
			Skyland.getInstance().addSkylandPlayer(skp);
			e.getPlayer().sendMessage("Please choose a Class.");//todo either per command or per npc or both
			UINewClassSelect ncls = new UINewClassSelect(e.getPlayer());
			ncls.openInv();

		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e){
		e.getPlayer().getPersistentDataContainer().remove(skylandPlayer);
		e.getPlayer().getPersistentDataContainer().set(skylandPlayer, PersistentDataType.STRING, Skyland.getInstance().getSkylandPlayers(e.getPlayer()).toString());
	}

	@EventHandler
	public void onMessageSend(AsyncChatEvent e) {

		/*
		List<MetadataValue> mv = e.getPlayer().getMetadata("selectingClass");
		if (!mv.isEmpty()) {
			if (mv.get(0).asBoolean()) {
				int nr;
				try {
					nr = Integer.parseInt(e.message().toString());
					nr--;
					if (SkylandClassTemplate.values().length > nr) {
						e.getPlayer().sendMessage(
								"you selected: " + SkylandClassTemplate.values()[nr].toString());
						e.getPlayer().setMetadata("selectingClass",
								new FixedMetadataValue(Skyland.getInstance(), false));
					} else {
						e.getPlayer().sendMessage("invalid choice");
					}
				} catch (Exception ex) {
					e.getPlayer().sendMessage("Please input a number");
				}

			}
		}

		 */
	}

	public void onDuraDmg(PlayerItemDamageEvent e){

		e.setCancelled(true);

		//todo change the dura of stuff
	}



}
