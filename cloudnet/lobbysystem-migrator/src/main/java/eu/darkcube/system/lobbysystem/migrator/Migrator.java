/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.migrator;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import de.dytanic.cloudnet.driver.database.DatabaseProvider;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;

import java.lang.reflect.Type;
import java.util.Map;

public class Migrator extends DriverModule {

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STARTED)
	public void start() {
		DatabaseProvider databaseProvider = CloudNetDriver.getInstance().getDatabaseProvider();
		if (databaseProvider.containsDatabase("pserver_userslots")) {
			Database db = databaseProvider.getDatabase("pserver_userslots");
			Type type = new TypeToken<Map<Integer, PSSlot>>() {
			}.getType();
		}
	}

	private static class PSSlot {
		public UniqueId pserverId;
		public JsonObject data;

		private static class UniqueId {
			private final String id;

			public UniqueId(String id) {
				this.id = id;
			}
		}
	}
}
