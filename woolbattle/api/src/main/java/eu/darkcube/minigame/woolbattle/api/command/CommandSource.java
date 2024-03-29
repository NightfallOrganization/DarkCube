package eu.darkcube.minigame.woolbattle.api.command;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.commandapi.util.Vector2f;
import eu.darkcube.system.commandapi.util.Vector3d;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;

public class CommandSource implements ISuggestionProvider, ForwardingAudience.Single {
    private final @NotNull WoolBattleApi woolbattle;
    private final @Nullable Game game;
    private final @NotNull CommandSender sender;
    private final @UnknownNullability Vector3d pos;
    private final @UnknownNullability Vector2f rotation;
    private final @UnknownNullability World world;
    private final @NotNull String name;
    private final @UnknownNullability String displayName;
    private final @NotNull Map<String, Object> extra;
    private final @NotNull String commandPrefix;

    public CommandSource(@NotNull WoolBattleApi woolbattle, @Nullable Game game, @NotNull CommandSender sender, @UnknownNullability Vector3d pos, @UnknownNullability Vector2f rotation, @UnknownNullability World world, @UnknownNullability @NotNull String name, @UnknownNullability String displayName, @NotNull Map<String, Object> extra, @NotNull String commandPrefix) {
        this.woolbattle = woolbattle;
        this.game = game;
        this.sender = sender;
        this.pos = pos;
        this.rotation = rotation;
        this.world = world;
        this.name = name;
        this.displayName = displayName;
        this.extra = extra;
        this.commandPrefix = commandPrefix;
    }

    public void sendMessage(BaseMessage message) {
        source().sendMessage(message);
    }

    public void sendMessage(BaseMessage message, Object... components) {
        source().sendMessage(message, components);
    }

    public @NotNull WoolBattleApi woolbattle() {
        return woolbattle;
    }

    public @Nullable Game game() {
        return game;
    }

    public @NotNull CommandSender sender() {
        return sender;
    }

    public @UnknownNullability Vector3d pos() {
        return pos;
    }

    public @UnknownNullability Vector2f rotation() {
        return rotation;
    }

    public @UnknownNullability World world() {
        return world;
    }

    public @NotNull String name() {
        return name;
    }

    public @UnknownNullability String displayName() {
        return displayName;
    }

    public @NotNull Map<String, Object> extra() {
        return extra;
    }

    public @NotNull String commandPrefix() {
        return commandPrefix;
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
        if (game != null) {
            return game.users().stream().map(WBUser::playerName).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<UUID> getPlayerUniqueIds() {
        if (game != null) {
            return game.users().stream().map(WBUser::uniqueId).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public @NotNull Audience audience() {
        return sender;
    }
}
