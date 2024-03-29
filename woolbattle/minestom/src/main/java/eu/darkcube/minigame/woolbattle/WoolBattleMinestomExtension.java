/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle;

import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.world.FullbrightChunk;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.block.Block;

public class WoolBattleMinestomExtension extends Extension {

    private MinestomWoolBattle woolbattle;

    @Override
    public void initialize() {
        woolbattle = new MinestomWoolBattle();
        woolbattle.start();
        woolbattle.api().fullyLoadedFuture().complete(null);
        var command = new Command("test");
        var instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.setChunkSupplier(FullbrightChunk::new);
        instance.setGenerator(unit -> {
            unit.modifier().fillHeight(unit.absoluteStart().blockY(), 60, Block.STONE);
            unit.modifier().fillHeight(60, 61, Block.GRASS_BLOCK);
            // unit.modifier().fillBiome(biome);
        });

        command.setDefaultExecutor((sender, context) -> {
            var player = (Player) sender;
            player.setInstance(instance, new Pos(0, 62, 0));
        });
        MinecraftServer.getCommandManager().register(command);
    }

    @Override
    public void terminate() {
        woolbattle.stop();
    }
}
