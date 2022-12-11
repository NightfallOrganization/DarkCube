/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.migrator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import de.dytanic.cloudnet.driver.database.DatabaseProvider;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Migrator extends DriverModule {

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STARTED)
	public void start() {
		DatabaseProvider databaseProvider = CloudNetDriver.getInstance().getDatabaseProvider();
		if (databaseProvider.containsDatabase("pserver_userslots")) {
			Database db = databaseProvider.getDatabase("pserver_userslots");
			Database target = databaseProvider.getDatabase("pserver_data");
			Type type = new TypeToken<Map<Integer, PSSlot>>() {
			}.getType();
			Gson gson = new Gson();
			Map<String, JsonDocument> pserverDataMap = new HashMap<>();
			for (String uuidString : db.keys()) {
				Map<Integer, PSSlot> slots = gson.fromJson(db.get(uuidString).toJson(), type);
				for (PSSlot slot : slots.values()) {
					JsonDocument doc = new JsonDocument();
					if (slot.data != null) {
						if (slot.data.has("private"))
							doc.append("private", slot.data.get("private").getAsBoolean());
						if (slot.data.has("task"))
							doc.append("task", slot.data.get("task").getAsString());
						System.out.println(
								new GsonBuilder().setPrettyPrinting().create().toJson(slot.data));
						pserverDataMap.put(slot.pserverId.id, doc);
					}
				}
			}
			CountDownLatch latch = new CountDownLatch(pserverDataMap.size());
			for (Map.Entry<String, JsonDocument> e : pserverDataMap.entrySet()) {
				target.insertAsync(e.getKey(), e.getValue()).onComplete(suc -> {
					latch.countDown();
				});
			}
			try {
				latch.await();
				databaseProvider.deleteDatabase("pserver_userslots");
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class PSSlot {
		public UniqueId pserverId;
		public JsonObject data;


		private static class UniqueId {
			private String id;
		}
	}
}
