package eu.darkcube.minigame.woolbattle.common.game.lobby.inventory;

import static eu.darkcube.system.libs.net.kyori.adventure.key.Key.key;
import static eu.darkcube.system.server.inventory.Inventory.createChestTemplate;

import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattle;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
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
    private final CommonLobby lobby;
    private final CommonGame game;
    private final Key teamKey;

    public LobbyInventories(CommonLobby lobby, CommonGame game) {
        this.lobby = lobby;
        this.woolbattle = game.woolbattle().woolbattle();
        this.game = game;
        this.teamKey = Key.key(woolbattle, "team");
    }

    public InventoryTemplate createTeamsTemplate() {
        var template = createChestTemplate(key(woolbattle, "lobby_select_team"), 5 * 9);
        this.woolbattle.configureDefaultSinglePagedInventory(template);
        template.title(Messages.INVENTORY_TEAMS);
        template.setItem(10, 4, Items.LOBBY_TEAMS.withoutId());
        var pagination = template.pagination();
        for (var team : game.teamManager().teams().stream().sorted(Comparator.comparing(CommonTeam::key)).toList()) {
            if (team.spectator()) continue;
            pagination.content().addStaticItem(user -> {
                var wbUser = game.woolbattle().user(user);
                if (wbUser == null) return ItemBuilder.item();
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

    public InventoryTemplate createVotingInventoryTemplate() {
        var template = createChestTemplate(key(woolbattle, "lobby_voting"), 5 * 9);
        this.woolbattle.configureDefaultSinglePagedInventory(template);
        template.title(Messages.INVENTORY_VOTING);
        template.setItem(10, 4, Items.LOBBY_VOTING.withoutId());
        template.setItem(10, 20, Items.LOBBY_VOTING_EP_GLITCH);
        template.setItem(10, 22, Items.LOBBY_VOTING_MAPS);
        template.setItem(10, 24, Items.LOBBY_VOTING_LIFES);
        template.addListener(new InventoryListener() {
            @Override
            public void onClick(@NotNull Inventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                var itemId = ItemManager.instance().getItemId(item);
                if (itemId == null) return;
                if (Items.LOBBY_VOTING_EP_GLITCH.itemId().equals(itemId)) {
                    lobby.votingEpGlitchInventoryTemplate().open(user);
                } else if (Items.LOBBY_VOTING_MAPS.itemId().equals(itemId)) {
                    lobby.votingMapsInventoryTemplate().open(user);
                } else if (Items.LOBBY_VOTING_LIFES.itemId().equals(itemId)) {
                    lobby.votingLifesInventoryTemplate().open(user);
                }
            }
        });
        return template;
    }

    public InventoryTemplate createPerksInventoryTemplate() {
        var template = createChestTemplate(key(woolbattle, "lobby_perks_main"), 5 * 9);
        this.woolbattle.configureDefaultSinglePagedInventory(template);
        template.title(Messages.INVENTORY_PERKS);
        template.setItem(10, 4, Items.LOBBY_PERKS.withoutId());
        var content = template.pagination().content();
        var perksType = Key.key(woolbattle, "perks_type");
        var perksTypeNumber = Key.key(woolbattle, "perks_type_number");
        for (var activationType : ActivationType.values()) {
            var perks = game.perkRegistry().perks(activationType);
            for (var i = 0; i < perks.length; i++) {
                if (perks.length > activationType.maxCount()) {
                    var finalI = i;
                    content.addStaticItem(user -> {
                        var builder = activationType.displayItem().getItem(user, finalI);
                        ItemManager.instance().setId(builder, perksType, String.valueOf(activationType.ordinal()));
                        ItemManager.instance().setId(builder, perksTypeNumber, String.valueOf(finalI));
                        return builder;
                    });
                    i++;
                }
            }
        }
        template.addListener(new InventoryListener() {
            @Override
            public void onClick(@NotNull Inventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                var typeId = ItemManager.instance().getId(item, perksType);
                if (typeId == null) return;
                var type = ActivationType.values()[Integer.parseInt(typeId)];
                var number = Integer.parseInt(Objects.requireNonNull(ItemManager.instance().getId(item, perksTypeNumber)));
                lobby.perkInventorytemplate(type, number).open(user);
            }
        });
        return template;
    }

    public InventoryTemplate createPerkInventoryTemplate(ActivationType type, int number) {
        var template = createChestTemplate(key(woolbattle, "perks_" + type.name().toLowerCase(Locale.ROOT) + "." + number), 5 * 9);
        this.woolbattle.configureDefaultSinglePagedInventory(template);
        template.title(Messages.INVENTORY_PERKS);
        var keyPerk = Key.key(woolbattle, "perk_name");
        var content = template.pagination().content();
        var perks = game.perkRegistry().perks(type);
        for (var perk : perks) {
            content.addStaticItem(user -> {
                var wbUser = woolbattle.api().user(user);
                if (wbUser == null) return ItemBuilder.item();
                var builder = perk.defaultItem().getItem(user);
                ItemManager.instance().setId(builder, keyPerk, perk.perkName().name());
                if (wbUser.perks().count(perk.perkName()) > 0) {
                    builder.lore(Messages.SELECTED.getMessage(user));
                }
                if (wbUser.perks().perks(type).get(number).perk().perkName().equals(perk.perkName())) {
                    builder.glow(true);
                }
                return builder;
            });
        }
        return template;
    }
}
