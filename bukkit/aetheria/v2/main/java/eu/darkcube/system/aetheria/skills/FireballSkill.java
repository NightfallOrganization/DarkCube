/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.skills;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

public class FireballSkill extends Skill {
    public FireballSkill() {
        super("Fireball", 10); // Name und Cooldown
    }

    @Override
    public void execute(Entity executor) {
        if (executor instanceof Player) {
            Player player = (Player) executor;
            player.launchProjectile(Fireball.class);
        }
    }
}
