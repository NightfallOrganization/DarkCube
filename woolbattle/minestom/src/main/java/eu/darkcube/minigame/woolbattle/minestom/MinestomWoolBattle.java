/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.derklaro.aerogel.binding.BindingBuilder;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattle;
import eu.darkcube.minigame.woolbattle.common.entity.CommonEntityMetaDataStorage;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.minigame.woolbattle.minestom.entity.MinestomEntity;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomAnimationListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomBlockListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomInventoryListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomItemListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomJoinListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomMoveListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomQuitListener;
import eu.darkcube.minigame.woolbattle.minestom.setup.MinestomSetupModeImplementation;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomUserInventoryAccess;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomUserPermissions;
import eu.darkcube.minigame.woolbattle.minestom.util.item.MinestomItemsProvider;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.data.BasicMetaDataStorage;
import eu.darkcube.system.util.data.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.thread.Acquirable;
import net.minestom.server.timer.TaskSchedule;

public class MinestomWoolBattle extends CommonWoolBattle {
    private final @NotNull MinestomWoolBattleApi api;
    private final @NotNull Map<Instance, MinestomWorld> worlds = new ConcurrentHashMap<>();
    private final @NotNull Map<Entity, MinestomEntity> entities = new ConcurrentHashMap<>();
    private final @NotNull Map<String, BasicMetaDataStorage> entityMetas = new ConcurrentHashMap<>();
    private final @NotNull Key playerKey;
    private final @NotNull MinestomSetupModeImplementation setupModeImplementation;

    public MinestomWoolBattle() {
        super();
        api = new MinestomWoolBattleApi(this);
        this.playerKey = new Key(api, "minestomPlayer");
        setupModeImplementation = new MinestomSetupModeImplementation(api);
    }

    @Override
    public void start() {
        var ext = InjectionLayer.ext();
        ext.install(BindingBuilder.create().bind(Items.Provider.class).toInstance(new MinestomItemsProvider()));

        super.start();

        var eventManager = MinecraftServer.getGlobalEventHandler();
        MinestomJoinListener.register(this, eventManager);
        MinestomQuitListener.register(this, eventManager);
        MinestomMoveListener.register(this, eventManager);
        MinestomBlockListener.register(this, eventManager);
        MinestomItemListener.register(this, eventManager);
        MinestomInventoryListener.register(this, eventManager);
        MinestomAnimationListener.register(this, eventManager);

        MinecraftServer.getConnectionManager().setPlayerProvider(MinestomPlayer::new);
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            api.scheduler().processTick();
            for (var game : api.games().games()) {
                game.scheduler().processTick();
            }
        }, TaskSchedule.immediate(), TaskSchedule.tick(1));
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public @NotNull MinestomSetupModeImplementation setupModeImplementation() {
        return setupModeImplementation;
    }

    @Override
    public void broadcastTeamUpdate(@NotNull CommonWBUser user, @Nullable CommonTeam oldTeam, @Nullable CommonTeam newTeam) {
        logger().info("User " + user.playerName() + " switched to team " + (newTeam == null ? "null" : newTeam.key()) + " from  " + (oldTeam == null ? "null" : oldTeam.key()));
    }

    @Override
    public @NotNull MinestomWoolBattleApi api() {
        return api;
    }

    @Override
    public @NotNull MinestomUserInventoryAccess createInventoryAccessFor(@NotNull CommonWBUser user) {
        return new MinestomUserInventoryAccess(this, user);
    }

    @Override
    public @NotNull MinestomUserPermissions createPermissionsFor(@NotNull CommonWBUser user) {
        return new MinestomUserPermissions(this, user);
    }

    public @NotNull MinestomPlayer player(@NotNull CommonWBUser user) {
        return user.metadata().get(playerKey);
    }

    public void player(@NotNull CommonWBUser user, @NotNull MinestomPlayer player) {
        user.metadata().set(playerKey, player);
        player.user(user);
    }

    public @NotNull Map<@NotNull Instance, @NotNull MinestomWorld> worlds() {
        return worlds;
    }

    public CommonEntityMetaDataStorage entityMeta(MinestomEntity entity) {
        return new CommonEntityMetaDataStorage(entityMetas, String.valueOf(entity.entity().unwrap().getEntityId()));
    }

    public MinestomEntity entity(Acquirable<Entity> entity) {
        return entities.computeIfAbsent(entity.unwrap(), k -> new MinestomEntity(k.getAcquirable(), this));
    }

    public void removed(MinestomEntity entity) {
        entities.remove(entity.entity().unwrap());
        entityMeta(entity).clear();
    }
}
