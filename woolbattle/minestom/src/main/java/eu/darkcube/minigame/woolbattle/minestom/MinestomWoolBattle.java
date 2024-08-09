/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.command.CommandSender;
import eu.darkcube.minigame.woolbattle.api.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattle;
import eu.darkcube.minigame.woolbattle.common.entity.CommonEntityMetaDataStorage;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.Slot;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.minigame.woolbattle.minestom.command.MinestomCommandSender;
import eu.darkcube.minigame.woolbattle.minestom.entity.MinestomEntity;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomAnimationListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomBlockListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomInteractListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomInventoryListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomItemListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomJoinListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomMoveListener;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomQuitListener;
import eu.darkcube.minigame.woolbattle.minestom.perk.MinestomPerkItemImplementation;
import eu.darkcube.minigame.woolbattle.minestom.perk.MinestomUserPerkImplementation;
import eu.darkcube.minigame.woolbattle.minestom.setup.MinestomSetupModeImplementation;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomUserPermissions;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomUserPlatformAccess;
import eu.darkcube.minigame.woolbattle.minestom.util.MinestomSlotMappings;
import eu.darkcube.minigame.woolbattle.minestom.util.item.MinestomItemsProvider;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.data.BasicMetaDataStorage;
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
        this.api = new MinestomWoolBattleApi(this);
        WoolBattleProvider.PROVIDER.register(WoolBattleApi.class, this.api);
        WoolBattleProvider.PROVIDER.register(Slot.Mappings.class, new MinestomSlotMappings());
        WoolBattleProvider.PROVIDER.register(DefaultUserPerk.Implementation.class, new MinestomUserPerkImplementation());
        WoolBattleProvider.PROVIDER.register(PerkItem.Implementation.class, new MinestomPerkItemImplementation());
        this.playerKey = Key.key(this.api, "minestom_player");
        this.setupModeImplementation = new MinestomSetupModeImplementation(this.api);
    }

    @Override
    public void start() {
        WoolBattleProvider.PROVIDER.register(Items.Provider.class, new MinestomItemsProvider());

        super.start();

        var eventManager = MinecraftServer.getGlobalEventHandler();
        MinestomJoinListener.register(this, eventManager);
        MinestomQuitListener.register(this, eventManager);
        MinestomMoveListener.register(this, eventManager);
        MinestomBlockListener.register(this, eventManager);
        MinestomItemListener.register(this, eventManager);
        MinestomInventoryListener.register(this, eventManager);
        MinestomAnimationListener.register(this, eventManager);
        MinestomInteractListener.register(this, eventManager);

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
        if (oldTeam != null && newTeam != null) {
            logger().info("User {} switched to team {} from {}", user.playerName(), newTeam.key(), oldTeam.key());
        } else if (oldTeam != null) {
            logger().info("User {} left team {}", user.playerName(), oldTeam.key());
        } else if (newTeam != null) {
            logger().info("User {} joined team {}", user.playerName(), newTeam.key());
        }
    }

    @Override
    public @NotNull MinestomWoolBattleApi api() {
        return api;
    }

    @Override
    public @NotNull MinestomUserPlatformAccess createInventoryAccessFor(@NotNull CommonWBUser user) {
        return new MinestomUserPlatformAccess(this, user);
    }

    @Override
    public @NotNull MinestomUserPermissions createPermissionsFor(@NotNull CommonWBUser user) {
        return new MinestomUserPermissions(this, user);
    }

    public CommandSender wrapCommandSender(net.minestom.server.command.CommandSender sender) {
        if (sender instanceof MinestomPlayer player) {
            var user = player.user();
            if (user != null) return user;
        }
        return new MinestomCommandSender(sender);
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
        return entities.computeIfAbsent(entity.unwrap(), k -> new MinestomEntity(k.acquirable(), this));
    }

    public void removed(MinestomEntity entity) {
        entities.remove(entity.entity().unwrap());
        entityMeta(entity).clear();
    }

    @Override
    public @NotNull String namespace() {
        return this.api.namespace();
    }
}
