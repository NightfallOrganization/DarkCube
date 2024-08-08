/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.user;

import static eu.darkcube.system.util.data.PersistentDataTypes.BOOLEAN;

import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.event.user.UserAddWoolEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserChangeTeamEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserGetMaxWoolSizeEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserGetWoolBreakAmountEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserParticlesUpdateEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserRemoveWoolEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserWoolCountUpdateEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserWoolSubtractDirectionUpdateEvent;
import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.api.user.HeightDisplay;
import eu.darkcube.minigame.woolbattle.api.user.PerksStorage;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.user.WoolSubtractDirection;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.perk.user.CommonUserPerks;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.BasicMetaDataStorage;
import eu.darkcube.system.util.data.MetaDataStorage;

public class CommonWBUser implements WBUser, ForwardingAudience.Single {
    private final @NotNull Key keyParticles;
    private final @NotNull Key keyHeightDisplay;
    private final @NotNull Key keyWoolSubtractDirection;
    private final @NotNull Key keyPerks;
    private final @NotNull CommonWoolBattleApi woolbattle;
    private final @NotNull User user;
    private final @Nullable CommonGame game;
    private final @NotNull CommonUserPerks perks;
    private final @NotNull UserPlatformAccess platformAccess;
    private final @NotNull UserPermissions permissions;
    private final @NotNull MetaDataStorage metadata = new BasicMetaDataStorage();
    private volatile @Nullable Location location;
    private volatile int woolCount;
    private volatile @Nullable CommonTeam team;

    public CommonWBUser(@NotNull CommonWoolBattleApi woolbattle, @NotNull User user, @Nullable CommonGame game) {
        this.woolbattle = woolbattle;
        this.user = user;
        this.game = game;
        this.perks = new CommonUserPerks(this);
        this.keyParticles = Key.key(woolbattle, "particles");
        this.keyHeightDisplay = Key.key(woolbattle, "height_display");
        this.keyWoolSubtractDirection = Key.key(woolbattle, "wool_subtract_direction");
        this.keyPerks = Key.key(woolbattle, "perks");
        this.platformAccess = woolbattle.woolbattle().createInventoryAccessFor(this);
        this.permissions = woolbattle.woolbattle().createPermissionsFor(this);
        if (this.game != null) { // We might be in setup mode
            this.perks.reloadFromStorage();
        }
    }

    @Override
    public @NotNull CommonWoolBattleApi woolbattle() {
        return woolbattle;
    }

    @Override
    public @NotNull User user() {
        return user;
    }

    @Override
    public @NotNull MetaDataStorage metadata() {
        return metadata;
    }

    @Override
    public int maxWoolSize() {
        var maxWoolSize = 3 * 64;
        var event = new UserGetMaxWoolSizeEvent(this, maxWoolSize);
        woolbattle.eventManager().call(event);
        return event.maxWoolSize();
    }

    @Override
    public int woolBreakAmount() {
        var woolBreakAmount = 2;
        var event = new UserGetWoolBreakAmountEvent(this, woolBreakAmount);
        woolbattle.eventManager().call(event);
        return event.woolBreakAmount();
    }

    @Override
    public int woolCount() {
        return woolCount;
    }

    @Override
    public void woolCount(int woolCount) {
        this.woolCount = woolCount;
        var event = new UserWoolCountUpdateEvent(this, woolCount);
        woolbattle.eventManager().call(event);
        platformAccess.woolCount(woolCount);
    }

    @Override
    public int addWool(int count, boolean dropIfFull) {
        if (count == 0) return 0;
        final var maxWoolSize = maxWoolSize();
        var maxAdd = maxWoolSize - woolCount();
        if (maxAdd < 0) {
            removeWool(-maxAdd);
        }
        var event = new UserAddWoolEvent(this, count, dropIfFull);
        woolbattle.eventManager().call(event);
        if (event.cancelled()) return 0;
        maxAdd = maxWoolSize - woolCount(); // Do this again in case some idiot (me) tries to change the woolCount in the EventHandler
        if (maxAdd < 0) {
            removeWool(-maxAdd);
            maxAdd = 0;
        }
        var addCount = Math.min(event.amount(), maxAdd);
        var dropCount = event.amount() - addCount;
        woolCount(woolCount + addCount);
        if (event.dropRemaining()) {
            var world = this.world();
            var location = this.location;
            var team = this.team;
            if (world != null && location != null && team != null && world == location.world()) {
                world.dropAt(location.x(), location.y(), location.z(), team.wool(), dropCount);
            }
        }
        return addCount;
    }

