/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.perk;

import eu.darkcube.minigame.woolbattle.api.listener.Listener;

public interface PerkHook {
    void enable();

    void disable();

    record ListenersHook(Listener... listeners) implements PerkHook {
        @Override public void enable() {
            for (var listener : listeners) {
                listener.register();
            }
        }

        @Override public void disable() {
            for (var listener : listeners) {
                listener.unregister();
            }
        }
    }
}
