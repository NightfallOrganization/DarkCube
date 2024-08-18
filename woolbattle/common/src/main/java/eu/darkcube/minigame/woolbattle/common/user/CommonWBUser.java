/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.user;

import static eu.darkcube.system.libs.net.kyori.adventure.key.Key.key;
import static eu.darkcube.system.util.data.DataKey.of;
import static eu.darkcube.system.util.data.PersistentDataTypes.BOOLEAN;

import java.util.Objects;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.minigame.woolbattle.api.event.user.UserAddWoolEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserChangeTeamEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserGetMaxWoolSizeEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserGetWoolBreakAmountEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserParticlesUpdateEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserRemoveWoolEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserWoolCountUpdateEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserWoolSubtractDirectionUpdateEvent;
import eu.darkcube.minigame.woolbattle.api.game.ingame.Ingame;
import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.api.user.HeightDisplay;
import eu.darkcube.minigame.woolbattle.api.user.PerksStorage;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.user.WoolSubtractDirection;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.perk.user.CommonPerksStorage;
import eu.darkcube.minigame.woolbattle.common.perk.user.CommonUserPerks;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextDecoration;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.BasicMetaDataStorage;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.MetaDataStorage;

public class CommonWBUser implements WBUser, ForwardingAudience.Single {
    private final @NotNull DataKey<Boolean> keyParticles;
    private final @NotNull DataKey<HeightDisplay> keyHeightDisplay;
    private final @NotNull DataKey<WoolSubtractDirection> keyWoolSubtractDirection;
    private final @NotNull DataKey<CommonPerksStorage> keyPerks;
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
        this.keyParticles = of(key(woolbattle, "particles"), BOOLEAN);
        this.keyHeightDisplay = of(key(woolbattle, "height_display"), HeightDisplay.TYPE);
        this.keyWoolSubtractDirection = of(key(woolbattle, "wool_subtract_direction"), WoolSubtractDirection.TYPE);
        this.keyPerks = of(key(woolbattle, "perks"), CommonPerksStorage.TYPE);
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
    public @NotNull EntityType<? extends Entity> type() {
        return EntityType.PLAYER;
    }

    @Override
    public boolean isAlive() {
        return platformAccess.isAlive();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Component getName() {
        return teamPlayerName();
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
        if (team == null) return Component.text(playerName(), NamedTextColor.GRAY).decorate(TextDecoration.ITALIC);
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

    @Override
    public void velocity(@NotNull Vector velocity) {
        platformAccess.velocity(velocity);
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
    public @NotNull CommonPerksStorage perksStorage() {
        var game = Objects.requireNonNull(this.game);
        return user.persistentData().get(keyPerks, () -> new CommonPerksStorage(game.perkRegistry())).clone();
    }

    @Override
    public void perksStorage(@NotNull PerksStorage perks) {
        user.persistentData().set(keyPerks, (CommonPerksStorage) perks);
    }

    @Override
    public @NotNull WoolSubtractDirection woolSubtractDirection() {
        return user.persistentData().get(keyWoolSubtractDirection, WoolSubtractDirection::getDefault);
    }

    @Override
    public void woolSubtractDirection(@NotNull WoolSubtractDirection woolSubtractDirection) {
        if (woolSubtractDirection() == woolSubtractDirection) return;
        user.persistentData().set(keyWoolSubtractDirection, woolSubtractDirection);
        woolbattle.eventManager().call(new UserWoolSubtractDirectionUpdateEvent(this, woolSubtractDirection));
    }

    @Override
    public @NotNull HeightDisplay heightDisplay() {
        return user.persistentData().get(keyHeightDisplay, HeightDisplay::getDefault).clone();
    }

    @Override
    public void heightDisplay(@NotNull HeightDisplay heightDisplay) {
        user.persistentData().set(keyHeightDisplay, heightDisplay);
    }

    @Override
    public @NotNull CommonUserPerks perks() {
        return perks;
    }

    @Override
    public boolean particles() {
        return user.persistentData().get(keyParticles, () -> true);
    }

    @Override
    public void particles(boolean particles) {
        user.persistentData().set(keyParticles, particles);
        woolbattle.eventManager().call(new UserParticlesUpdateEvent(this, particles));
    }

    @Override
    public boolean canSee(WBUser other) {
        var game = this.game;
        if (game != other.game()) return false;
        if (game == null) return true;
        if (game.phase() instanceof Ingame) {
            var team = this.team;
            if (team == null) return true;
            if (team.spectator()) return true;
            var otherTeam = other.team();
            if (otherTeam == null) return false;
            return otherTeam.canPlay();
        }
        return true;
    }

    @Override
    public void language(@NotNull Language language) {
        user.language(language);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
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

    public void recalculateAllValues() {
        woolbattle.eventManager().call(new UserParticlesUpdateEvent(this, particles()));
        woolbattle.eventManager().call(new UserWoolSubtractDirectionUpdateEvent(this, woolSubtractDirection()));
        perks.reloadFromStorage();
    }

    @Override
    public String toString() {
        return "WBUser{name=" + playerName() + ", id=" + uniqueId() + "}";
    }
}
