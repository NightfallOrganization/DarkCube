/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland.mobs;

import eu.darkcube.system.skyland.Skyland;
import eu.darkcube.system.skyland.equipment.Material;
import eu.darkcube.system.skyland.equipment.PlayerStats;
import eu.darkcube.system.skyland.equipment.Rarity;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.List;

public abstract class AbstractHostileMob extends AbstractCustomMob{

    //location from which aggro range etc are calculated
    NamespacedKey originLocationKey = new NamespacedKey(Skyland.getInstance(), "originLocation");
    NamespacedKey aggroRangeKey = new NamespacedKey(Skyland.getInstance(), "aggroRange");
    NamespacedKey tetherRangeKey = new NamespacedKey(Skyland.getInstance(), "tetherRange");

    public AbstractHostileMob(Mob mob, int dmg, PlayerStats[] stats, int speed, boolean isAware, String name, Rarity rarity, MonsterAbility[] monsterAbilities,
                              Location origin, int aggroRange, int tetherRange) {//todo finish consturctor with new values
        super(mob, dmg, stats, speed, isAware, name, rarity, monsterAbilities);
    }

    public AbstractHostileMob(Mob mob) {
        super(mob);
    }

    



}
