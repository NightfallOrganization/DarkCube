/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.user.UserJoinGameEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;

public class MinestomLobbyJoinGameListener extends ConfiguredListener<UserJoinGameEvent> {
    private final MinestomWoolBattle woolbattle;

    public MinestomLobbyJoinGameListener(MinestomWoolBattle woolbattle) {
        super(UserJoinGameEvent.class);
        this.woolbattle = woolbattle;
    }

    @Override
    public void accept(UserJoinGameEvent event) {
        var user = (CommonWBUser) event.user();
        var player = woolbattle.player(user);
        var packet = player.getAddPlayerToList();
        for (var wbUser : event.game().users()) {
            var u = (CommonWBUser) wbUser;
            var p = woolbattle.player(u);
            p.sendPacketDirect(packet);

            if (u != user) {
                player.sendPacketDirect(p.getAddPlayerToList());
            }
        }
        player.postJoin();

        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodSaturation(0);
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024);
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(new AttributeModifier("test", 1000, AttributeOperation.MULTIPLY_BASE));
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(new AttributeModifier("test2", 1000, AttributeOperation.MULTIPLY_BASE));
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(new AttributeModifier("test3", 1000, AttributeOperation.MULTIPLY_BASE));
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(new AttributeModifier("test4", 1000, AttributeOperation.MULTIPLY_BASE));
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(new AttributeModifier("test5", 1000, AttributeOperation.MULTIPLY_BASE));
    }
}
