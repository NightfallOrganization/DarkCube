package eu.darkcube.api.cubes;

import java.math.BigInteger;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;

public class CubesAPI extends JavaPlugin {

	private static final Database DATABASE = CloudNetDriver.getInstance().getDatabaseProvider()
			.getDatabase("cubesapi_cubes");

	public static BigInteger getCubes(UUID user) {
		checkAndInsert(user);
		JsonDocument doc = DATABASE.get(user.toString());
		return doc.getBigInteger("cubes");
	}

	private static void checkAndInsert(UUID uuid) {
		if (!DATABASE.contains(uuid.toString())) {
			DATABASE.insert(uuid.toString(), new JsonDocument().append("cubes", BigInteger.valueOf(1000)));
		}
	}

	public static void setCubes(UUID user, BigInteger cubes) {
		checkAndInsert(user);
		DATABASE.update(user.toString(), new JsonDocument().append("cubes", cubes));
	}
}