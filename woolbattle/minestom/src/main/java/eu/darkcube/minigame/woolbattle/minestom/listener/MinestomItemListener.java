package eu.darkcube.minigame.woolbattle.minestom.listener;

import eu.darkcube.minigame.woolbattle.api.event.item.UserDropItemEvent;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.server.item.ItemBuilder;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.ItemDropEvent;

public class MinestomItemListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(ItemDropEvent.class, event -> {
            var player = (MinestomPlayer) event.getPlayer();
            var user = player.user();
            if (user == null) return;
            var itemStack = event.getItemStack();
            var item = ItemBuilder.item(itemStack);
            var userDropItemEvent = new UserDropItemEvent(user, item);
            woolbattle.api().eventManager().call(userDropItemEvent);
            if (userDropItemEvent.cancelled()) {
                event.setCancelled(true);
            }
        });
    }
}
