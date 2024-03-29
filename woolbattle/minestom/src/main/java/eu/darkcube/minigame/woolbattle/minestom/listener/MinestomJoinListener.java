package eu.darkcube.minigame.woolbattle.minestom.listener;

import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;

public class MinestomJoinListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            var player = event.getPlayer();
            var joinResult = woolbattle.eventHandler().playerJoined(player.getUuid());
            if (joinResult == null) {
                // denied
                event.getPlayer().kick("Bad request");
                return;
            }
            var user = joinResult.user();
            var game = joinResult.game();
            woolbattle.player(user, player);
            if (game == null) {
                woolbattle.logger().info("Player " + user.playerName() + " connecting to SetupMode");
                // setup mode
                woolbattle.setupModeImplementation().enterSetupMode(user, (instance, point) -> {
                    event.setSpawningInstance(instance);
                    player.setRespawnPoint(point);
                });
            } else {
                woolbattle.logger().info("Player " + user.playerName() + " connecting to game " + game.id());
                var result = game.playerJoined(user);
                if (result.location() == null) {
                    game.playerQuit(user);
                    woolbattle.setupModeImplementation().enterSetupMode(user, (instance, point) -> {
                        event.setSpawningInstance(instance);
                        player.setRespawnPoint(point);
                    });
                    return;
                }
                var loc = result.location();
                event.getPlayer().setRespawnPoint(new Pos(loc.x(), loc.y(), loc.z(), loc.yaw(), loc.pitch()));
                event.setSpawningInstance(((MinestomWorld) loc.world()).instance());
            }
        });
    }
}
