/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.skylandclasssystem;

import eu.darkcube.system.skyland.equipment.PlayerStats;

public interface SkylandEntity {

	PlayerStats[] getStats();

	int getAttackDmg();

	//todo

}
