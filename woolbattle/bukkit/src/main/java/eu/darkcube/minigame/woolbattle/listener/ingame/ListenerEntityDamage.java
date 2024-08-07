/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class ListenerEntityDamage extends Listener<EntityDamageEvent> {
    private final WoolBattleBukkit woolbattle;

    public ListenerEntityDamage(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    @EventHandler
    public void handle(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            var user = WBUser.getUser(p);
            var ingame = woolbattle.ingame();
            if (ingame.globalSpawnProtection() || user.hasSpawnProtection()) {
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
                    if (woolbattle.gameData().epGlitch()) {
                        var ticks = user.getTicksAfterLastHit();
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
