/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.monster;

import eu.darkcube.system.aetheria.manager.shared.DamageManager;
import eu.darkcube.system.aetheria.manager.shared.HealthManager;
import eu.darkcube.system.aetheria.manager.shared.LevelManager;
import eu.darkcube.system.aetheria.manager.player.MaxHealthManager;
import org.bukkit.entity.LivingEntity;

public class MonsterLevelManager {
    private HealthManager healthManager;
    private MaxHealthManager maxHealthManager;
    private LevelManager levelManager;
    private DamageManager damageManager;

    public MonsterLevelManager(HealthManager healthManager, MaxHealthManager maxHealthManager, LevelManager levelManager, DamageManager damageManager) {
        this.healthManager = healthManager;
        this.maxHealthManager = maxHealthManager;
        this.levelManager = levelManager;
        this.damageManager = damageManager;
    }

    public void updateMonsterLevel(LivingEntity monster, int level) {
        levelManager.setLevel(monster, level);
        double health = calculateHealth(level);
        double maxHealth = calculateMaxHealth(level);
        double damage = calculateDamage(level);
        maxHealthManager.setMaxHealth(monster, maxHealth);
        healthManager.setHealth(monster, health);
        damageManager.setDamage(monster, damage);
    }

    private double calculateHealth(int level) {
        return 10 + (level - 1) * (7990.0 / 99);
    }

    private double calculateMaxHealth(int level) {
        return 10 + (level - 1) * (7980.0 / 99);
    }

    private double calculateDamage(int level) {
        return 1 + (level - 1) * (399.0 / 99);
    }

    public int getMonsterLevel(LivingEntity monster) {
        return levelManager.getLevel(monster);
    }

    public double getMonsterHealth(LivingEntity monster) {
        return healthManager.getHealth(monster);
    }

    public double getMonsterMaxHealth(LivingEntity monster) {
        return maxHealthManager.getMaxHealth(monster);
    }

    public double getMonsterDamage(LivingEntity monster) {
        return damageManager.getDamage(monster);
    }

}
