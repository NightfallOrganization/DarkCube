/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame;

import java.io.IOException;
import java.nio.file.Files;

import eu.darkcube.minigame.woolbattle.api.game.ingame.Ingame;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.CommonPhase;
import eu.darkcube.minigame.woolbattle.common.game.ingame.inventory.IngameUserInventory;
import eu.darkcube.minigame.woolbattle.common.game.ingame.listener.IngameBreakBlockListener;
import eu.darkcube.minigame.woolbattle.common.game.ingame.listener.IngameUserChatListener;
import eu.darkcube.minigame.woolbattle.common.game.ingame.listener.IngameUserJoinGameListener;
import eu.darkcube.minigame.woolbattle.common.game.ingame.listener.IngameUserLoginGameListener;
import eu.darkcube.minigame.woolbattle.common.game.ingame.listener.IngameUserQuitGameListener;
import eu.darkcube.minigame.woolbattle.common.game.ingame.scheduler.CommonWoolResetScheduler;
import eu.darkcube.minigame.woolbattle.common.map.CommonMapIngameData;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.schematic.SchematicReader;
import eu.darkcube.minigame.woolbattle.common.world.CommonIngameWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;
import eu.darkcube.system.util.GameState;

public class CommonIngame extends CommonPhase implements Ingame {

    private final CommonWoolResetScheduler woolResetScheduler = new CommonWoolResetScheduler(this);
    private CommonIngameWorld world;
    private CommonMapIngameData mapIngameData;

    public CommonIngame(@NotNull CommonGame game) {
        super(game, GameState.INGAME);

        this.listeners.addListener(new IngameUserChatListener(woolbattle).create());
        this.listeners.addListener(new IngameUserJoinGameListener(this).create());
        this.listeners.addListener(new IngameUserLoginGameListener(this).create());
        this.listeners.addListener(new IngameBreakBlockListener(this).create());
        this.listeners.addChild(new IngameUserQuitGameListener(this).node());

    }

    @Override
    public void init(@Nullable CommonPhase oldPhase) {
        super.init(oldPhase);
        loadWorld();
        mapIngameData = woolbattleApi.mapManager().loadIngameData(game.map());
        for (var user : game.users()) {
            preJoin(user);
        }
    }

    @Override
    public void enable(@Nullable CommonPhase oldPhase) {
        super.enable(oldPhase);
        var splitter = new TeamSplitter(game);
        splitter.splitPlayers();

        for (var user : game.users()) {
            join(user);
        }
        woolResetScheduler.start();
    }

    @Override
    public void disable(@Nullable CommonPhase newPhase) {
        woolResetScheduler.stop();

        super.disable(newPhase);
    }

    @Override
    public void unload(@Nullable CommonPhase newPhase) {
        super.unload(newPhase);
        woolbattleApi.worldHandler().unloadWorld(world);
        world = null;
    }

    public void preJoin(@NotNull CommonWBUser user) {
        IngameUserInventory.create(user);
    }

    public void join(@NotNull CommonWBUser user) {
        var inventory = IngameUserInventory.get(user);
        inventory.setAllItems();
    }

    public void quit(@NotNull CommonWBUser user) {
        IngameUserInventory.destroy(user);
    }

    private void loadWorld() {
        var schematicPath = woolbattle.mapsDirectory().resolve(game.mapSize().toString()).resolve(game.map().name() + ".litematic");
        if (Files.exists(schematicPath)) {
            var schematic = SchematicReader.read(schematicPath);
            world = game.api().worldHandler().loadIngameWorld(game, schematic);
        } else {
            try {
                Files.createDirectories(schematicPath.getParent());
            } catch (IOException _) {
            }
            world = woolbattleApi.worldHandler().loadIngameWorld(game, null);
            woolbattleApi.woolbattle().logger().error("No schematic file for Map {}-{}", game.map().name(), game.mapSize());
        }
    }

    public CommonMapIngameData mapIngameData() {
        return mapIngameData;
    }

    @Override
    @UnknownNullability
    public CommonIngameWorld world() {
        return world;
    }
}
