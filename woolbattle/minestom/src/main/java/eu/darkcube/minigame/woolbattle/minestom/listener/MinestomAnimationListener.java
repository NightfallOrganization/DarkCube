package eu.darkcube.minigame.woolbattle.minestom.listener;

import java.util.Objects;

import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerItemAnimationEvent;
import net.minestom.server.gamedata.tags.Tag;

public class MinestomAnimationListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        var arrows = Objects.requireNonNull(MinecraftServer.getTagManager().getTag(Tag.BasicType.ITEMS, "minecraft:arrows")).getValues();
        node.addListener(PlayerItemAnimationEvent.class, event -> {
            var player = event.getPlayer();
            switch (event.getItemAnimationType()) {
                case BOW, CROSSBOW -> {
                    var contents = player.getInventory().getItemStacks();
                    var found = false;
                    for (var itemStack : contents) {
                        if (arrows.contains(itemStack.material().namespace())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        // No arrow in inventory
                        event.setCancelled(true);
                    }
                }
                default -> {
                }
            }
        });
    }
}
