/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldgen.structures;

import org.bukkit.Location;

public class StrucWrapper {
	SkylandStructure skylandStructure;
	Location loc;

	public StrucWrapper(SkylandStructure skylandStructure, Location loc) {
		this.skylandStructure = skylandStructure;
		this.loc = loc;
	}

	public Location getLoc() {
		return loc;
	}

	public SkylandStructure getSkylandStructure() {
		return skylandStructure;
	}
}
