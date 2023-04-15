/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.skyland.Equipment.EquipmentType;

import java.util.LinkedList;

public enum SkylandClassTemplate {

	ARCHER;

	LinkedList<EquipmentType> allowedEquip;

	public LinkedList<EquipmentType> getAllowedEquip() {
		return allowedEquip;
	}

}
