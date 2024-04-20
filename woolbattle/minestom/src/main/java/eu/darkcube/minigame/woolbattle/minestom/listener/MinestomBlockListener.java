package eu.darkcube.minigame.woolbattle.minestom.listener;

import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.server.item.material.Material;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;

public class MinestomBlockListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(PlayerBlockBreakEvent.class, event -> {
            var player = (MinestomPlayer) event.getPlayer();
            var user = player.user();
            if (user == null) return;
            var instance = event.getInstance();
            var world = woolbattle.worlds().get(instance);
            if (world == null) return;
            var pos = event.getBlockPosition();
            var block = world.blockAt(pos.blockX(), pos.blockY(), pos.blockZ());
            var breakResult = woolbattle.eventHandler().blockBreak(user, block);
            if (breakResult.cancel()) {
                event.setCancelled(true);
            }
        });
        node.addListener(PlayerBlockPlaceEvent.class, event -> {
            var player = (MinestomPlayer) event.getPlayer();
            var user = player.user();
            if (user == null) return;
            var instance = event.getInstance();
            var world = woolbattle.worlds().get(instance);
            if (world == null) return;
            var pos = event.getBlockPosition();
            var block = world.blockAt(pos.blockX(), pos.blockY(), pos.blockZ());
            var placingBlock = event.getBlock();
            var minestomMaterial = placingBlock.registry().material();
            if (minestomMaterial == null) {
                woolbattle.logger().severe("No corresponding material for block " + placingBlock);
                return;
            }
            var material = Material.of(minestomMaterial);
            var placeResult = woolbattle.eventHandler().blockPlace(user, block, material);
            if (placeResult.cancel()) {
                event.setCancelled(true);
            }
        });
    }
}
