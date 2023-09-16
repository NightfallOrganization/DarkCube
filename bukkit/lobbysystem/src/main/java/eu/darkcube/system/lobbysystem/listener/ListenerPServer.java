/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.channel.ChannelMessageSender;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.server.ServerInformation;
import eu.darkcube.system.pserver.bukkit.event.PServerStopEvent;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.Key;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Singleton public class ListenerPServer implements Listener {

    private final Key key = new Key(Lobby.getInstance(), "connecting.pserver");
    private final Key keyCallback = new Key(Lobby.getInstance(), "connecting.pserver.callback");
    private final Key keyStarted = new Key(Lobby.getInstance(), "connecting.pserver.started");

    @Inject public ListenerPServer() {
        Bukkit.getPluginManager().registerEvents(this, Lobby.getInstance());
    }

    public boolean setStarted(User user, UniqueId id, Consumer<Boolean> callback) {
        user.metadata().set(keyCallback, callback);
        user.metadata().set(keyStarted, id);
        return true;
    }

    public void remove(User user) {
        var callback = user.metadata().<Consumer<Boolean>>remove(keyCallback);
        new BukkitRunnable() {
            @Override public void run() {
                if (callback != null) callback.accept(false);
            }
        }.runTask(Lobby.getInstance());
        user.metadata().remove(keyStarted);
        user.metadata().remove(key);
    }

    public void failed(User user) {
        Thread.dumpStack();
        var callback = user.metadata().<Consumer<Boolean>>remove(keyCallback);
        remove(user);
        new BukkitRunnable() {
            @Override public void run() {
                if (callback != null) callback.accept(true);
                user.sendActionBar(Message.CONNECTING_TO_PSERVER_FAILED);
            }
        }.runTask(Lobby.getInstance());
    }

    public void connectPlayer(User user, PServerExecutor executor) {
        executor.type().thenAccept(type -> {
            if (type == PServerExecutor.Type.GAMEMODE) {
                user.metadata().set(key, executor);
                ChannelMessage
                        .builder()
                        .channel("darkcube_lobbysystem")
                        .message("send_is_loaded")
                        .buffer(DataBuf.empty().writeString(executor.id().toString()))
                        .targetServices()
                        .build()
                        .send();
            } else {
                executor.connectPlayer(user.uniqueId()).thenAccept(suc -> {
                    if (!suc) {
                        failed(user);
                    }
                });
            }
        });
    }

    public CompletableFuture<Boolean> connectNow(User user, PServerExecutor pserver) {
        return Lobby.getInstance().serverManager().forPServer(pserver).thenCompose(information -> {
            if (information == null) return CompletableFuture.completedFuture(null);
            return information.connectPlayer(user.uniqueId());
        }).thenApply((ServerInformation.State state) -> {
            if (state == null) return false;
            if (state.success()) return true;
            state.cause().printStackTrace();
            return false;
        }).whenComplete((suc, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                failed(user);
            } else if (!suc) { // Failed to connect player
                failed(user);
            }
        });
    }

    private void pserverLoaded(ChannelMessageSender sender) {
        for (var player : Bukkit.getOnlinePlayers()) {
            var user = UserAPI.instance().user(player.getUniqueId());
            if (!user.metadata().has(key)) continue;
            var senderName = sender.name();
            var pserver = user.metadata().<PServerExecutor>get(key);
            pserver.serverName().thenAccept(name -> {
                if (!senderName.equals(name)) return;
                connectNow(user, pserver);
                user.metadata().remove(key);
            });
        }
    }

    @EventListener public void handle(PServerStopEvent event) {
        for (var player : Bukkit.getOnlinePlayers()) {
            var user = UserAPI.instance().user(player.getUniqueId());
            var id = user.metadata().<UniqueId>get(keyStarted);
            if (id == null) continue;
            if (id.equals(event.pserver().id())) {
                failed(user);
            }
        }
    }

    @EventHandler public void handle(PlayerQuitEvent event) {
        var user = UserAPI.instance().user(event.getPlayer().getUniqueId());
        remove(user);
    }

    @EventListener public void handle(ChannelMessageReceiveEvent event) {
        if (!event.channel().equals("darkcube_lobbysystem")) return;
        if (event.message().equals("pserver_loaded")) pserverLoaded(event.sender());
    }
}
