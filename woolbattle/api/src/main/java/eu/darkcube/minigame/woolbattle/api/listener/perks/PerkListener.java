/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.listener.perks;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.listener.Listener;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.system.event.EventNode;

public abstract class PerkListener implements Listener {
    protected final Perk perk;
    protected final Game game;
    protected final EventNode<Object> listeners;

    public PerkListener(Game game, Perk perk) {
        this.perk = perk;
        this.game = game;
        this.listeners = EventNode.all(getClass().getSimpleName());
    }

    @Override
    public void register() {
        game.eventManager().addChild(listeners);
    }

    @Override
    public void unregister() {
        game.eventManager().removeChild(listeners);
    }

    public Perk perk() {
        return perk;
    }
}
