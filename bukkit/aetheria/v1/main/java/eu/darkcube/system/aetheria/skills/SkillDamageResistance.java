/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.skills;

import org.bukkit.entity.Player;

public class SkillDamageResistance extends Skill {

    public SkillDamageResistance() {
        super("DamageResistance", false); // Setze false für passiven Skill
    }

    @Override
    public void activateSkill(Player player) {
        player.sendMessage("§7Damage Resistance Skill §aactivated§7!");
    }
}
