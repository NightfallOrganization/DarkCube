/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.module;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;
import de.dytanic.cloudnet.driver.template.TemplateStorage;

import java.nio.file.Paths;

public class WoolBattle extends DriverModule {

	@ModuleTask(order = 0, event = ModuleLifeCycle.STARTED)
	public void start() {
		CloudNet.getInstance().getServicesRegistry()
				.registerService(TemplateStorage.class, "woolbattle",
						new WoolBattleTemplateStorage(Paths.get("local/woolbattle")));
	}
}
