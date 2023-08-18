/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.skills;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SkillDash implements Skill {

    @Override
    public void execute(Player player) {
        Vector direction = player.getLocation().getDirection();
        player.setVelocity(direction.multiply(2));
    }
}
