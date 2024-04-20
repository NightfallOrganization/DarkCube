package eu.darkcube.minigame.woolbattle.minestom.listener;

import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.event.MinestomInventoryClickEvent;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryClickEvent;

public class MinestomInventoryListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(InventoryClickEvent.class, event -> {
            var player = (MinestomPlayer) event.getPlayer();
            var user = player.user();
            if (user == null) return;
            woolbattle.api().eventManager().call(new MinestomInventoryClickEvent(user, event));
        });
    }
}
