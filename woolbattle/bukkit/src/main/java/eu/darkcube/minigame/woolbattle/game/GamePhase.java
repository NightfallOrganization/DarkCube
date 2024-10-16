/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.game;

import java.util.ArrayList;
import java.util.Collection;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler.ConfiguredScheduler;
import org.bukkit.event.Listener;

public abstract class GamePhase {

    private final Collection<Listener> listeners = new ArrayList<>();
    private final Collection<ConfiguredScheduler> schedulers = new ArrayList<>();
    private boolean enabled = false;

    protected abstract void onEnable();

    protected abstract void onDisable();

    protected final void addListener(Listener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
        if (enabled()) {
            WoolBattleBukkit.registerListeners(listeners);
        }
    }

    protected final void addScheduler(ConfiguredScheduler... schedulers) {
        this.schedulers.addAll(Arrays.asList(schedulers));
        if (enabled()) {
            for (var scheduler : schedulers) {
                scheduler.start();
            }
        }
    }

    public boolean enabled() {
        return this.enabled;
    }

    public void enable() {
        if (!this.enabled) {
            this.enabled = true;
            this.onEnable();
            listeners.forEach(WoolBattleBukkit::registerListeners);
            schedulers.forEach(ConfiguredScheduler::start);
        }
    }

    public void disable() {
        if (this.enabled) {
            this.enabled = false;
            listeners.forEach(WoolBattleBukkit::unregisterListeners);
            schedulers.forEach(ConfiguredScheduler::stop);
            this.onDisable();
        }
    }
}
