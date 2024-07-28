package eu.darkcube.minigame.woolbattle.minestom.listener;

import eu.darkcube.minigame.woolbattle.api.event.user.UserInteractEvent;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.server.item.ItemBuilder;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;

public class MinestomInteractListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(PlayerUseItemEvent.class, event -> handle(woolbattle, event, event));
    }

    private static void handle(MinestomWoolBattle woolbattle, PlayerInstanceEvent pevent, ItemEvent ievent) {
        var player = (MinestomPlayer) pevent.getPlayer();
        var user = player.user();
        if (user == null) return;
        var itemStack = ievent.getItemStack();
        var item = ItemBuilder.item(itemStack);
        woolbattle.api().eventManager().call(new UserInteractEvent(user, item, UserInteractEvent.Action.RIGHT_CLICK_AIR));
    }
}
