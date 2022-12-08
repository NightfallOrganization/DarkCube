/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.knockout;

import eu.darkcube.system.DarkCubePlugin;

public class KnockOut extends DarkCubePlugin {

	private static KnockOut instance;
	
	public KnockOut() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static KnockOut getInstance() {
		return instance;
	}
}
