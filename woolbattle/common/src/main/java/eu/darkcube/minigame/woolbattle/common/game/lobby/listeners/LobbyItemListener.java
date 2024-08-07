/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.item.UserClickItemEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserInteractEvent;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListeners;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.minigame.woolbattle.common.util.item.ItemManager;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public class LobbyItemListener implements ConfiguredListeners {
    private final CommonLobby lobby;
    private final EventNode<Object> node;

    public LobbyItemListener(CommonLobby lobby) {
        this.lobby = lobby;
        this.node = EventNode.all("lobby-items");
        this.node.addListener(UserClickItemEvent.class, this::handle);
        this.node.addListener(UserInteractEvent.class, this::handle);
    }

    private void handle(WBUser user, ItemBuilder item) {
        var itemId = ItemManager.instance().getItemId(item);
        if (itemId == null) return;
        if (itemId.equals(Items.LOBBY_PERKS.itemId())) {
            lobby.perksInventoryTemplate().open(user.user());
        } else if (itemId.equals(Items.LOBBY_VOTING.itemId())) {
            lobby.votingInventoryTemplate().open(user.user());
        } else if (itemId.equals(Items.LOBBY_TEAMS.itemId())) {
            lobby.teamsInventoryTemplate().open(user.user());
        } else if (itemId.equals(Items.LOBBY_PARTICLES_OFF.itemId())) {
            user.particles(true);
        } else if (itemId.equals(Items.LOBBY_PARTICLES_ON.itemId())) {
            user.particles(false);
        } else if (itemId.equals(Items.SETTINGS.itemId())) {
            lobby.game().woolbattle().woolbattle().settingsInventoryTemplate().open(user.user());
        }
    }

    private void handle(UserClickItemEvent event) {
        handle(event.user(), event.item());
    }

    private void handle(UserInteractEvent event) {
        if (event.action() == UserInteractEvent.Action.RIGHT_CLICK_AIR || event.action() == UserInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            handle(event.user(), event.item());
        }
    }

    @Override
    public @NotNull EventNode<?> node() {
        return node;
    }
}
