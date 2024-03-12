/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.game;

import eu.darkcube.minigame.woolbattle.api.event.entity.ProjectileHitBlockEvent;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.projectile.ProjectileCollideWithBlockEvent;
import net.minestom.server.event.trait.InstanceEvent;

public class MinestomListeners {
    public static void configure(EventNode<InstanceEvent> minestom, CommonGame game) {
        var events = game.eventManager();
        minestom.addListener(ProjectileCollideWithBlockEvent.class, event -> {
            events.call(new ProjectileHitBlockEvent(projectile, block));
        });
    }
}
