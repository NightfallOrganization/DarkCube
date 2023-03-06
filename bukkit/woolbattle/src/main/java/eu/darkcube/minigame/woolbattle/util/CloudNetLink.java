/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.util;

import de.dytanic.cloudnet.ext.bridge.server.BridgeServerHelper;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.DarkCubeBukkit;
import eu.darkcube.system.util.GameState;

public class CloudNetLink {

	public static boolean shouldDisplay = true;
	private static boolean isCloudnet;

	static {
		try {
			Wrapper.getInstance().getServiceId();
			CloudNetLink.isCloudnet = true;
			DarkCubeBukkit.autoConfigure(false);
		} catch (Exception ex) {
			CloudNetLink.isCloudnet = false;
		}
	}

	public static void update() {
		try {
			if (CloudNetLink.isCloudnet && CloudNetLink.shouldDisplay) {
				GameState current = GameState.UNKNOWN;
				if (WoolBattle.getInstance().getLobby().enabled()) {
					current = GameState.LOBBY;
				} else if (WoolBattle.getInstance().getIngame().enabled()) {
					current = GameState.INGAME;
				} else if (WoolBattle.getInstance().getEndgame().enabled()) {
					current = GameState.STOPPING;
				}
				DarkCubeBukkit.gameState(current);
				DarkCubeBukkit.playingPlayers()
						.set(WoolBattle.getInstance().getLobby().enabled()
								? WBUser.onlineUsers().size()
								: (int) WBUser.onlineUsers().stream()
										.filter(u -> u.getTeam().canPlay()).count());
				DarkCubeBukkit.maxPlayingPlayers().set(WoolBattle.getInstance().getMaxPlayers());
				BridgeServerHelper.setMaxPlayers(1000);
				String mapname = WoolBattle.getInstance().getMap() == null
						? "Unknown Map"
						: WoolBattle.getInstance().getMap().getName();
				DarkCubeBukkit.displayName("§d" + mapname + " §7(" + Wrapper.getInstance()
						.getCurrentServiceInfoSnapshot().getServiceId().getTaskName()
						.substring("woolbattle".length()) + ")");
				Wrapper.getInstance().publishServiceInfoUpdate();
			}
		} catch (Exception ignored) {
		}
	}

}
