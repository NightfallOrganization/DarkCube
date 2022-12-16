/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.gadget;

public enum Gadget {

	HOOK_ARROW, GRAPPLING_HOOK;

	public static Gadget fromString(String gadget) {
		for (Gadget g : Gadget.values()) {
			if (g.name().equalsIgnoreCase(gadget)) {
				return g;
			}
		}
		return HOOK_ARROW;
	}
}
