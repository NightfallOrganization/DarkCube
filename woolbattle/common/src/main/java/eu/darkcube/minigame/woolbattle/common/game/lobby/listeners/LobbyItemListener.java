/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.item.UserClickItemEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.minigame.woolbattle.common.util.item.ItemManager;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class LobbyItemListener extends ConfiguredListener<UserClickItemEvent> {
    private final CommonLobby lobby;

    public LobbyItemListener(CommonLobby lobby) {
        super(UserClickItemEvent.class);
        this.lobby = lobby;
    }

    @Override
    public void accept(UserClickItemEvent event) {
        var itemId = ItemManager.instance().getItemId(event.item());
        if (itemId == null) return;
        var user = event.user();
        if (itemId.equals(Items.LOBBY_VOTING_MAPS.itemId())) {

        } else if (itemId.equals(Items.LOBBY_VOTING_LIFES.itemId())) {

        } else if (itemId.equals(Items.LOBBY_VOTING_EP_GLITCH.itemId())) {

        } else if (itemId.equals(Items.LOBBY_TEAMS.itemId())) {
            lobby.teamsInventoryTemplate().open(user.user());
        } else if (itemId.equals(Items.LOBBY_PARTICLES_OFF.itemId())) {
            user.particles(true);
            System.out.println("Particles on");
        } else if (itemId.equals(Items.LOBBY_PARTICLES_ON.itemId())) {
            user.particles(false);
            System.out.println("Particles off");
        }
    }
}
