/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.listener;

import eu.darkcube.minigame.woolbattle.api.event.game.UserLoginGameEvent;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.minestom.util.adventure.MinestomAdventureSupport;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;

public class MinestomJoinListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            var player = (MinestomPlayer) event.getPlayer();
            var joinResult = woolbattle.eventHandler().playerJoined(player.getUuid());
            if (joinResult == null) {
                // denied
                player.kick("Bad request");
                return;
            }
            var user = joinResult.user();
            var game = joinResult.game();
            woolbattle.player(user, player);
            if (game == null) {
                woolbattle.logger().info("Player {} connecting to SetupMode", user.playerName());
                // setup mode
                woolbattle.setupModeImplementation().enterSetupMode(user, (instance, point) -> {
                    event.setSpawningInstance(instance);
                    player.setRespawnPoint(point);
                });
            } else {
                if (!game.mayJoin(user)) {
                    woolbattle.logger().info("Denied login for {} to game {}", user.playerName(), game.id());
                    player.kick(MinestomAdventureSupport.adventureSupport().convert(Messages.KICKED_FULL.getMessage(user)));
                    return;
                }
                woolbattle.logger().info("Player {} connecting to game {}", user.playerName(), game.id());
                var result = game.playerLogin(user);
                if (result.location() == null && result.result() == UserLoginGameEvent.Result.CANNOT_JOIN) {
                    // no spawn location found, enter setup mode
                    woolbattle.setupModeImplementation().enterSetupMode(user, (instance, point) -> {
                        event.setSpawningInstance(instance);
                        player.setRespawnPoint(point);
                    });
                    return;
                }
                var loc = result.location();
                user.location(loc);
                player.setRespawnPoint(new Pos(loc.x(), loc.y(), loc.z(), loc.yaw(), loc.pitch()));
                event.setSpawningInstance(((MinestomWorld) loc.world()).instance());
            }
        });
        node.addListener(PlayerSpawnEvent.class, event -> {
            var player = (MinestomPlayer) event.getPlayer();
            var user = player.user();
            if (user == null) return;
            if (event.isFirstSpawn()) {
                player.setGameMode(GameMode.SURVIVAL);
                // now it is safe to interact with the player. In AsyncPlayerConfigurationEvent only setRespawnPoint and setSpawningInstance can be done.
                // gotta do things like set the inventory
                var game = user.game();
                if (game == null) {
                    // user is in setup mode. We do not do anything here at this time
                    return;
                }
                game.playerJoin(user);
            }
        });
    }
}
