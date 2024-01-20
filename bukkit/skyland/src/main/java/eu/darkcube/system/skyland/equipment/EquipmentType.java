/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.equipment;

public enum EquipmentType {
	AXE(new ComponentType[] {}, new ComponentType[] {} ), HELMET(new ComponentType[] {}, new ComponentType[] {} )

	;
	ComponentType[] requiredComponents;
	ComponentType[] allowedUpgradeComponents;

	EquipmentType(ComponentType[] requiredComponents, ComponentType[] allowedUpgradeComponents){
		this.requiredComponents = requiredComponents;
		this.allowedUpgradeComponents = allowedUpgradeComponents;
	}


}