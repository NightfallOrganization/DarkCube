/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.user.UserJoinGameEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import net.minestom.server.entity.attribute.Attribute;

public class MinestomLobbyJoinGameListener extends ConfiguredListener<UserJoinGameEvent> {
    private final MinestomWoolBattle woolbattle;

    public MinestomLobbyJoinGameListener(MinestomWoolBattle woolbattle) {
        super(UserJoinGameEvent.class);
        this.woolbattle = woolbattle;
    }

    @Override
    public void accept(UserJoinGameEvent event) {
        var user = (CommonWBUser) event.user();
        var player = woolbattle.player(user);
        var inventory = player.getInventory();
        inventory.setItemStack(0, Items.LOBBY_PERKS.getItem(user).build());
        inventory.setItemStack(1, Items.LOBBY_TEAMS.getItem(user).build());
        inventory.setItemStack(4, (user.particles() ? Items.LOBBY_PARTICLES_ON : Items.LOBBY_PARTICLES_OFF).getItem(user).build());
        inventory.setItemStack(7, Items.SETTINGS.getItem(user).build());
        inventory.setItemStack(8, Items.LOBBY_VOTING.getItem(user).build());
        player.setFoodSaturation(0);

        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1024);
    }
}
