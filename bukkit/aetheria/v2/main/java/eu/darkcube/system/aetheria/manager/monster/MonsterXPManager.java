/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.monster;

import eu.darkcube.system.aetheria.manager.player.XPManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.Listener;

public class MonsterXPManager implements Listener {
    private XPManager xpManager;
    private MonsterLevelManager monsterLevelManager;

    public MonsterXPManager(XPManager xpManager, MonsterLevelManager monsterLevelManager) {
        this.xpManager = xpManager;
        this.monsterLevelManager = monsterLevelManager;
    }

    public void awardXP(Player player, LivingEntity monster) {
        int monsterLevel = monsterLevelManager.getMonsterLevel(monster);
        double xpToAward = calculateXP(monsterLevel);
        double currentXP = xpManager.getXP(player);
        xpManager.setXP(player, currentXP + xpToAward);
    }

    private double calculateXP(int level) {
        return level * 1.5;
    }

}
