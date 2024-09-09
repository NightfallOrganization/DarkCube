/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.command;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.api.world.Rotation;
import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;

// @formatter:off
public record CommandSource(@NotNull WoolBattleApi woolbattle,
                            @Nullable Game game,
                            @NotNull CommandSender sender,
                            @UnknownNullability Position pos,
                            @UnknownNullability Rotation rotation,
                            @UnknownNullability World world,
                            @Nullable Entity entity,
                            @NotNull String name,
                            @UnknownNullability String displayName,
                            @NotNull Map<String, Object> extra,
                            @NotNull String commandPrefix
) implements ISuggestionProvider, ForwardingAudience.Single {
    // @formatter:on

    public void sendMessage(BaseMessage message) {
        source().sendMessage(message);
    }

    public void sendMessage(BaseMessage message, Object... components) {
        source().sendMessage(message, components);
    }

    public boolean hasPermission(String permission) {
        return sender().hasPermission(permission);
    }

    public <T> @UnknownNullability T get(@NotNull String key) {
        return this.get(key, null);
    }

    public <T> @UnknownNullability T get(@NotNull String key, @Nullable T defaultValue) {
        return (T) this.extra.getOrDefault(key, defaultValue);
    }

    public @NotNull CommandSender source() {
        return sender;
    }

    @Override
    public Collection<String> getPlayerNames() {
        return getPlayers().stream().map(WBUser::playerName).toList();
    }

    @Override
    public Collection<UUID> getPlayerUniqueIds() {
        return getPlayers().stream().map(WBUser::uniqueId).toList();
    }

    public Collection<? extends WBUser> getPlayers() {
        if (game != null) {
            return game.users();
        }
        return List.of();
    }

    @Override
    public @NotNull CommandSender audience() {
        return sender;
    }
}
