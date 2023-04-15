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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class SkylandListener implements Listener {

	Skyland skyland;

	public SkylandListener(Skyland skyland) {
		this.skyland = skyland;
	}

	public int calculateDmgAfterDefense(SkylandEntity e1, SkylandEntity e2) {

		PlayerStats[] e1Stats = e1.getStats();
		PlayerStats[] e2Stats = e2.getStats();

		int dmgE1 = 0;
		//int dmgE2 = 0;

		//int armorE1 = 0;
		int armorE2 = 0;

		for (PlayerStats ps : e2Stats) {
			if (ps.getType() == PlayerStatsType.ARMOR) {
				armorE2 += ps.getMenge();
			}
		}

		dmgE1 = e1.getAttackDmg();
		//todo get the dmg val

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

		if (e.getPlayer().getMetadata("skylandPlayer").isEmpty()) {
			e.getPlayer().sendMessage("Welcome to Skyland!");
			e.getPlayer().sendMessage("Please choose a Class.");

			int i = 1;
			for (SkylandClassTemplate sct : SkylandClassTemplate.values()) {
				e.getPlayer().sendMessage(i + ": " + sct.toString());
			}
			e.getPlayer().setMetadata("selectingClass",
					new FixedMetadataValue(Skyland.getInstance(), true));

			//todo make player select a class
		} else {
			e.getPlayer().sendMessage("Welcome back to Skyland!");
			Skyland.getInstance().addSkylandPlayer(SkylandPlayer.parseFromString(
					e.getPlayer().getMetadata("skylandPlayer").get(0).asString()));

		}
	}

	@EventHandler
	public void onMessageSend(PlayerChatEvent e) {
		List<MetadataValue> mv = e.getPlayer().getMetadata("selectingClass");
		if (!mv.isEmpty()) {
			if (mv.get(0).asBoolean()) {
				int nr;
				try {
					nr = Integer.parseInt(e.getMessage());
					nr--;
					if (SkylandClassTemplate.values().length > nr) {
						e.getPlayer().sendMessage(
								"you selected: " + SkylandClassTemplate.values()[nr].toString());
						e.getPlayer().setMetadata("selectingClass",
								new FixedMetadataValue(Skyland.getInstance(), false));
						//todo initialize the skylandplayerclass and save it ot the meta data
					} else {
						e.getPlayer().sendMessage("invalid choice");
					}
				} catch (Exception ex) {
					e.getPlayer().sendMessage("Please input a number");
				}

			}
		}
	}

}
