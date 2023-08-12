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
    private static WoolBattleBukkit woolbattle;

    public static void init(WoolBattleBukkit woolbattle) {
        CloudNetLink.woolbattle = woolbattle;
        try {
            Wrapper.getInstance().getServiceId();
            CloudNetLink.isCloudnet = true;
            DarkCubeBukkit.autoConfigure(false);
            new Scheduler(woolbattle, () -> {
                fullyLoaded = true;
                update();
            }).runTask();
        } catch (Exception ex) {
            CloudNetLink.isCloudnet = false;
        }
    }

    public static void update() {
        try {
            if (CloudNetLink.isCloudnet && CloudNetLink.shouldDisplay && fullyLoaded) {
                GameState current = GameState.UNKNOWN;
                if (woolbattle.lobby().enabled()) {
                    current = GameState.LOBBY;
                } else if (woolbattle.ingame().enabled()) {
                    current = GameState.INGAME;
                } else if (woolbattle.endgame().enabled()) {
                    current = GameState.STOPPING;
                }
                DarkCubeBukkit.gameState(current);
                DarkCubeBukkit
                        .playingPlayers()
                        .set(woolbattle.lobby().enabled() ? WBUser.onlineUsers().size() : (int) WBUser
                                .onlineUsers()
                                .stream()
                                .filter(u -> u.getTeam().canPlay())
                                .count());
                DarkCubeBukkit.maxPlayingPlayers().set(woolbattle.maxPlayers());
                BridgeServerHelper.setMaxPlayers(1000);
                String mapname = woolbattle.gameData().map() == null ? "Unknown Map" : woolbattle.gameData().map().getName();
                DarkCubeBukkit.displayName("§d" + mapname + " §7(" + Wrapper
                        .getInstance()
                        .getCurrentServiceInfoSnapshot()
                        .getServiceId()
                        .getTaskName()
                        .substring("woolbattle".length()) + ")");
                Wrapper.getInstance().publishServiceInfoUpdate();
            }
        } catch (Exception ignored) {
        }
    }

}
