package eu.darkcube.system.pserver.node.database;

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
