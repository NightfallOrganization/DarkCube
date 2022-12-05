package eu.darkcube.system.lobbysystem.user;

import java.util.UUID;
import org.bukkit.Bukkit;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.event.EventGadgetSelect;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.userapi.data.Key;
import eu.darkcube.system.userapi.data.UserModifier;

public class UserWrapper implements UserModifier {
	public static final Key key = new Key(Lobby.getInstance(), "user");

	public void beginMigration() {
		if (CloudNetDriver.getInstance().getDatabaseProvider()
				.containsDatabase("lobbysystem_userdata")) {
			Lobby.getInstance().getLogger().info("Starting migration of lobbysystem_userdata");
			Database db = CloudNetDriver.getInstance().getDatabaseProvider()
					.getDatabase("lobbysystem_userdata");
			for (String key : db.keys()) {
				long time1 = System.currentTimeMillis();
				UUID uuid = UUID.fromString(key);
				User user = UserAPI.getInstance().getUser(uuid);
				Lobby.getInstance().getLogger()
						.info("Migrating lobbydata: " + user.getName() + "(" + uuid + ")");
				long time2 = System.currentTimeMillis();
				UserData data = new UserData(uuid, db.get(key));
				LobbyUser u = fromUser(user);
				u.setGadget(data.getGadget());
				u.setSounds(data.isSounds());
				u.setAnimations(data.isAnimations());
				u.setLastDailyReward(data.getLastDailyReward());
				u.setRewardSlotsUsed(data.getRewardSlotsUsed());
				long time3 = System.currentTimeMillis();
				UserAPI.getInstance().unloadUser(user, true);
				if (System.currentTimeMillis() - time1 > 100) {
					Lobby.getInstance().getLogger()
							.info("Migration of lobbydata took very long: "
									+ (System.currentTimeMillis() - time1) + " | "
									+ (System.currentTimeMillis() - time2) + " | "
									+ (System.currentTimeMillis() - time3));
				}
			}
			CloudNetDriver.getInstance().getDatabaseProvider()
					.deleteDatabase("lobbysystem_userdata");
		}
	}

	@Override
	public void onLoad(User user) {
		user.getMetaDataStorage().set(key, new LobbyUser(user));
		Bukkit.getPluginManager()
				.callEvent(new EventGadgetSelect(fromUser(user), fromUser(user).getGadget()));
	}

	@Override
	public void onUnload(User user) {
		user.getMetaDataStorage().remove(key);
	}

	public static LobbyUser fromUser(User user) {
		return user.getMetaDataStorage().get(key);
	}
}
