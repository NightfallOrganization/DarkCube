/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game;

import java.util.Locale;

import eu.darkcube.minigame.woolbattle.api.game.GamePhase;
import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.GameState;

public abstract class CommonPhase implements GamePhase {
    protected final @NotNull CommonGame game;
    private final @NotNull GameState gameState;
    protected final @NotNull EventNode<Object> listeners;

    public CommonPhase(@NotNull CommonGame game, @NotNull GameState gameState) {
        this.game = game;
        this.gameState = gameState;
        this.listeners = EventNode.all("phase-" + gameState.name().toLowerCase(Locale.ROOT));
    }

    public void enable() {
        this.game.eventManager().addChild(this.listeners);
    }

    public void disable() {
        this.game.eventManager().removeChild(this.listeners);
    }

    @Override
    public @NotNull CommonGame game() {
        return game;
    }

    @Override
    public @NotNull GameState gameState() {
        return gameState;
    }
}
