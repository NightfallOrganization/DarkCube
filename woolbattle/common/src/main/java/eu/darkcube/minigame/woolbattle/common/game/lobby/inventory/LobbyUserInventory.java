/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby.inventory;

import java.util.function.Function;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.user.UserPlatformAccess;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.item.ItemBuilder;

public class LobbyUserInventory {

    private static final Function<WoolBattleApi, Key> KEY = woolbattle -> Key.key(woolbattle, "lobby_user_inventory");

    private final WBUser user;
    private final UserPlatformAccess access;

    private LobbyUserInventory(WBUser user) {
        this.user = user;
        this.access = ((CommonWBUser) user).platformAccess();
    }

    public static LobbyUserInventory get(WBUser user) {
        return user.metadata().get(KEY.apply(user.woolbattle()));
    }

    public static LobbyUserInventory create(WBUser user) {
        var inventory = new LobbyUserInventory(user);
        user.metadata().set(KEY.apply(user.woolbattle()), inventory);
        return inventory;
    }

    public static void destroy(WBUser user) {
        var inventory = user.metadata().<LobbyUserInventory>remove(KEY.apply(user.woolbattle()));
        inventory.clearAllItems();
    }

    public void setAllItems() {
        setPerksItem();
        setTeamsItem();
        setParticlesItem();
        setSettingsItem();
        setVotingItem();
    }

    public void clearAllItems() {
        access.setItem(0, ItemBuilder.item());
        access.setItem(1, ItemBuilder.item());
        access.setItem(4, ItemBuilder.item());
        access.setItem(7, ItemBuilder.item());
        access.setItem(8, ItemBuilder.item());
    }

    public void setPerksItem() {
        access.setItem(0, Items.LOBBY_PERKS.getItem(user));
    }

    public void setTeamsItem() {
        access.setItem(1, Items.LOBBY_TEAMS.getItem(user));
    }

    public void setParticlesItem() {
        access.setItem(4, (user.particles() ? Items.LOBBY_PARTICLES_ON : Items.LOBBY_PARTICLES_OFF).getItem(user));
    }

    public void setSettingsItem() {
        access.setItem(7, Items.SETTINGS.getItem(user));
    }

    public void setVotingItem() {
        access.setItem(8, Items.LOBBY_VOTING.getItem(user));
    }

}
