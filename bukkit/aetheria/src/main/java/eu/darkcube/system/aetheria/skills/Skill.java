/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.skills;

import org.bukkit.entity.Player;

public abstract class Skill {
    protected String name;

    public Skill(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Diese Methode wird überschrieben, um den spezifischen Effekt des Skills zu definieren
    public abstract void activate(Player player);
}
