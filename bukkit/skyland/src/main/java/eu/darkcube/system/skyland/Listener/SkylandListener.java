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
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayerModifier;
import eu.darkcube.system.skyland.inventoryUI.UINewClassSelect;
import eu.darkcube.system.userapi.UserAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;

import javax.swing.text.html.HTML.Tag;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SkylandListener implements Listener {

	Skyland skyland;
	NamespacedKey skylandPlayer = new NamespacedKey(Skyland.getInstance(), "SkylandPlayer");

	public SkylandListener(Skyland skyland) {
		this.skyland = skyland;
	}



	public int calculateDmgAfterDefense(SkylandEntity attacker, SkylandEntity defender) {

		PlayerStats[] e2Stats = defender.getStats();

		int dmgE1 = attacker.getAttackDmg();
		System.out.println("attack pre mitigation: " + dmgE1);

		int armorE2 = 0;

		for (PlayerStats ps : e2Stats) {
			System.out.println(ps.getType() + ": " + ps.getMenge());
			if (ps.getType() == PlayerStatsType.ARMOR) {
				armorE2 += ps.getMenge();
			}
		}
		System.out.println("armor: " + armorE2);
		System.out.println("attack post mitigation: " + dmgE1 / (1 + (armorE2 / 100)));
		return dmgE1 / (1 + (armorE2 / 100));
	}

	@EventHandler
	public void handleDmg(EntityDamageByEntityEvent e) {



		if (e.getEntity() instanceof Player p) {

			p.sendMessage("you got hit by a entity");//todo remove debug

			if (e.getEntity().getMetadata("spawnProt").isEmpty()) {
				System.out.println("No Spawn prot meta found for: " + p.getUniqueId().toString());
			} else {
				if (p.getMetadata("spawnProt").get(0).asBoolean()) {
					p.sendMessage("spawn prot true");//todo remove debug
					e.setCancelled(true);
				} else {
					p.sendMessage("spawn prot false");//todo remove debug

					SkylandPlayer sp = SkylandPlayerModifier.getSkylandPlayer(p);
					int dmg = 0;

					if (e.getDamager() instanceof Player p2){
						p2.sendMessage("you are skp");//todo remove debug

						SkylandPlayer sp2 = SkylandPlayerModifier.getSkylandPlayer(p2);
						dmg = calculateDmgAfterDefense(sp2, sp);


					}else {
						//todo dmg calc for skyland entities
					}


					e.setDamage(dmg);

				}
			}

		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().sendMessage("welcome");
		e.getPlayer().setMetadata("spawnProt",
				new FixedMetadataValue(Skyland.getInstance(), false));//todo set to true just false to debug

		if (e.getPlayer().getMetadata("spawnProt").isEmpty()) {
			e.getPlayer().sendMessage("no meta");
		} else {
			e.getPlayer().sendMessage(e.getPlayer().getMetadata("spawnProt").get(0).asString());
			//e.getPlayer().sendMessage(e.getPlayer().getMetadata("spawnProt").get(1).asString());
		}

		SkylandPlayer skp = SkylandPlayerModifier.getSkylandPlayer(e.getPlayer());

		if (!skp.hastActiveClass()){

			UINewClassSelect uiNewClassSelect = new UINewClassSelect(e.getPlayer());
			uiNewClassSelect.openInv();

			//todo create command to open inv with instructions


		}

		//todo if player joined make him choose class
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e){

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

	@EventHandler
	public void onDuraDmg(PlayerItemDamageEvent e){

		e.setCancelled(true);

		//todo change the dura of stuff
	}

	@EventHandler
	public void onScrollEvent(PlayerItemHeldEvent e){
		 String text = "Ⳮ⳯⳰⳱";
		 text = text + switch (e.getNewSlot()) {
			case 0 -> "Ⲓ";
			case 1 -> "Ⲕ";
			case 2 -> "⳥";
			case 3 -> "⳦";
			case 4 -> "⳧";
			case 5 -> "⳨";
			case 6 -> "⳩";
			case 7 -> "⳪";
			case 8 -> "Ⳬ";
			default -> "";
		};

		e.getPlayer().showTitle(Title.title(Component.text(text).
				color(TextColor.color(0x4e, 0x5c, 0x24)), Component.text(""), Times.times(
				Duration.of(0, ChronoUnit.SECONDS), Duration.of(10, ChronoUnit.SECONDS),Duration.of(0, ChronoUnit.SECONDS))));

	}



}
