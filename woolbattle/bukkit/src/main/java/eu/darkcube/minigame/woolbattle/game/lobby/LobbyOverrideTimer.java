/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.game.lobby;

import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableObject;
import eu.darkcube.minigame.woolbattle.util.observable.SimpleObservableInteger;

public class LobbyOverrideTimer extends SimpleObservableInteger {
    private final ObservableInteger timer;

    public LobbyOverrideTimer(ObservableInteger timer) {
        this.timer = timer;
    }

    @Override
    public void onChange(ObservableObject<Integer> instance, Integer oldValue,
                         Integer newValue) {
        timer.setObject(newValue);
    }

    @Override
    public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue,
                               Integer newValue) {
    }
}
