/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.citybuild.listener;

import java.util.Iterator;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class AroundDamageListener implements Listener {
    private NamespacedKey aroundDamageKey;

    public AroundDamageListener(Citybuild plugin, NamespacedKey aroundDamageKey) {
        this.aroundDamageKey = aroundDamageKey;// 25
    }// 26

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();// 30
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();// 31
        if (!dataContainer.has(this.aroundDamageKey, PersistentDataType.DOUBLE)) {// 32
            dataContainer.set(this.aroundDamageKey, PersistentDataType.DOUBLE, 0.0);// 33
        }

    }// 35

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();// 39
        Entity hitEntity = event.getEntity();// 40
        if (damager instanceof Player player) {// 41
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();// 43
            if (dataContainer.has(this.aroundDamageKey, PersistentDataType.DOUBLE)) {// 44
                double aroundDamage = (Double)dataContainer.get(this.aroundDamageKey, PersistentDataType.DOUBLE);// 45
                double baseDamage = event.getDamage();// 46
                double radius = aroundDamage / 10.0;// 47
                Iterator var12 = hitEntity.getNearbyEntities(radius, radius, radius).iterator();// 48

                while(var12.hasNext()) {
                    Entity nearbyEntity = (Entity)var12.next();
                    if (!nearbyEntity.equals(hitEntity) && !(nearbyEntity instanceof Player)) {// 49
                        double distance = nearbyEntity.getLocation().distance(hitEntity.getLocation());// 50
                        double damagePercent = 1.0 - distance / radius;// 51
                        double damage = baseDamage * damagePercent;// 52
                        ((Damageable)nearbyEntity).damage(damage);// 53
                    }
                }
            }
        }

    }// 58
}
