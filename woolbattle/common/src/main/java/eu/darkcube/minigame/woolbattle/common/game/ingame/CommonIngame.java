/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame;

import java.io.IOException;
import java.nio.file.Files;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.CommonPhase;
import eu.darkcube.minigame.woolbattle.common.map.CommonMapIngameData;
import eu.darkcube.minigame.woolbattle.common.util.schematic.SchematicReader;
import eu.darkcube.minigame.woolbattle.common.world.CommonIngameWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.GameState;

public class CommonIngame extends CommonPhase {

    private CommonIngameWorld world;
    private CommonMapIngameData mapIngameData;

    public CommonIngame(@NotNull CommonGame game) {
        super(game, GameState.INGAME);
    }

    @Override
    public void init(@Nullable CommonPhase oldPhase) {
        super.init(oldPhase);
        loadWorld();
        mapIngameData = woolbattleApi.mapManager().loadIngameData(game.map());
    }

    @Override
    public void enable(@Nullable CommonPhase oldPhase) {
        super.enable(oldPhase);
        var splitter = new TeamSplitter(game);
        splitter.splitPlayers();
    }

    @Override
    public void unload(@Nullable CommonPhase newPhase) {
        super.unload(newPhase);
        woolbattleApi.worldHandler().unloadWorld(world);
        world = null;
    }

    private void loadWorld() {
        var schematicPath = woolbattle.mapsDirectory().resolve(game.mapSize().toString()).resolve(game.map().name() + ".litematic");
        if (Files.exists(schematicPath)) {
            var schematic = SchematicReader.read(schematicPath);
            world = game.woolbattle().worldHandler().loadIngameWorld(game, schematic);
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

    public CommonIngameWorld world() {
        return world;
    }
}
