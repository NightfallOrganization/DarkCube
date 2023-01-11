/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.npc;

import eu.darkcube.system.labymod.emotes.Emotes;
import eu.darkcube.system.libs.com.github.juliarn.npc.NPC;
import eu.darkcube.system.libs.com.github.juliarn.npc.modifier.AnimationModifier.EntityAnimation;
import eu.darkcube.system.libs.com.github.juliarn.npc.modifier.LabyModModifier.LabyModAction;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class NPCKnockbackThread extends BukkitRunnable {

	private final NPC npc;

	public NPCKnockbackThread(NPC npc) {
		this.npc = npc;
	}

	@Override
	public void run() {
		npc.getLocation().getWorld()
				.getNearbyEntities(npc.getLocation().clone().add(0, 0.6, 0), 0.3, 0.7, 0.3).stream()
				.filter(Player.class::isInstance).map(Player.class::cast).filter(npc::isShownFor)
				.filter(e -> !e.hasPermission("system.npc.knockback.ignore")).forEach(p -> {
					if (p.isFlying())
						return;
					Location loc1 = p.getLocation();
					Location loc2 = npc.getLocation();
					double x = loc1.getX() - loc2.getX();
					double y = loc1.getY() - loc2.getY();
					double z = loc1.getZ() - loc2.getZ();
					p.setVelocity(new Vector(x, Math.abs(y) > 1.7 ? 2 : Math.abs(y) + 0.3, z).normalize()
							.multiply(.7));
					npc.animation().queue(EntityAnimation.SWING_MAIN_ARM).send(p);
					npc.labymod().queue(LabyModAction.EMOTE, Emotes.KARATE.getId()).send(p);
				});
	}
}
