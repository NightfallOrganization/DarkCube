/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Equipment;

public enum EquipmentType {
	AXE(new ComponentTypes[] {}, new ComponentTypes[] {} ), HELMET(new ComponentTypes[] {}, new ComponentTypes[] {} )

	;
	ComponentTypes[] requiredComponents;
	ComponentTypes[] allowedUpgradeComponents;

	EquipmentType(ComponentTypes[] requiredComponents, ComponentTypes[] allowedUpgradeComponents){
		this.requiredComponents = requiredComponents;
		this.allowedUpgradeComponents = allowedUpgradeComponents;
	}


}