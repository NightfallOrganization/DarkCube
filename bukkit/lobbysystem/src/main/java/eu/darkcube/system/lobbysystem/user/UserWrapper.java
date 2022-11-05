package eu.darkcube.system.lobbysystem.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.lobbysystem.inventory.InventoryPlayer;

public class UserWrapper {

	public static Map<UUID, User> users = new HashMap<>();
	private static Database database = CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("lobbysystem_userdata");

	public static void init() {
	}
	
	public static User loadUser(UUID uuid) {
		if (!isLoaded(uuid)) {
			JsonDocument doc = database.get(uuid.toString());
			UserData data = new UserData(uuid, doc);
			if (doc == null) {
				database.insert(uuid.toString(), data.serializeToDocument());
				Language.setLanguage(uuid, data.getLanguage());
			}

			User user = new CachedUser(data.getLanguage(), data.getGadget(),
							data.isSounds(), data.isAnimations(), uuid,
							data.getLastDailyReward(), new InventoryPlayer(),
							data.getRewardSlotsUsed());
			users.put(uuid, user);
			return user;
		}
		return users.get(uuid);
	}

	public static User saveUser(User user) {
		database.update(user.getUniqueId().toString(), user.newUserData().serializeToDocument());
		user.getSlots().save();
		return user;
	}

	public static UUID unloadUser(User user) {
		user.save();
		UUID uuid = user.getUniqueId();
		users.remove(uuid);
		user.unload();
		return uuid;
	}

	public static boolean isLoaded(UUID uuid) {
		return users.containsKey(uuid);
	}

	public static User getUser(UUID uuid) {
		return users.getOrDefault(uuid, loadUser(uuid));
	}
}
