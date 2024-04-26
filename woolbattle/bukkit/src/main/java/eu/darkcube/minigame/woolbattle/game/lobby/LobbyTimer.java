/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.game.lobby;

import com.google.common.util.concurrent.AtomicDouble;
import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableObject;
import eu.darkcube.minigame.woolbattle.util.observable.SimpleObservableInteger;
import org.bukkit.Bukkit;

public class LobbyTimer extends SimpleObservableInteger {
    private final Lobby lobby;

    public LobbyTimer(Lobby lobby) {
        this.lobby = lobby;
    }

    @Override public void onChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
        if (lobby.enabled()) {
            if (newValue <= 1) {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    p.setLevel(0);
                    p.setExp(0);
                });
                lobby.disable();
                lobby.woolbattle().ingame().enable();
                return;
            }
            AtomicDouble exp = new AtomicDouble((float) newValue / (lobby.maxTimerSeconds() * 20F));
            if (exp.get() >= 0.9999) exp.set(0.9999);
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.setLevel(newValue / 20);
                p.setExp((float) exp.get());
            });
            WBUser.onlineUsers().forEach(lobby::updateTimer);
        }
    }

    @Override public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
    }
}
