/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import de.dytanic.cloudnet.driver.channel.ChannelMessageSender;
import de.dytanic.cloudnet.driver.channel.ChannelMessageTarget;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.dytanic.cloudnet.driver.serialization.ProtocolBuffer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.executor.PlayerExecutor;
import de.dytanic.cloudnet.ext.bridge.server.BridgeServerHelper;
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
        CloudNetDriver.getInstance().getEventManager().registerListener(requestListener);
        new Scheduler(woolbattle, this::setFullyLoaded).runTask();
        DarkCubeBukkit.autoConfigure(false);
        enabled = true;
    }

    public void disable() {
        if (!enabled) return;
        CloudNetDriver.getInstance().getEventManager().unregisterListener(requestListener);
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
        BridgeServerHelper.setMaxPlayers(1000); // Unlimited spectators
        DarkCubeBukkit.displayName("LobbyV2");
        BridgeServerHelper.updateServiceInfo();
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
        ProtocolBuffer buffer = ProtocolBuffer.create();
        boolean gameSizeLoaded = woolbattle.gameData().mapSize() != null;

        if (gameSizeLoaded) fillGameSize(buffer);
        else fillGameSizeUnknown(buffer);

        ChannelMessage message = ChannelMessage
                .builder()
                .target(sender.getType(), sender.getName())
                .channel(CHANNEL)
                .buffer(buffer)
                .build();
        message.send();
    }

    private void fillGameSizeUnknown(ProtocolBuffer buffer) {
        java.util.Map<MapSize, Collection<Map>> maps = new HashMap<>();
        for (Map map : woolbattle.mapManager().getMaps()) {
            if (!map.isEnabled()) continue;
            maps.computeIfAbsent(map.size(), k -> new ArrayList<>()).add(map);
        }
        buffer.writeByte(0); // 0 to indicate that we have not selected a GameSize
        buffer.writeVarInt(maps.size()); // Count

        for (java.util.Map.Entry<MapSize, Collection<Map>> entry : maps.entrySet()) {
            fillGameSizeUnknownMaps(buffer, entry.getKey(), entry.getValue()); // Fill every single map
        }
    }

    private void fillGameSizeUnknownMaps(ProtocolBuffer buffer, MapSize size, Collection<Map> maps) {
        buffer.writeVarInt(size.teams() * size.teamSize()); // Max players for following maps
        buffer.writeString(size.toString()); // Textual representation of the GameSize
        buffer.writeVarInt(maps.size()); // Count, again

        for (Map map : maps) {
            buffer.writeString(map.getName());
        }
    }

    private void fillGameSize(ProtocolBuffer buffer) {
        MapSize size = woolbattle.gameData().mapSize();
        Map map = woolbattle.gameData().map();
        String mapName = map == null ? null : map.getName();

        buffer.writeByte(1); // 1 to indicate that we have selected a GameSize
        buffer.writeString(size.toString()); // Textual representation of the GameSize
        buffer.writeVarInt(size.teams() * size.teamSize()); // Max player count
        buffer.writeOptionalString(mapName); // Text to display
    }

    private void connectionFailed(@NotNull ChannelMessageSender sender, int requestId, @NotNull String reason, @NotNull String arg) {
        ProtocolBuffer buffer = ProtocolBuffer.create();
        buffer.writeVarInt(requestId);
        buffer.writeString(reason);
        buffer.writeString(arg);
        send("connection_failed", sender, buffer);
    }

    private void connectionSuccess(@NotNull ChannelMessageSender sender, int requestId) {
        ProtocolBuffer buffer = ProtocolBuffer.create();
        buffer.writeVarInt(requestId);
        send("connection_success", sender, buffer);
    }

    private void workingOnRequest(@NotNull ChannelMessageSender sender, int requestId) {
        ProtocolBuffer buffer = ProtocolBuffer.create();
        buffer.writeVarInt(requestId);
        send("working_on_request", sender, buffer);
    }

    private void send(@NotNull String message, @NotNull ChannelMessageSender target, @Nullable ProtocolBuffer buffer) {
        DriverEnvironment environment = target.getType();
        ChannelMessageTarget.Type type = environment == DriverEnvironment.CLOUDNET ? ChannelMessageTarget.Type.NODE : ChannelMessageTarget.Type.SERVICE;
        ChannelMessageTarget realTarget = new ChannelMessageTarget(type, target.getName());
        send(message, realTarget, buffer);
    }

    private void send(@NotNull String message, @NotNull ChannelMessageTarget target, @Nullable ProtocolBuffer buffer) {
        ChannelMessage channelMessage = ChannelMessage.builder().target(target).channel(CHANNEL).message(message).buffer(buffer).build();
        CloudNetDriver.getInstance().getMessenger().sendChannelMessage(channelMessage);
    }

    private class RequestListener {

        @EventListener public void handle(ChannelMessageReceiveEvent event) {
            if (!event.getChannel().equals(CHANNEL)) return;
            @Nullable String msg = event.getMessage();
            if (msg == null) return;
            @NotNull ChannelMessageSender sender = event.getSender();
            if (msg.equals("request_ids")) {
                schedule(() -> requestIds(sender));
            } else if (msg.equals("connect_player")) {
                @NotNull ProtocolBuffer buffer = event.getBuffer();
                int requestId = buffer.readVarInt();
                @NotNull UUID uuid = buffer.readUUID();
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
                    IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
                    PlayerExecutor executor = playerManager.getPlayerExecutor(uuid);
                    executor.connect(CloudNetDriver.getInstance().getComponentName());
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
