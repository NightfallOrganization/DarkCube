/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class ListenerEntityDamage extends Listener<EntityDamageEvent> {
    @Override
    @EventHandler
    public void handle(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            WBUser user = WBUser.getUser(p);
            Ingame ingame = WoolBattleBukkit.instance().ingame();
            if (ingame.isGlobalSpawnProtection || user.hasSpawnProtection()) {
                e.setCancelled(true);
                return;
            }
            e.setDamage(0);
            switch (e.getCause()) {
                case FALL:
                case STARVATION:
                    e.setCancelled(true);
                    break;
                case SUFFOCATION:
                    if (WoolBattleBukkit.instance().gameData().epGlitch()) {
                        int ticks = user.getTicksAfterLastHit();
                        if (ticks < 200) {
                            ticks += 60;
                            user.setTicksAfterLastHit(ticks);
                        }
                        break;
                    }
                    e.setCancelled(true);
                    break;
                default:
                    break;
            }
        }
    }
}
