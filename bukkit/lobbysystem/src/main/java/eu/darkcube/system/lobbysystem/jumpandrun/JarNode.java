/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.jumpandrun;

import org.bukkit.DyeColor;
import org.bukkit.util.Vector;

import java.math.BigInteger;

public class JarNode {

	JaR jar;
	JarNode prev, next;
	JaRRegion region;
	Vector block;
	int turn = 1;
	int count;
	DyeColor color;
	BigInteger add = BigInteger.ONE;
	boolean last = false;

	public JarNode(JaR jar, Vector block) {
		this.jar = jar;
		this.block = block;
	}

	public JarNode(JarNode prev, Vector block) {
		prev.next = this;
		this.prev = prev;
		this.block = block;
		this.jar = prev.jar;
		this.region = prev.region;
		this.add = prev.add;
	}
}
