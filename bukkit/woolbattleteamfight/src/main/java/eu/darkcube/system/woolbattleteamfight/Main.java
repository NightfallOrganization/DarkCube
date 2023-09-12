/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight;


import eu.darkcube.system.DarkCubePlugin;

public class Main extends DarkCubePlugin {
    private static Main instance;
    private LobbyTimer lobbyTimer;

    public Main() {
        super("woolbattleteamfight");
    }

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(new LobbyScoreboardManager(), this);
        getServer().getPluginManager().registerEvents(new LobbyItemManager(), this);
        getServer().getPluginManager().registerEvents(new LobbyManager(), this);

        lobbyTimer = new LobbyTimer(this);
        lobbyTimer.startTimer();

        instance.getCommand("timer").setExecutor(new TimerCommand(this, lobbyTimer));
        instance.getCommand("start").setExecutor(new StartCommand(this, lobbyTimer));
    }

    public static Main getInstance() {
        return instance;
    }

    public LobbyTimer getLobbyTimer() {
        return lobbyTimer;
    }
}
