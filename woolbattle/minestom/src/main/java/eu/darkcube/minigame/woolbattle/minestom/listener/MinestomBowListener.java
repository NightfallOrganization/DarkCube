/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.listener;

import static eu.darkcube.system.server.item.ItemBuilder.item;

import eu.darkcube.minigame.woolbattle.api.event.user.UserShootBowEvent;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.ItemUpdateStateEvent;
import net.minestom.server.event.player.PlayerItemAnimationEvent;
import net.minestom.server.item.Material;

public class MinestomBowListener {
    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        Key chargeSinceKey = Key.key(woolbattle, "charge_since");
        node.addListener(PlayerItemAnimationEvent.class, event -> {
            if (event.getItemAnimationType() != PlayerItemAnimationEvent.ItemAnimationType.BOW) return;
            var player = (MinestomPlayer) event.getPlayer();
            var user = player.user();
            if (user == null) return;
            user.metadata().set(chargeSinceKey, System.nanoTime());
        });
        node.addListener(ItemUpdateStateEvent.class, event -> {
            var item = event.getItemStack();
            if (item.material() != Material.BOW) return;
            var player = (MinestomPlayer) event.getPlayer();
            var user = player.user();
            if (user == null) return;
            var eyeLocation = user.eyeLocation();
            if (eyeLocation == null) return;
            if (!user.metadata().has(chargeSinceKey)) return;
            var chargeTimeNanos = System.nanoTime() - user.metadata().<Long>remove(chargeSinceKey);
            var chargeTimeMillis = (double) chargeTimeNanos / 1000D;
            var chargeTimeSeconds = chargeTimeMillis / 1000D;
            var power = (float) calculatePower(chargeTimeSeconds);
            var itemBuilder = item(item);
            var shootEvent = new UserShootBowEvent(user, itemBuilder, power);
            woolbattle.api().eventManager().call(shootEvent);
        });
    }

    private static double calculatePower(double chargeTimeSeconds) {
        var power = (chargeTimeSeconds * chargeTimeSeconds + chargeTimeSeconds * 2F) / 3F;
        if (power > 1D) power = 1D;
        return power;
    }
}
