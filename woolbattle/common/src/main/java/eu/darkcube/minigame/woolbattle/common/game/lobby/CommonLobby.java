/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import eu.darkcube.minigame.woolbattle.api.game.lobby.Lobby;
import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.vote.Vote;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.CommonPhase;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.game.lobby.inventory.LobbyInventories;
import eu.darkcube.minigame.woolbattle.common.game.lobby.inventory.LobbyUserInventory;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyBreakBlockListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyInventoryListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyItemListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyPlaceBlockListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserChatListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserDropItemListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserJoinGameListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserLoginGameListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserMoveListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserParticlesUpdateListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserQuitGameListener;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.minigame.woolbattle.common.vote.CommonPoll;
import eu.darkcube.minigame.woolbattle.common.vote.CommonVoteRegistry;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.util.GameState;

public abstract class CommonLobby extends CommonPhase implements Lobby {
    protected int minPlayerCount;
    protected int maxPlayerCount;
    protected int deathLine;
    protected boolean enoughTeams;
    protected int lifes;
    protected int forcedLifes = -1;
    protected CommonWorld world;
    protected Location spawn;
    protected CommonVoteRegistry voteRegistry;
    protected CommonPoll<Map> mapPoll;
    protected CommonPoll<Boolean> epGlitchPoll;
    protected CommonPoll<Integer> lifesPoll;
    protected CommonLobbyTimer timer;
    protected InventoryTemplate teamsInventoryTemplate;
    protected InventoryTemplate perksInventoryTemplate;
    protected InventoryTemplate votingInventoryTemplate;
    protected InventoryTemplate votingEpGlitchInventoryTemplate;
    protected InventoryTemplate votingMapsInventoryTemplate;
    protected InventoryTemplate votingLifesInventoryTemplate;
    protected InventoryTemplate[][] perkTemplates;