    @Override
    public int removeWool(int count, boolean updateInventory) {
        if (count == 0) return 0;
        var event = new UserRemoveWoolEvent(this, count);
        woolbattle.eventManager().call(event);
        if (event.cancelled()) return 0;
        var removeCount = Math.min(woolCount(), event.amount());
        woolCount(woolCount() - removeCount);
        if (updateInventory) {
            platformAccess.woolCount(count);
        }
        return removeCount;
    }

    @Override
    public @Nullable CommonGame game() {
        return game;
    }

    @Override
    public @NotNull UUID uniqueId() {
        return user().uniqueId();
    }

    @Override
    public @NotNull String playerName() {
        return user().name();
    }

    @Override
    public @NotNull Component teamPlayerName() {
        var team = team();
        if (team == null) return Component.text(playerName());
        return Component.text(playerName(), team.nameColor());
    }

    @Override
    public @Nullable CommonTeam team() {
        return team;
    }

    public void clearTeam() {
        this.team0(null);
    }

    @Override
    public void team(@NotNull Team team) {
        this.team0(team);
    }

    private void team0(@Nullable Team team) {
        if (this.team == team) return;
        if (team != null && !(team instanceof CommonTeam)) throw new IllegalArgumentException("Team must be created with CommonTeamManager");
        var c = (CommonTeam) team;
        var oldTeam = this.team;
        if (oldTeam != null) oldTeam.usersModifiable().remove(this);
        this.team = c;
        if (c != null) {
            c.usersModifiable().add(this);
        }
        woolbattle.eventManager().call(new UserChangeTeamEvent(this, oldTeam, team));
        woolbattle.woolbattle().broadcastTeamUpdate(this, oldTeam, c);
    }

    @Override
    public @Nullable CommonWorld world() {
        var l = location;
        return l == null ? null : (CommonWorld) l.world();
    }

    @Override
    public @Nullable Location location() {
        return location;
    }

    @ApiStatus.Internal
    public void location(@Nullable Location location) {
        this.location = location;
    }

    public void teleport(@NotNull Location location) {
        location(location);
        platformAccess.teleport(location);
    }

    public void teleport(@NotNull Position.Directed position) {
        if (position instanceof Location location) {
            teleport(location);
        } else {
            var l = location;
            if (l == null) throw new IllegalStateException("Can't perform teleport: location is null");
            teleport(new Location(l.world(), position));
        }
    }

    @Override
    public @NotNull PerksStorage perksStorage() {
        return user.persistentData().get(keyPerks, CommonPerksStorage.TYPE, CommonPerksStorage::new).clone();
    }

    @Override
    public void perksStorage(@NotNull PerksStorage perks) {
        user.persistentData().set(keyPerks, CommonPerksStorage.TYPE, perks);
    }

    @Override
    public @NotNull WoolSubtractDirection woolSubtractDirection() {
        return user.persistentData().get(keyWoolSubtractDirection, WoolSubtractDirection.TYPE, WoolSubtractDirection::getDefault);
    }

    @Override
    public void woolSubtractDirection(@NotNull WoolSubtractDirection woolSubtractDirection) {
        if (woolSubtractDirection() == woolSubtractDirection) return;
        user.persistentData().set(keyWoolSubtractDirection, WoolSubtractDirection.TYPE, woolSubtractDirection);
        woolbattle.eventManager().call(new UserWoolSubtractDirectionUpdateEvent(this, woolSubtractDirection));
    }

    @Override
    public @NotNull HeightDisplay heightDisplay() {
        return user.persistentData().get(keyHeightDisplay, HeightDisplay.TYPE, HeightDisplay::getDefault).clone();
    }

    @Override
    public void heightDisplay(@NotNull HeightDisplay heightDisplay) {
        user.persistentData().set(keyHeightDisplay, HeightDisplay.TYPE, heightDisplay);
    }

    @Override
    public @NotNull CommonUserPerks perks() {
        return perks;
    }

    @Override
    public boolean particles() {
        return user.persistentData().get(keyParticles, BOOLEAN, () -> true);
    }

    @Override
    public void particles(boolean particles) {
        user.persistentData().set(keyParticles, BOOLEAN, particles);
        woolbattle.eventManager().call(new UserParticlesUpdateEvent(this, particles));
    }

    @Override
    public void language(Language language) {
        user.language(language);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissions.hasPermission(permission);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public @NotNull UUID playerUniqueId() {
        return uniqueId();
    }

    @Override
    public @NotNull Audience audience() {
        return user;
    }

    public @NotNull UserPlatformAccess platformAccess() {
        return platformAccess;
    }
}
