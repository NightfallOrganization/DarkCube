/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.bukkit.inventoryapi.item.attribute;

public enum Operation {

	/**
	 * Adds (or subtracts) the specified amount to the base value.
	 */
	ADD_NUMBER,
	/**
	 * Adds this scalar of amount to the base value.
	 */
	ADD_SCALAR,
	/**
	 * Multiply amount by this value, after adding 1 to it.
	 */
	MULTIPLY_SCALAR_1;
}
