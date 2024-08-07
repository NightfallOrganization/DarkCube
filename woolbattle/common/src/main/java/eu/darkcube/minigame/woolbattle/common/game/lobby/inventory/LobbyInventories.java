package eu.darkcube.minigame.woolbattle.common.game.lobby.inventory;

import static eu.darkcube.system.libs.net.kyori.adventure.key.Key.key;
import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.vote.Poll;
import eu.darkcube.minigame.woolbattle.api.vote.Vote;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattle;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.util.item.ItemManager;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
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
        var template = woolbattle.createDefaultTemplate("lobby_select_team", Messages.INVENTORY_TEAMS, Items.LOBBY_TEAMS);
        var pagination = template.pagination();
        pagination.specialPageSlots(22);
        pagination.specialPageSlots(21, 23);
        pagination.specialPageSlots(21, 22, 23);
        pagination.specialPageSlots(20, 21, 23, 24);
        pagination.specialPageSlots(20, 21, 22, 23, 24);
        pagination.specialPageSlots(19, 20, 21, 23, 24, 25);
        pagination.specialPageSlots(19, 20, 21, 22, 23, 24, 25);
        pagination.specialPageSlots(20, 21, 23, 24, 29, 30, 32, 33);
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
        template.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                var uniqueIdString = ItemManager.instance().getId(item, teamKey);
                if (uniqueIdString == null) return;
                var uniqueId = UUID.fromString(uniqueIdString);
                var team = game.teamManager().team(uniqueId);
                if (team == null) return;
                var wbUser = game.woolbattle().user(user);
                if (wbUser == null) return;
                if (team.users().size() >= game.mapSize().teamSize()) {
                    wbUser.sendMessage(Messages.TEAM_IS_FULL, team.getName(user));
                } else {
                    wbUser.team(team);
                }
            }
        });
        return template;
    }

    public InventoryTemplate createVotingInventoryTemplate() {
        var template = woolbattle.createDefaultTemplate("lobby_voting", Messages.INVENTORY_VOTING, Items.LOBBY_VOTING);
        template.setItem(10, 20, Items.LOBBY_VOTING_EP_GLITCH);
        template.setItem(10, 22, Items.LOBBY_VOTING_MAPS);
        template.setItem(10, 24, Items.LOBBY_VOTING_LIFES);
        template.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
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
        var template = woolbattle.createDefaultTemplate("lobby_perks_main", Messages.INVENTORY_PERKS, Items.LOBBY_PERKS);
        var content = template.pagination().content();
        var perksType = Key.key(woolbattle, "perks_type");
        var perksTypeNumber = Key.key(woolbattle, "perks_type_number");
        for (var activationType : ActivationType.values()) {
            var perks = game.perkRegistry().perks(activationType);
            if (perks.length > activationType.maxCount()) {
                for (var i = 0; i < activationType.maxCount(); i++) {
                    var finalI = i;
                    content.addStaticItem(user -> {
                        var builder = activationType.displayItem().getItem(user, finalI + 1);
                        ItemManager.instance().setId(builder, perksType, String.valueOf(activationType.ordinal()));
                        ItemManager.instance().setId(builder, perksTypeNumber, String.valueOf(finalI));
                        return builder;
                    });
                }
            }
        }
        template.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
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
        var template = woolbattle.createDefaultTemplate("lobby_perks_" + type.name().toLowerCase(Locale.ROOT) + "." + number, Messages.INVENTORY_PERKS, null);
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
                var userPerks = wbUser.perks().perks(type);
                if (userPerks.size() >= number + 1 && userPerks.get(number).perk().perkName().equals(perk.perkName())) {
                    builder.glow(true);
                }
                return builder;
            });
        }
        template.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                var perkNameString = ItemManager.instance().getId(item, keyPerk);
                if (perkNameString == null) return;
                var perkName = new PerkName(perkNameString);
                var wbUser = woolbattle.api().user(user);
                if (wbUser == null) return;
                var perksStorage = wbUser.perksStorage();
                var currentPerks = Arrays.asList(perksStorage.perks(type));
                if (currentPerks.contains(perkName)) return;
                perksStorage.perk(type, number, perkName);
                wbUser.perksStorage(perksStorage);
                wbUser.perks().reloadFromStorage();
                inventory.pagedController().publishUpdateAll();
            }
        });
        return template;
    }

    public InventoryTemplate createVotingEpGlitchInventoryTemplate() {
        var template = woolbattle.createDefaultTemplate("lobby_voting_ep_glitch", Messages.INVENTORY_VOTING_EP_GLITCH, Items.LOBBY_VOTING_EP_GLITCH);
        template.pagination().pageSlots(21, 23);
        template.pagination().content().addStaticItem(Items.GENERAL_VOTING_FOR.modify((user, item) -> {
            var vote = vote(lobby.epGlitchPoll(), user);
            if (vote != null && vote.vote()) item.glow(true);
        }));
        template.pagination().content().addStaticItem(Items.GENERAL_VOTING_AGAINST.modify((user, item) -> {
            var vote = vote(lobby.epGlitchPoll(), user);
            if (vote != null && !vote.vote()) item.glow(true);
        }));
        template.addListener(new TemplateInventoryListener() {
            private Poll.Listener.Updated<Boolean> listener;

            @Override
            public void onOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
                this.listener = u -> {
                    if (u.user() != user) return;
                    inventory.pagedController().publishUpdateAll();
                };
                lobby.epGlitchPoll().addListener(this.listener);
            }

            @Override
            public void onClose(@NotNull TemplateInventory inventory, @NotNull User user) {
                lobby.epGlitchPoll().removeListener(this.listener);
                this.listener = null;
            }

            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                var itemId = ItemManager.instance().getItemId(item);
                if (itemId == null) return;
                if (itemId.equals(Items.GENERAL_VOTING_FOR.itemId())) {
                    vote(lobby.epGlitchPoll(), user, true);
                } else if (itemId.equals(Items.GENERAL_VOTING_AGAINST.itemId())) {
                    vote(lobby.epGlitchPoll(), user, false);
                }
            }
        });
        return template;
    }

    public InventoryTemplate createVotingMapsInventoryTemplate() {
        var template = woolbattle.createDefaultTemplate("lobby_voting_maps", Messages.INVENTORY_VOTING_MAPS, Items.LOBBY_VOTING_MAPS);
        var maps = lobby.mapPoll().possibilities().stream().sorted(Comparator.comparing(Map::name)).toList();
        var keyMap = Key.key(woolbattle, "map");
        var content = template.pagination().content();
        for (var map : maps) {
            content.addStaticItem(user -> {
                var builder = map.icon();
                ItemManager.instance().setId(builder, keyMap, map.name());
                var vote = vote(lobby.mapPoll(), user);
                if (vote != null && vote.vote() == map) {
                    builder.glow(true);
                }
                builder.displayname(text(map.name(), NamedTextColor.GREEN));
                return builder;
            });
        }
        template.addListener(new TemplateInventoryListener() {
            private Poll.Listener.Updated<Map> listener;

            @Override
            public void onOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
                this.listener = u -> {
                    if (user != u.user()) return;
                    inventory.pagedController().publishUpdateAll();
                };
                lobby.mapPoll().addListener(this.listener);
            }

            @Override
            public void onClose(@NotNull TemplateInventory inventory, @NotNull User user) {
                lobby.mapPoll().removeListener(this.listener);
                this.listener = null;
            }

            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                var mapName = ItemManager.instance().getId(item, keyMap);
                if (mapName == null) return;
                var map = game.woolbattle().mapManager().map(mapName, game.mapSize());
                if (map == null) return;
                vote(lobby.mapPoll(), user, map);
            }
        });
        return template;
    }

    public InventoryTemplate createVotingLifesInventoryTemplate() {
        var template = woolbattle.createDefaultTemplate("lobby_voting_lifes", Messages.INVENTORY_VOTING_LIFES, Items.LOBBY_VOTING_LIFES);
        var keyLifes = key(woolbattle, "lifes");
        Function<Integer, Object> generator = lifeCount -> Items.LOBBY_VOTING_LIFES_ENTRY.defaultReplacements(lifeCount).modify((user, item) -> {
            item.setLore(List.of());
            ItemManager.instance().setId(item, keyLifes, String.valueOf(lifeCount));
            var vote = vote(lobby.lifesPoll(), user);
            if (vote != null && vote.vote().equals(lifeCount)) item.glow(true);
        });
        template.pagination().pageSlots(new int[]{20, 21, 23, 24});
        template.pagination().content().addStaticItem(generator.apply(3));
        template.pagination().content().addStaticItem(generator.apply(10));
        template.pagination().content().addStaticItem(generator.apply(20));
        template.pagination().content().addStaticItem(generator.apply(30));
        template.addListener(new TemplateInventoryListener() {
            private Poll.Listener.Updated<Integer> listener = null;

            @Override
            public void onOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
                this.listener = wbUser -> {
                    if (user != wbUser.user()) return;
                    inventory.pagedController().publishUpdateAll();
                };
                lobby.lifesPoll().addListener(listener);
            }

            @Override
            public void onClose(@NotNull TemplateInventory inventory, @NotNull User user) {
                lobby.lifesPoll().removeListener(listener);
                listener = null;
            }

            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                var stringLifes = ItemManager.instance().getId(item, keyLifes);
                if (stringLifes == null) return;
                var lifes = Integer.parseInt(stringLifes);
                vote(lobby.lifesPoll(), user, lifes);
            }
        });
        return template;
    }

    private <T> void vote(Poll<T> poll, User user, T vote) {
        var wbUser = woolbattle.api().user(user);
        if (wbUser == null) return;
        poll.vote(wbUser, vote);
    }

    private <T> @Nullable Vote<T> vote(Poll<T> poll, User user) {
        var wbUser = woolbattle.api().user(user);
        if (wbUser == null) return null;
        return poll.vote(wbUser);
    }
}
