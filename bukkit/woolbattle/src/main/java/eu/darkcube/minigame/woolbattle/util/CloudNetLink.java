/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import com.google.gson.JsonObject;

import de.dytanic.cloudnet.ext.bridge.BridgeHelper;
import de.dytanic.cloudnet.ext.bridge.server.BridgeServerHelper;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.util.GameState;

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
				if (WoolBattle.getInstance().getLobby().isEnabled()) {
					current = GameState.LOBBY;
				} else if (WoolBattle.getInstance().getIngame().isEnabled()) {
					current = GameState.INGAME;
				} else if (WoolBattle.getInstance().getEndgame().isEnabled()) {
					current = GameState.STOPPING;
				}
				BridgeServerHelper.setState(current.name());
				JsonObject json = new JsonObject();
				json.addProperty("online",
						WoolBattle.getInstance().getLobby().isEnabled()
								? WoolBattle.getInstance().getUserWrapper().getUsers().size()
								: WoolBattle.getInstance()
										.getUserWrapper()
										.getUsers()
										.stream()
										.filter(u -> u.getTeam().getType() != TeamType.SPECTATOR)
										.count());
				json.addProperty("max", WoolBattle.getInstance().getMaxPlayers());
				BridgeServerHelper.setExtra(json.toString());
				BridgeServerHelper.setMaxPlayers(1000);
				String mapname = WoolBattle.getInstance().getMap() == null ? "Unknown Map"
						: WoolBattle.getInstance().getMap().getName();
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
