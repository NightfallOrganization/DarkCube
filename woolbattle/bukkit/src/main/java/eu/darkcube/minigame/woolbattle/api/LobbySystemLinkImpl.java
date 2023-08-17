/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api;

import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.DriverEnvironment;
import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.channel.ChannelMessageSender;
import eu.cloudnetservice.driver.channel.ChannelMessageTarget;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.cloudnetservice.modules.bridge.player.executor.PlayerExecutor;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.DarkCubeBukkit;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.util.GameState;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class LobbySystemLinkImpl implements LobbySystemLink {
    private static final String CHANNEL = "lobbysystem_connector";

    private final WoolBattleBukkit woolbattle;
    private final UpdateTask updateTask;
    private final RequestListener requestListener = new RequestListener();
    private boolean fullyLoaded = false;
    private boolean enabled = false;

    public LobbySystemLinkImpl(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        this.updateTask = new UpdateTask(woolbattle);
    }

    @Override public boolean enabled() {
        return enabled;
    }

    public void enable() {
        if (PServerProvider.instance().isPServer()) return; // Do not enable if we are a PServer
        updateTask.runTaskTimer(50);
        fullyLoaded = false;
        InjectionLayer.boot().instance(EventManager.class).registerListener(requestListener);
        new Scheduler(woolbattle, this::setFullyLoaded).runTask();
        DarkCubeBukkit.autoConfigure(false);
        enabled = true;
    }

    public void disable() {
        if (!enabled) return;
        InjectionLayer.boot().instance(EventManager.class).unregisterListener(requestListener);
        updateTask.cancel();
        enabled = false;
    }

    private void setFullyLoaded() {
        fullyLoaded = true;
        update();
    }

    @Override public void update() {
        if (!fullyLoaded) return;
        if (!enabled) return;
        DarkCubeBukkit.gameState(gameState());
        DarkCubeBukkit.playingPlayers().set(playingPlayers());
        DarkCubeBukkit.maxPlayingPlayers().set(woolbattle.maxPlayers());
        ServiceInfoHolder serviceInfoHolder = InjectionLayer.boot().instance(ServiceInfoHolder.class);
        BridgeServiceHelper serviceHelper = InjectionLayer.ext().instance(BridgeServiceHelper.class);
        serviceHelper.maxPlayers().set(1000);
        DarkCubeBukkit.displayName("LobbyV2");
        serviceInfoHolder.publishServiceInfoUpdate();
    }

    private Component displayName(@NotNull Map map) {
        return Component
                .text(map.getName(), NamedTextColor.LIGHT_PURPLE)
                .append(Component.space())
                .append(Component.text("(" + map.size() + ")", NamedTextColor.GRAY));
    }

    private int playingPlayers() {
        if (woolbattle.lobby().enabled()) return WBUser.onlineUsers().size();
        if (woolbattle.ingame().enabled()) return (int) WBUser.onlineUsers().stream().filter(u -> !u.getTeam().isSpectator()).count();
        return 0;
    }

    private GameState gameState() {
        if (woolbattle.lobby().enabled()) return GameState.LOBBY;
        if (woolbattle.ingame().enabled()) return GameState.INGAME;
        if (woolbattle.endgame().enabled()) return GameState.STOPPING;
        return GameState.UNKNOWN;
    }

    private void schedule(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) runnable.run();
        else new Scheduler(woolbattle, runnable).runTask();
    }

    private void requestIds(ChannelMessageSender sender) {
        DataBuf.Mutable buffer = DataBuf.empty();
        boolean gameSizeLoaded = woolbattle.gameData().mapSize() != null;

        if (gameSizeLoaded) fillGameSize(buffer);
        else fillGameSizeUnknown(buffer);

        ChannelMessage message = ChannelMessage.builder().target(sender.type(), sender.name()).channel(CHANNEL).buffer(buffer).build();
        message.send();
    }

    private void fillGameSizeUnknown(DataBuf.Mutable buffer) {
        java.util.Map<MapSize, Collection<Map>> maps = new HashMap<>();
        for (Map map : woolbattle.mapManager().getMaps()) {
            if (!map.isEnabled()) continue;
            maps.computeIfAbsent(map.size(), k -> new ArrayList<>()).add(map);
        }
        buffer.writeByte((byte) 0); // 0 to indicate that we have not selected a GameSize
        buffer.writeInt(maps.size()); // Count

        for (java.util.Map.Entry<MapSize, Collection<Map>> entry : maps.entrySet()) {
            fillGameSizeUnknownMaps(buffer, entry.getKey(), entry.getValue()); // Fill every single map
        }
    }

    private void fillGameSizeUnknownMaps(DataBuf.Mutable buffer, MapSize size, Collection<Map> maps) {
        buffer.writeInt(size.teams() * size.teamSize()); // Max players for following maps
        buffer.writeString(size.toString()); // Textual representation of the GameSize
        buffer.writeInt(maps.size()); // Count, again

        for (Map map : maps) {
            buffer.writeString(map.getName());
        }
    }

    private void fillGameSize(DataBuf.Mutable buffer) {
        MapSize size = woolbattle.gameData().mapSize();
        Map map = woolbattle.gameData().map();
        String mapName = map == null ? null : map.getName();

        buffer.writeByte((byte) 1); // 1 to indicate that we have selected a GameSize
        buffer.writeString(size.toString()); // Textual representation of the GameSize
        buffer.writeInt(size.teams() * size.teamSize()); // Max player count
        buffer.writeByte((byte) (mapName == null ? 0 : 1));
        if (mapName != null) buffer.writeString(mapName); // Text to display
    }

    private void connectionFailed(@NotNull ChannelMessageSender sender, int requestId, @NotNull String reason, @NotNull String arg) {
        DataBuf.Mutable buffer = DataBuf.empty();
        buffer.writeInt(requestId);
        buffer.writeString(reason);
        buffer.writeString(arg);
        send("connection_failed", sender, buffer);
    }

    private void connectionSuccess(@NotNull ChannelMessageSender sender, int requestId) {
        DataBuf.Mutable buffer = DataBuf.empty();
        buffer.writeInt(requestId);
        send("connection_success", sender, buffer);
    }

    private void workingOnRequest(@NotNull ChannelMessageSender sender, int requestId) {
        DataBuf.Mutable buffer = DataBuf.empty();
        buffer.writeInt(requestId);
        send("working_on_request", sender, buffer);
    }

    private void send(@NotNull String message, @NotNull ChannelMessageSender target, @Nullable DataBuf buffer) {
        DriverEnvironment environment = target.type();
        ChannelMessageTarget.Type type = environment == DriverEnvironment.NODE ? ChannelMessageTarget.Type.NODE : ChannelMessageTarget.Type.SERVICE;
        ChannelMessageTarget realTarget = ChannelMessageTarget.of(type, target.name());
        send(message, realTarget, buffer);
    }

    private void send(@NotNull String message, @NotNull ChannelMessageTarget target, @Nullable DataBuf buffer) {
        ChannelMessage channelMessage = ChannelMessage.builder().target(target).channel(CHANNEL).message(message).buffer(buffer).build();
        channelMessage.send();
    }

    private class RequestListener {

        @EventListener public void handle(ChannelMessageReceiveEvent event) {
            if (!event.channel().equals(CHANNEL)) return;
            @NotNull String msg = event.message();
            @NotNull ChannelMessageSender sender = event.sender();
            if (msg.equals("request_ids")) {
                schedule(() -> requestIds(sender));
            } else if (msg.equals("connect_player")) {
                @NotNull DataBuf buffer = event.content();
                int requestId = buffer.readInt();
                @NotNull UUID uuid = buffer.readUniqueId();
                @NotNull String mapSizeString = buffer.readString();
                @NotNull String mapString = buffer.readString();

                @Nullable MapSize mapSize = MapSize.fromString(mapSizeString);
                if (mapSize == null) {
                    connectionFailed(sender, requestId, "invalid_target", mapSizeString);
                    return;
                }

                @Nullable Map map = woolbattle.mapManager().getMap(mapString, mapSize);
                if (map == null) {
                    connectionFailed(sender, requestId, "invalid_target_map", mapString);
                    return;
                }

                workingOnRequest(sender, requestId);

                schedule(() -> {
                    if (!woolbattle.lobby().enabled()) {
                        connectionFailed(sender, requestId, "user_defined", "game_already_started");
                        return;
                    }
                    @Nullable MapSize loadedMapSize = woolbattle.gameData().mapSize();
                    if (loadedMapSize != null) {
                        if (!loadedMapSize.equals(mapSize)) {
                            connectionFailed(sender, requestId, "invalid_data", mapSizeString);
                            return;
                        }
                        // Game is already loaded with the MapSize. Only joining is left to do
                    } else {
                        woolbattle.lobby().loadGame(mapSize);
                    }
                    PlayerManager playerManager = InjectionLayer.boot().instance(PlayerManager.class);
                    PlayerExecutor executor = playerManager.playerExecutor(uuid);
                    executor.connect(InjectionLayer.boot().instance(ComponentInfo.class).componentName());
                });
            }
        }
    }

    private class UpdateTask extends Scheduler {

        public UpdateTask(WoolBattleBukkit woolbattle) {
            super(woolbattle);
        }

        @Override public void run() {
            update();
        }
    }
}
