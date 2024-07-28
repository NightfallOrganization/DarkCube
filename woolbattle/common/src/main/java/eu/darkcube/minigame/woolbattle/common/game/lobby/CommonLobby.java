/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby;

import eu.darkcube.minigame.woolbattle.api.game.lobby.Lobby;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.CommonPhase;
import eu.darkcube.minigame.woolbattle.common.game.lobby.inventory.LobbyInventories;
import eu.darkcube.minigame.woolbattle.common.game.lobby.inventory.LobbyUserInventory;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyBreakBlockListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyInventoryListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyItemListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyPlaceBlockListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserDropItemListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserJoinGameListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserLoginGameListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserParticlesUpdateListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserQuitGameListener;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.util.GameState;

public class CommonLobby extends CommonPhase implements Lobby {
    protected CommonWorld world;
    protected Location spawn;
    protected InventoryTemplate teamsInventoryTemplate;
    protected InventoryTemplate perksInventoryTemplate;
    protected InventoryTemplate votingInventoryTemplate;
    protected InventoryTemplate[][] perkTemplates;

    public CommonLobby(@NotNull CommonGame game) {
        super(game, GameState.LOBBY);
        var lobbyInventories = new LobbyInventories(this, game);
        this.teamsInventoryTemplate = lobbyInventories.createTeamsTemplate();
        this.votingInventoryTemplate = lobbyInventories.createVotingInventoryTemplate();
        this.perksInventoryTemplate = lobbyInventories.createPerksInventoryTemplate();
        this.perkTemplates = createPerkTemplates(lobbyInventories);

        this.listeners.addListener(new LobbyBreakBlockListener().create());
        this.listeners.addListener(new LobbyPlaceBlockListener().create());
        this.listeners.addListener(new LobbyUserJoinGameListener().create());
        this.listeners.addListener(new LobbyUserParticlesUpdateListener().create());
        this.listeners.addListener(new LobbyUserLoginGameListener(this).create());
        this.listeners.addListener(new LobbyUserQuitGameListener().create());
        this.listeners.addListener(new LobbyUserDropItemListener().create());
        this.listeners.addChild(new LobbyItemListener(this).node());
        this.listeners.addChild(new LobbyInventoryListener(this).node());
    }

    private InventoryTemplate[][] createPerkTemplates(LobbyInventories lobbyInventories) {
        var perkTemplates = new InventoryTemplate[ActivationType.values().length][];
        for (var ordinal = 0; ordinal < perkTemplates.length; ordinal++) {
            var type = ActivationType.values()[ordinal];
            perkTemplates[ordinal] = new InventoryTemplate[type.maxCount()];
            for (var number = 0; number < type.maxCount(); number++) {
                perkTemplates[ordinal][number] = lobbyInventories.createPerkInventoryTemplate(type, number);
            }
        }
        return perkTemplates;
    }

    @Override
    public void enable() {
        setupWorld();
        super.enable();
    }

    @Override
    public void disable() {
        super.disable();
        for (var user : game.users()) {
            LobbyUserInventory.destroy(user);
        }
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

    public InventoryTemplate perkInventorytemplate(ActivationType type, int number) {
        return perkTemplates[type.ordinal()][number];
    }

    public InventoryTemplate votingInventoryTemplate() {
        return votingInventoryTemplate;
    }

    public InventoryTemplate perksInventoryTemplate() {
        return perksInventoryTemplate;
    }

    public InventoryTemplate teamsInventoryTemplate() {
        return teamsInventoryTemplate;
    }

    public eu.darkcube.system.pserver.plugin.inventory.DefaultPServerAsyncPagedInventory votingEpGlitchInventoryTemplate() {
    }
}
