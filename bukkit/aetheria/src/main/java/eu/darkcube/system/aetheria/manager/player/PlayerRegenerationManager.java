/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.player;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public class PlayerRegenerationManager {
    private final JavaPlugin plugin;
    private final HealthManager healthManager;
    private final MaxHealthManager maxHealthManager;
    private final RegenerationManager regenerationManager;
    private final NamespacedKey regenerationRunningKey;

    public PlayerRegenerationManager(JavaPlugin plugin, HealthManager healthManager, MaxHealthManager maxHealthManager, RegenerationManager regenerationManager) {
        this.plugin = plugin;
        this.healthManager = healthManager;
        this.maxHealthManager = maxHealthManager;
        this.regenerationManager = regenerationManager;
        this.regenerationRunningKey = new NamespacedKey(plugin, "regeneration_running");
    }

    public void startRegeneration(LivingEntity livingEntity) {
        if (isRegenerationRunning(livingEntity)) {
            return;
        }

        setRegenerationRunning(livingEntity, true);

        new BukkitRunnable() {
            @Override
            public void run() {
                double currentHealth = healthManager.getHealth(livingEntity);
                double maxHealth = maxHealthManager.getMaxHealth(livingEntity);
                double regenerationRate = regenerationManager.getRegenerationRate(livingEntity);

                if (currentHealth < maxHealth) {
                    double newHealth = Math.min(currentHealth + regenerationRate, maxHealth);
                    healthManager.setHealth(livingEntity, newHealth);
                } else {
                    setRegenerationRunning(livingEntity, false);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 100L); // 100L = 5 seconds
    }

    public void stopRegeneration(LivingEntity livingEntity) {
        if (!isRegenerationRunning(livingEntity)) {
            return;
        }

        setRegenerationRunning(livingEntity, false);
    }

    private boolean isRegenerationRunning(LivingEntity livingEntity) {
        return livingEntity.getPersistentDataContainer().getOrDefault(regenerationRunningKey, PersistentDataType.INTEGER, 0) == 1;
    }

    private void setRegenerationRunning(LivingEntity livingEntity, boolean running) {
        livingEntity.getPersistentDataContainer().set(regenerationRunningKey, PersistentDataType.INTEGER, running ? 1 : 0);
    }
}
