/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.perk.other.PlayerHitPlayerEvent;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ListenerEntityDamageByEntity extends Listener<EntityDamageByEntityEvent> {
    private final WoolBattleBukkit woolbattle;

    public ListenerEntityDamageByEntity(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override @EventHandler public void handle(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setDamage(0);
            WBUser target = WBUser.getUser((Player) event.getEntity());
            Ingame ingame = woolbattle.ingame();
            if (target.hasSpawnProtection() || ingame.globalSpawnProtection()) {
                event.setCancelled(true);
                return;
            }
            if (event.getDamager() instanceof Player) {
                WBUser user = WBUser.getUser((Player) event.getDamager());
                if (!ingame.playerUtil().canAttack(user, target)) {
                    event.setCancelled(true);
                } else {
                    PlayerHitPlayerEvent hitEvent = new PlayerHitPlayerEvent(user, target);
                    Bukkit.getPluginManager().callEvent(hitEvent);
                    if (hitEvent.isCancelled()) {
                        event.setCancelled(true);
                    } else if (!ingame.playerUtil().attack(user, target)) {
                        event.setCancelled(true);
                    }
                }
            }
        } else {
            event.setCancelled(true);
        }
    }
}
