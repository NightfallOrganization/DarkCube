/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.skills;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class SkillDash extends Skill {

    public SkillDash() {
        super("Dash", true);
    }

    @Override public void activateSkill(Player player) {
        Vector direction = player.getLocation().getDirection();

        // Begrenze die Y-Komponente des Vektors.
        if (direction.getY() > 0.4) {
            direction.setY(0.4);
        }

        player.setVelocity(direction.multiply(2));
        player.sendMessage("§7Dash Skill §aactivated§7!");

    }
}
