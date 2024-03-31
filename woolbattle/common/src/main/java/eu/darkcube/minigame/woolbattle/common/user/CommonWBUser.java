package eu.darkcube.minigame.woolbattle.common.user;

import static eu.darkcube.system.util.data.PersistentDataTypes.BOOLEAN;

import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.event.user.UserAddWoolEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserGetMaxWoolSizeEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserGetWoolBreakAmountEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserRemoveWoolEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserWoolCountUpdateEvent;
import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.api.user.HeightDisplay;
import eu.darkcube.minigame.woolbattle.api.user.PerksStorage;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.user.WoolSubtractDirection;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.perk.user.CommonUserPerks;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.BasicMetaDataStorage;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.MetaDataStorage;

public class CommonWBUser implements WBUser {
    private final @NotNull Key keyParticles;
    private final @NotNull Key keyHeightDisplay;
    private final @NotNull Key keyWoolSubtractDirection;
    private final @NotNull Key keyPerks;
    private final @NotNull CommonWoolBattleApi woolbattle;
    private final @NotNull User user;
    private final @Nullable CommonGame game;
    private final @NotNull CommonUserPerks perks;
    private final @NotNull UserInventoryAccess inventoryAccess;
    private final @NotNull UserPermissions permissions;
    private final @NotNull MetaDataStorage metadata = new BasicMetaDataStorage();
    private volatile @Nullable CommonWorld world;
    private volatile @Nullable Location location;
    private volatile int woolCount;
    private volatile @Nullable CommonTeam team;

    public CommonWBUser(@NotNull CommonWoolBattleApi woolbattle, @NotNull User user, @Nullable CommonGame game) {
        this.woolbattle = woolbattle;
        this.user = user;
        this.game = game;
        this.perks = new CommonUserPerks(this);
        this.keyParticles = new Key(woolbattle, "particles");
        this.keyHeightDisplay = new Key(woolbattle, "heightDisplay");
        this.keyWoolSubtractDirection = new Key(woolbattle, "woolSubtractDirection");
        this.keyPerks = new Key(woolbattle, "perks");
        this.inventoryAccess = woolbattle.woolbattle().createInventoryAccessFor(this);
        this.permissions = woolbattle.woolbattle().createPermissionsFor(this);
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
        inventoryAccess.woolCount(woolCount);
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
            var world = this.world;
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
            inventoryAccess.woolCount(count);
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
        return Component.text(playerName()).style(team.nameStyle());
    }

    @Override
    public @Nullable CommonTeam team() {
        return team;
    }

    @Override
    public void team(@NotNull Team team) {
        if (!(team instanceof CommonTeam c)) throw new IllegalArgumentException("Team must be created with CommonTeamManager");
        var oldTeam = this.team;
        this.team = c;
        woolbattle.woolbattle().broadcastTeamUpdate(this, oldTeam, c);
    }

    @Override
    public @Nullable CommonWorld world() {
        return world;
    }

    public void world(@Nullable CommonWorld world) {
        this.world = world;
    }

    @Override
    public @Nullable Location location() {
        return location;
    }

    public void location(@Nullable Location location) {
        this.location = location;
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
        user.persistentData().set(keyWoolSubtractDirection, WoolSubtractDirection.TYPE, woolSubtractDirection);
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
}
