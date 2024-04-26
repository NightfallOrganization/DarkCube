/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.jumpandrun;

import eu.darkcube.system.lobbysystem.listener.BaseListener;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class JaRListener extends BaseListener {
    private final JaR jar;

    public JaRListener(JaR jar) {
        this.jar = jar;
    }

    @EventHandler public void handle(PlayerMoveEvent event) {
        if (jar.destroyed) {
            return;
        }
        if (event.getPlayer() != jar.getUser().asPlayer()) {
            return;
        }
        if (jar.getCurrentBlock() == null) {
            return;
        }
        Location to = event.getTo();
        if (to.getY() < jar.getCurrentBlock().getLocation().getY() - 5) {
            jar.reset();
            return;
        }
        if (to.distance(jar.getCurrentBlock().getLocation().add(1, 1.5, 1)) < 1.5) {
            if (jar.node.prev != null && jar.node.prev.color != jar.node.color) {
                jar.user.playSound(Sound.NOTE_PLING, 50, 1);
                jar.user.user().cubes(jar.user.user().cubes().add(jar.node.add));
            }
            if (jar.getNextBlock() == null) {
                jar.startFromBeginning();
            }
        }
    }
}
