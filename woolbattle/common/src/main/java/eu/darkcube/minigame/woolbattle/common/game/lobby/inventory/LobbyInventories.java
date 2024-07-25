package eu.darkcube.minigame.woolbattle.common.game.lobby.inventory;

import static eu.darkcube.system.libs.net.kyori.adventure.key.Key.key;
import static eu.darkcube.system.server.inventory.Inventory.createChestTemplate;

import java.util.Comparator;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.common.CommonWoolBattle;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.util.item.ItemManager;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.listener.InventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public class LobbyInventories {
    private final CommonWoolBattle woolbattle;
    private final CommonGame game;
    private final Key teamKey;

    public LobbyInventories(CommonGame game) {
        this.woolbattle = game.woolbattle().woolbattle();
        this.game = game;
        this.teamKey = Key.key(game.woolbattle(), "team");
    }

    public InventoryTemplate createTeamsTemplate() {
        var template = createChestTemplate(key("woolbattle", "lobby_select_team"), 5 * 9);
        this.woolbattle.configureDefaultPagedInventory(template);
        template.title(Messages.INVENTORY_TEAMS);
        template.setItem(10, 4, Items.LOBBY_TEAMS.withoutId());
        var pagination = template.pagination();
        for (var team : game.teamManager().teams().stream().sorted(Comparator.comparing(CommonTeam::key)).toList()) {
            if (team.spectator()) continue;
            pagination.content().addStaticItem(user -> {
                var wbUser = game.woolbattle().user(user);
                var b = team.wool().createSingleItem();
                b.displayname(team.getName(user));
                if (team.users().contains(wbUser)) {
                    b.glow(true);
                }
                for (var teamUser : team.users()) {
                    b.lore(teamUser.teamPlayerName());
                }
                ItemManager.instance().setId(b, teamKey, team.uniqueId().toString());
                return b;
            });
        }
        template.addListener(new InventoryListener() {
            @Override
            public void onClick(@NotNull Inventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                var uniqueIdString = ItemManager.instance().getId(item, teamKey);
                if (uniqueIdString == null) return;
                var uniqueId = UUID.fromString(uniqueIdString);
                var team = game.teamManager().team(uniqueId);
                if (team == null) return;
                var wbUser = game.woolbattle().user(user);
                if (wbUser == null) return;
                wbUser.team(team);
            }
        });
        return template;
    }
}
