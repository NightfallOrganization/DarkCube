/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby;

import static eu.darkcube.system.libs.net.kyori.adventure.key.Key.key;
import static eu.darkcube.system.server.inventory.Inventory.createChestTemplate;

import eu.darkcube.minigame.woolbattle.api.game.lobby.Lobby;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.CommonPhase;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyBreakBlockListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyItemListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyPlaceBlockListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserDropItemListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserJoinGameListener;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.util.GameState;

public class CommonLobby extends CommonPhase implements Lobby {
    protected CommonWorld world;
    protected Location spawn;
    protected InventoryTemplate teamsInventoryTemplate;

    public CommonLobby(@NotNull CommonGame game) {
        super(game, GameState.LOBBY);
        this.listeners.addListener(new LobbyBreakBlockListener().create());
        this.listeners.addListener(new LobbyPlaceBlockListener().create());
        this.listeners.addListener(new LobbyUserJoinGameListener(this).create());
        this.listeners.addListener(new LobbyUserDropItemListener().create());
        this.listeners.addListener(new LobbyItemListener(this).create());

        this.teamsInventoryTemplate = createChestTemplate(key("woolbattle", "lobby_select_team"), 5 * 9);
        this.teamsInventoryTemplate.setItems(0, woolbattle.defaultInventoryTemplate());
        this.teamsInventoryTemplate.title(Messages.INVENTORY_TEAMS);
        this.teamsInventoryTemplate.setItem(1, 4, Items.LOBBY_TEAMS);
        this.teamsInventoryTemplate.setItem(-1, 2, Items.LOBBY_TEAMS);
        // setDelayed(teamsInventoryTemplate, Items.LOBBY_TEAMS, 1, 1000, 6);
        // setDelayed(teamsInventoryTemplate, Items.ARMOR_LEATHER_BOOTS, 2, 2000, 6);
        // setDelayed(teamsInventoryTemplate, Items.PERK_BOOSTER, 1, 1000, 23);
        // setDelayed(teamsInventoryTemplate, Items.ARMOR_LEATHER_CHESTPLATE, 2, 2000, 23);
        this.teamsInventoryTemplate.animation().calculateManifold(22, 1);
        var pagination = this.teamsInventoryTemplate.pagination();
        pagination.pageSlots(new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34});
        pagination.content().addStaticItem(Items.LOBBY_VOTING_EP_GLITCH);
        pagination.content().addStaticItem(Items.LOBBY_VOTING_EP_GLITCH);
        pagination.content().addStaticItem(user -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Items.LOBBY_VOTING_MAPS;
        }).makeAsync();
        for (var i = 0; i < 300; i++) {
            var item = Items.values()[i % Items.values().length];
            var fi = i;
            pagination.content().addStaticItem(user -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return item.createItem(user).lore(Component.text(fi));
            }).makeAsync();
        }
        pagination.previousButton().setItem(Items.PREV_PAGE);
        pagination.previousButton().slots(new int[]{9, 18, 27});
        pagination.nextButton().setItem(Items.NEXT_PAGE);
        pagination.nextButton().slots(new int[]{17, 26, 35});
    }

    private void setDelayed(InventoryTemplate template, Items item, int priority, int delay, int slot) {
        template.setItem(priority, slot, user -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return item.createItem(user);
        }).makeAsync();
    }

    @Override
    public void enable() {
        setupWorld();
        super.enable();
    }

    @Override
    public void disable() {
        super.disable();
        game.woolbattle().worldHandler().unloadWorld(world);
        world = null;
        spawn = null;
    }

    public Location spawn() {
        return spawn;
    }

    private void setupWorld() {
        world = game.woolbattle().worldHandler().loadLobbyWorld(game);
        var spawnPosition = game.lobbyData().spawn();
        spawn = new Location(world, spawnPosition);
    }

    public InventoryTemplate teamsInventoryTemplate() {
        return teamsInventoryTemplate;
    }
}
