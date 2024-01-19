/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.handler;

import eu.darkcube.system.aetheria.manager.monster.MonsterXPManager;
import eu.darkcube.system.aetheria.manager.player.DamageManager;
import eu.darkcube.system.aetheria.manager.player.HealthManager;
import eu.darkcube.system.aetheria.manager.player.LevelManager;
import eu.darkcube.system.aetheria.manager.player.PlayerRegenerationManager;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Objects;

public class EntityDamageHandler implements Listener {
    private HealthManager healthManager;
    private DamageManager damageManager;
    private MonsterXPManager monsterXPManager;
    private PlayerRegenerationManager playerRegenerationManager;

    public EntityDamageHandler(HealthManager healthManager, DamageManager damageManager, MonsterXPManager monsterXPManager, PlayerRegenerationManager playerRegenerationManager) {
        this.healthManager = healthManager;
        this.damageManager = damageManager;
        this.monsterXPManager = monsterXPManager;
        this.playerRegenerationManager = playerRegenerationManager;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity damaged = (LivingEntity) event.getEntity();
        double damage = 0;
        Entity damager = null;

        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event;
            damager = entityEvent.getDamager();

            if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                ProjectileSource shooter = projectile.getShooter();

                if (shooter instanceof LivingEntity) {
                    damager = (LivingEntity) shooter;
                }
            }

            if (damager instanceof LivingEntity) {
                damage = damageManager.getDamage((LivingEntity) damager);
            } else if (damager instanceof Creeper || damager instanceof TNTPrimed) {
                // Hier könnten Sie einen Standard-Schadenswert für Creeper und TNT festlegen
                damage = 10; // Beispiel: Standard-Schadenswert
            }
        }

        if (damage <= 0) {
            event.setCancelled(true);
            return;
        }

        if (damage > 0) {
            double currentHealth = healthManager.getHealth(damaged);
            double newHealth = currentHealth - damage;

            if (newHealth < 0) {
                newHealth = 0;
            }

            if (newHealth <= 0) {
            damaged.setHealth(0);
            }

            if (damage > 0 && damager instanceof Player && damaged instanceof Monster) {
                monsterXPManager.awardXP((Player) damager, (LivingEntity) damaged);
            }

            playerRegenerationManager.startRegeneration(damaged);
            healthManager.setHealth(damaged, newHealth);
            event.setDamage(0.0);
        }
    }

}