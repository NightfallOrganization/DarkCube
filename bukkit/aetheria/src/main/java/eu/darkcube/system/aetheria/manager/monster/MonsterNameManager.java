/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.monster;

import eu.darkcube.system.aetheria.Aetheria;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MonsterNameManager implements Listener {

    private MonsterLevelManager monsterLevelManager;

    public MonsterNameManager(MonsterLevelManager monsterLevelManager) {
        this.monsterLevelManager = monsterLevelManager;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    updateName((LivingEntity) event.getEntity());
                }
            }.runTaskLater(Aetheria.getInstance(), 1L);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    updateName((LivingEntity) event.getEntity());
                }
            }.runTaskLater(Aetheria.getInstance(), 1L);
        }
    }

    private void updateName(LivingEntity entity) {
        int level = monsterLevelManager.getMonsterLevel(entity);
        double health = monsterLevelManager.getMonsterHealth(entity);
        double maxHealth = monsterLevelManager.getMonsterMaxHealth(entity);
        double healthPercentage = Math.max(1, (health / maxHealth) * 100);

        String name = ChatColor.GOLD + "Level " + ChatColor.YELLOW + level
                + ChatColor.GRAY + " - " + ChatColor.RED
                + String.format("%.0f%%", healthPercentage);
        entity.setCustomName(name);
        entity.setCustomNameVisible(true);
    }

}
