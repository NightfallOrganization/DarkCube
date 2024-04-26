/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen;

import org.bukkit.Material;

public class TreeSet {

	int weight;
	Material leaves;
	Material log;

	public TreeSet(Material leaves, Material log) {
		this.leaves = leaves;
		this.log = log;
		weight =1;
	}

	public TreeSet(Material leaves, Material log, int weight) {
		this.leaves = leaves;
		this.log = log;
		this.weight = weight;
	}

	public Material getLeaves() {
		return leaves;
	}

	public Material getLog() {
		return log;
	}

	public int getWeight() {
		return weight;
	}
}
