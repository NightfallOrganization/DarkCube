package eu.darkcube.minigame.woolbattle.common.game.lobby.inventory;

import java.util.function.Function;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.user.UserPlatformAccess;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

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
        user.metadata().remove(KEY.apply(user.woolbattle()));
    }

    public void setAllItems() {
        setPerksItem();
        setTeamsItem();
        setParticlesItem();
        setSettingsItem();
        setVotingItem();
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
