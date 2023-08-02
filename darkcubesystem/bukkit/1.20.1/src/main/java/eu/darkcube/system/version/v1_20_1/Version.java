/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.version.v1_20_1;

import eu.darkcube.system.version.BukkitVersion;

public class Version extends BukkitVersion {

	@Override
	public void init() {
		super.init();
		this.commandApi = new CommandAPI1_20_1();
		this.itemProvider = new ItemProvider1_20_1();
		this.classifier = "1_20_1";
	}
}
