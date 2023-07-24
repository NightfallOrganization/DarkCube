/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.map;

import eu.darkcube.minigame.woolbattle.util.Serializable;
import org.bukkit.Location;

public interface MapIngameData extends Serializable {

    int deathHeight();

    void deathHeight(int deathHeight);

    void spawn(String name, Location loc);

    Location spawn(String name);

}
