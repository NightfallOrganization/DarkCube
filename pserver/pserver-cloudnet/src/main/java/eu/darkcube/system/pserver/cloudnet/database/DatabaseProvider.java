/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.database;

import java.util.HashMap;
import java.util.Map;

public class DatabaseProvider {

	private static Map<String, Database> dbs = new HashMap<>();
	
	public static Database get(String key) {
		return dbs.getOrDefault(key, null);
	}
	
	public static void register(String key, Database db) {
		dbs.put(key,db);
	}
}
