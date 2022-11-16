package eu.darkcube.minigame.woolbattle.util;

import com.google.gson.JsonObject;

import de.dytanic.cloudnet.ext.bridge.BridgeHelper;
import de.dytanic.cloudnet.ext.bridge.server.BridgeServerHelper;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.GameState;

public class CloudNetLink {

	private static boolean isCloudnet;

	public static boolean shouldDisplay = true;

	static {
		try {
			Class.forName("de.dytanic.cloudnet.wrapper.Wrapper");
			CloudNetLink.isCloudnet = true;
		} catch (Exception ex) {
			CloudNetLink.isCloudnet = false;
		}
	}

	public static void update() {
		try {
			if (CloudNetLink.isCloudnet && CloudNetLink.shouldDisplay) {
				GameState current = GameState.UNKNOWN;
				if (Main.getInstance().getLobby().isEnabled()) {
					current = GameState.LOBBY;
				} else if (Main.getInstance().getIngame().isEnabled()) {
					current = GameState.INGAME;
				} else if (Main.getInstance().getEndgame().isEnabled()) {
					current = GameState.STOPPING;
				}
				BridgeServerHelper.setState(current.name());
				JsonObject json = new JsonObject();
				json.addProperty("online",
						Main.getInstance().getLobby().isEnabled()
								? Main.getInstance().getUserWrapper().getUsers().size()
								: Main.getInstance()
										.getUserWrapper()
										.getUsers()
										.stream()
										.filter(u -> u.getTeam().getType() != TeamType.SPECTATOR)
										.count());
				json.addProperty("max", Main.getInstance().getMaxPlayers());
				BridgeServerHelper.setExtra(json.toString());
				BridgeServerHelper.setMaxPlayers(1000);
				String mapname = Main.getInstance().getMap() == null ? "Unknown Map"
						: Main.getInstance().getMap().getName();
				BridgeServerHelper.setMotd("§a" + mapname + " §7("
						+ Wrapper.getInstance()
								.getCurrentServiceInfoSnapshot()
								.getServiceId()
								.getTaskName()
								.substring("woolbattle".length())
						+ ")");
				BridgeHelper.updateServiceInfo();
			}
		} catch (Exception ex) {
		}
	}

}