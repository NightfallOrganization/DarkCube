/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.team.Team;

public class IngameEntityDamage implements Listener {

	public static Map<Player, Player> lastHit = new HashMap<>();

	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		Entity damager = e.getDamager();
		Player killer = null;
		if (damager instanceof Projectile) {
			Projectile p = (Projectile) damager;
			if (p.getShooter() instanceof Entity) {
				damager = (Entity) p.getShooter();
			}
		}
		if (damager instanceof Player) {
			killer = (Player) damager;
		}
		if (killer == null) {
			return;
		}
		Team kteam = Team.getTeam(killer);
		if (kteam == Team.SPECTATOR) {
			e.setCancelled(true);
			return;
		}
		if (e.getEntity() instanceof Player) {
			Player target = (Player) e.getEntity();
			lastHit.put(target, killer);
		}
	}

	@EventHandler
	public void handle(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Team team = Team.getTeam(p);
			if (Team.getTeams().contains(team)) {
				if (p.getHealth() - e.getDamage() <= 0) {
//					new BukkitRunnable() {
//						@Override
//						public void run() {
							Main.getInstance().getIngame().kill(p);
//						}
//					}.runTask(Main.getInstance());
					e.setCancelled(true);
				}
			}
		}
	}
}
