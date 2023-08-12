/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.npc;

import com.github.juliarn.npclib.api.protocol.enums.EntityAnimation;
import eu.darkcube.system.labymod.emotes.Emotes;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class NPCKnockbackThread extends BukkitRunnable {

    private final NPCManagement.NPC npc;

    public NPCKnockbackThread(NPCManagement.NPC npc) {
        this.npc = npc;
    }

    @Override public void run() {
        final Location loc = npc.location();
        if (loc == null) return;
        loc
                .getWorld()
                .getNearbyEntities(loc.clone().add(0, 0.6, 0), 0.3, 0.7, 0.3)
                .stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .filter(npc::shownFor)
                .filter(e -> !e.hasPermission("system.npc.knockback.ignore"))
                .forEach(p -> {
                    if (p.isFlying()) return;
                    Location loc1 = p.getLocation();
                    double x = loc1.getX() - loc.getX();
                    double y = loc1.getY() - loc.getY();
                    double z = loc1.getZ() - loc.getZ();
                    p.setVelocity(new Vector(x, Math.abs(y) > 1.7 ? 2 : Math.abs(y) + 0.3, z).normalize().multiply(.7));
                    if (Math.random() < 0.3) npc.sendEmotes(p, Emotes.KARATE.getId());
                    else npc.animation(p, EntityAnimation.SWING_MAIN_ARM);
                });
    }
}