    public CommonLobby(@NotNull CommonGame game) {
        super(game, GameState.LOBBY);

        this.voteRegistry = new CommonVoteRegistry();
        this.mapPoll = voteRegistry.<Map>pollBuilder().addPossibilities(game.api().mapManager().maps(game.mapSize()).stream().filter(Map::enabled).toList()).addToRegistry();
        this.mapPoll.onVote((user, vote) -> user.sendMessage(Messages.VOTED_FOR_MAP, vote.name()));
        this.mapPoll.onUpdate(_ -> {
            var winner = this.mapPoll.sortedWinners().getFirst();
            game.map(winner);
            updateSidebar(LobbySidebarTeam.MAP);
        });
        this.epGlitchPoll = voteRegistry.<Boolean>pollBuilder().addPossibilities(Boolean.FALSE, Boolean.TRUE).addToRegistry();
        this.epGlitchPoll.onVote((user, vote) -> user.sendMessage(vote ? Messages.VOTED_FOR_EP_GLITCH : Messages.VOTED_AGAINST_EP_GLITCH));
        this.epGlitchPoll.onUpdate(_ -> updateSidebar(LobbySidebarTeam.EP_GLITCH));
        this.lifesPoll = voteRegistry.<Integer>pollBuilder().addPossibilities(IntStream.range(3, 31).boxed().toArray(Integer[]::new)).addToRegistry();
        this.lifesPoll.onVote((user, vote) -> user.sendMessage(Messages.VOTED_LIFES, vote));
        this.lifesPoll.onUpdate(_ -> {
            computeLifes();
            updateSidebar(LobbySidebarTeam.LIFES);
        });

        var lobbyInventories = new LobbyInventories(this, game);
        this.teamsInventoryTemplate = lobbyInventories.createTeamsTemplate();
        this.votingInventoryTemplate = lobbyInventories.createVotingInventoryTemplate();
        this.perksInventoryTemplate = lobbyInventories.createPerksInventoryTemplate();
        this.votingEpGlitchInventoryTemplate = lobbyInventories.createVotingEpGlitchInventoryTemplate();
        this.votingMapsInventoryTemplate = lobbyInventories.createVotingMapsInventoryTemplate();
        this.votingLifesInventoryTemplate = lobbyInventories.createVotingLifesInventoryTemplate();
        this.perkTemplates = this.createPerkTemplates(lobbyInventories);
        this.timer = new CommonLobbyTimer(this);
        this.maxPlayerCount = game.mapSize().teams() * game.mapSize().teamSize();
        this.enoughTeams = game.teamManager().playingTeams().size() >= 2;

        this.listeners.addListener(new LobbyBreakBlockListener().create());
        this.listeners.addListener(new LobbyPlaceBlockListener().create());
        this.listeners.addListener(new LobbyUserJoinGameListener(this).create());
        this.listeners.addListener(new LobbyUserParticlesUpdateListener().create());
        this.listeners.addListener(new LobbyUserLoginGameListener(this).create());
        this.listeners.addListener(new LobbyUserDropItemListener().create());
        this.listeners.addListener(new LobbyUserChatListener(woolbattle).create());
        this.listeners.addListener(new LobbyUserMoveListener(this).create());
        this.listeners.addChild(new LobbyUserQuitGameListener(this).node());
        this.listeners.addChild(new LobbyItemListener(this).node());
        this.listeners.addChild(new LobbyInventoryListener(this).node());

        this.computeLifes();
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
    public void init(@Nullable CommonPhase oldPhase) {
        super.init(oldPhase);
        if (!enoughTeams) {
            woolbattle.logger().error("Not enough teams for {}", game.mapSize());
        }
    }

    @Override
    public void enable(@Nullable CommonPhase oldPhase) {
        setupWorld();
        spawn = new Location(world, game.lobbyData().spawn());
        deathLine = game.lobbyData().deathLine();
        minPlayerCount = game.lobbyData().minPlayerCount();
        super.enable(oldPhase);
        if (enoughTeams) timer.start();
    }

    @Override
    public void disable(@Nullable CommonPhase newPhase) {
        super.disable(newPhase);
        if (enoughTeams) timer.stop();
        for (var user : game.users()) {
            quit(user);
        }
        for (var team : game.teamManager().playingTeams()) {
            team.lifes(lifes);
        }
    }

    @Override
    public void unload(@Nullable CommonPhase newPhase) {
        var ingame = (CommonIngame) newPhase;
        if (ingame != null) {
            transitionPlayers(ingame);
        }
        super.unload(newPhase);
        game.api().worldHandler().unloadWorld(world);
        world = null;
        spawn = null;
    }

    @Override
    @Nullable
    public CommonWorld world() {
        return world;
    }

    protected void transitionPlayers(@NotNull CommonIngame ingame) {
        for (var user : game.users()) {
            var team = user.team();
            if (team == null) throw new IllegalStateException("User team can't be null when game starts");
            var spawn = ingame.mapIngameData().spawn(team.key());
            if (spawn == null) {
                woolbattle.logger().warn("No spawn configured for team {} on {}", team.key(), game.mapSize());
                spawn = new Position.Directed.Simple(0.5, 100, 0.5, 0, 0);
            }
            user.teleport(new Location(Objects.requireNonNull(ingame.world()), spawn));
        }
    }

    private void computeLifes() {
        var lifes = -1;
        var votes = this.lifesPoll.votes();
        if (!votes.isEmpty()) {
            var sum = votes.stream().mapToInt(Vote::vote).sum();
            lifes = Math.round((float) sum / (float) votes.size());
        }
        if (lifes == -1) {
            var extra = 0;
            var playerCount = this.game.users().size();
            if (playerCount >= 3) {
                playerCount = 3;
                extra = ThreadLocalRandom.current().nextInt(4);
            }
            extra += playerCount;
            lifes = 10 + extra;
        }
        if (this.forcedLifes != -1) {
            this.forcedLifes = lifes;
        }
        this.lifes = lifes;
    }

    private void setupWorld() {
        this.world = this.game.api().worldHandler().loadLobbyWorld(game);
    }

    public void preJoin(@NotNull CommonWBUser user) {
        LobbyUserInventory.create(user);
    }

    /**
     * Adds a user to the lobby.
     */
    public void join(@NotNull CommonWBUser user) {
        var inventory = LobbyUserInventory.get(user);
        inventory.setAllItems();
    }

    /**
     * Removes a user from the lobby. The user may be quitting the server, or not.
     */
    public void quit(@NotNull CommonWBUser user) {
        LobbyUserInventory.destroy(user);
    }

    public void updateSidebar(LobbySidebarTeam team) {
        for (var user : game.users()) {
            updateSidebar(user, team);
        }
    }

    public void updateTimer() {
        for (var user : game.users()) {
            updateTimer(user);
        }
    }

    @Override
    public @NotNull Location spawn() {
        return spawn;
    }

    public CommonLobbyTimer timer() {
        return timer;
    }

    @Override
    public void forceLifes(int lifes) {
        this.forcedLifes = lifes;
        this.computeLifes();
    }

    @Override
    public int lifes() {
        return lifes;
    }

    protected void updateTimer(@NotNull CommonWBUser user) {
        var millis = this.timer.timer().toMillis();
        var maxMillis = this.timer.maxTimer().toMillis();
        var xp = Math.min((double) millis / (double) maxMillis, 0.999999);
        user.platformAccess().xp((float) xp);
        updateSidebar(user, LobbySidebarTeam.TIME);
    }

    protected abstract void updateSidebar(@NotNull CommonWBUser user, LobbySidebarTeam team);

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

    public InventoryTemplate votingEpGlitchInventoryTemplate() {
        return votingEpGlitchInventoryTemplate;
    }

    public InventoryTemplate votingMapsInventoryTemplate() {
        return votingMapsInventoryTemplate;
    }

    public InventoryTemplate votingLifesInventoryTemplate() {
        return votingLifesInventoryTemplate;
    }

    public CommonPoll<Map> mapPoll() {
        return mapPoll;
    }

    public CommonPoll<Boolean> epGlitchPoll() {
        return epGlitchPoll;
    }

    public CommonPoll<Integer> lifesPoll() {
        return lifesPoll;
    }

    public int minPlayerCount() {
        return minPlayerCount;
    }

    public int maxPlayerCount() {
        return maxPlayerCount;
    }
}
