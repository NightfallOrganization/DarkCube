/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland.mobs;

import eu.darkcube.system.skyland.Skyland;
import org.bukkit.entity.Mob;

public abstract class AbstractMobAbility implements MonsterAbility{
    long cooldown;
    long lastActivation;

    public AbstractMobAbility(long cooldown){
        this.cooldown = cooldown;
    }

    protected boolean isCooldownReady(Mob mob, boolean startCooldown){
        if (lastActivation + cooldown < mob.getWorld().getFullTime()){
            if (startCooldown){
                lastActivation = mob.getWorld().getFullTime();
            }
            return true;
        }
        return false;
    }
}
