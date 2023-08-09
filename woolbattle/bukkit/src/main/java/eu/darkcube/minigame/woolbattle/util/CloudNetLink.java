/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.util;

import de.dytanic.cloudnet.ext.bridge.server.BridgeServerHelper;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.DarkCubeBukkit;
import eu.darkcube.system.util.GameState;

public class CloudNetLink {

    public static boolean shouldDisplay = true;
    private static boolean fullyLoaded = false;
    private static boolean isCloudnet;

    static {
        try {
            Wrapper.getInstance().getServiceId();
            CloudNetLink.isCloudnet = true;
            DarkCubeBukkit.autoConfigure(false);
            new Scheduler(() -> {
                fullyLoaded = true;
                update();
                System.out.println("Fully loaded!");
            }).runTask();
        } catch (Exception ex) {
            CloudNetLink.isCloudnet = false;
        }
    }

    public static void update() {
        try {
            if (CloudNetLink.isCloudnet && CloudNetLink.shouldDisplay && fullyLoaded) {
                GameState current = GameState.UNKNOWN;
                if (WoolBattleBukkit.instance().lobby().enabled()) {
                    current = GameState.LOBBY;
                } else if (WoolBattleBukkit.instance().ingame().enabled()) {
                    current = GameState.INGAME;
                } else if (WoolBattleBukkit.instance().endgame().enabled()) {
                    current = GameState.STOPPING;
                }
                DarkCubeBukkit.gameState(current);
                DarkCubeBukkit.playingPlayers()
                        .set(WoolBattleBukkit.instance().lobby().enabled()
                                ? WBUser.onlineUsers().size()
                                : (int) WBUser.onlineUsers().stream()
                                .filter(u -> u.getTeam().canPlay()).count());
                DarkCubeBukkit.maxPlayingPlayers().set(WoolBattleBukkit.instance().maxPlayers());
                BridgeServerHelper.setMaxPlayers(1000);
                String mapname = WoolBattleBukkit.instance().gameData().map() == null
                        ? "Unknown Map"
                        : WoolBattleBukkit.instance().gameData().map().getName();
                DarkCubeBukkit.displayName("§d" + mapname + " §7(" + Wrapper.getInstance()
                        .getCurrentServiceInfoSnapshot().getServiceId().getTaskName()
                        .substring("woolbattle".length()) + ")");
                Wrapper.getInstance().publishServiceInfoUpdate();
            }
        } catch (Exception ignored) {
        }
    }

}
