/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.game.lobby;

import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Bukkit;

public class LobbyTimerTask extends Scheduler implements Scheduler.ConfiguredScheduler {
    private final Lobby lobby;
    private final ObservableInteger overrideTimer;
    private boolean announced = false;

    public LobbyTimerTask(Lobby lobby, ObservableInteger overrideTimer) {
        this.lobby = lobby;
        this.overrideTimer = overrideTimer;
    }

    @Override
    public void run() {
        if (lobby.maxPlayerCount() == 0 && !this.announced) {
            this.announced = true;
            lobby.woolbattle().sendConsole("It does not seem that any teams have been set up");
        } else if (lobby.maxPlayerCount() != 0) {
            final int online = Bukkit.getOnlinePlayers().size();
            if (online >= lobby.minPlayerCount()) {
                if (overrideTimer.getObject() != 0) {
                    overrideTimer.setObject(overrideTimer.getObject() - 1);
                } else {
                    if (online == lobby.maxPlayerCount() && lobby.getTimer() > 200) {
                        lobby.setTimer(200);
                    }
                    lobby.setTimer(lobby.getTimer() - 1);
                }
            } else if (lobby.getTimer() != lobby.maxTimerSeconds() * 20) {
                overrideTimer.setSilent(0);
                lobby.setTimer(lobby.maxTimerSeconds() * 20);
            }
        }
    }

    @Override
    public void start() {
        runTaskTimer(1);
    }

    @Override
    public void stop() {
        cancel();
    }
}
