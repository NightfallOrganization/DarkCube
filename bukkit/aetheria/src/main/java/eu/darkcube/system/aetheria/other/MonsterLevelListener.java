/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.other;

import eu.darkcube.system.aetheria.manager.monster.MonsterLevelManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MonsterLevelListener implements Listener {

    private MonsterLevelManager monsterLevelManager;

    public MonsterLevelListener(MonsterLevelManager monsterLevelManager) {
        this.monsterLevelManager = monsterLevelManager;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity monster = (LivingEntity) event.getEntity();
        Player player = (Player) event.getDamager();

        int level = monsterLevelManager.getMonsterLevel(monster);
        double health = monsterLevelManager.getMonsterHealth(monster);
        double damage = monsterLevelManager.getMonsterDamage(monster);

        player.sendMessage("§7Monster Level: §a" + level);
        player.sendMessage("§7Monster Health: §a" + health);
        player.sendMessage("§7Monster Damage: §a" + damage);
    }
}
