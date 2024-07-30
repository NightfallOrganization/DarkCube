package eu.darkcube.minigame.woolbattle.minestom.listener;

import eu.darkcube.minigame.woolbattle.api.event.user.UserInteractEvent;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.server.item.ItemBuilder;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.item.ItemStack;

public class MinestomInteractListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(PlayerUseItemEvent.class, event -> handle(woolbattle, event, event.getItemStack(), UserInteractEvent.Action.RIGHT_CLICK_AIR));
        node.addListener(PlayerBlockInteractEvent.class, event -> {
            var itemStack = event.getPlayer().getItemInHand(event.getHand());
            if (itemStack.isAir()) return;
            if (!itemStack.material().isBlock()) return;
            handle(woolbattle, event, itemStack, UserInteractEvent.Action.RIGHT_CLICK_BLOCK);
        });
    }

    private static void handle(MinestomWoolBattle woolbattle, PlayerInstanceEvent pevent, ItemStack itemStack, UserInteractEvent.Action action) {
        var player = (MinestomPlayer) pevent.getPlayer();
        var user = player.user();
        if (user == null) return;
        var item = ItemBuilder.item(itemStack);
        woolbattle.api().eventManager().call(new UserInteractEvent(user, item, action));
    }
}
