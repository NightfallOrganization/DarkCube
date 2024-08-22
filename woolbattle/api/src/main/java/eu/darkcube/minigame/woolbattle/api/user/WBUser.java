/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.user;

import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.command.CommandSender;
import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerks;
import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.MetaDataStorage;

public interface WBUser extends CommandSender, Entity {
    @Api
    @NotNull
    WoolBattleApi api();

    /**
     * @return the user from the {@link UserAPI UserAPI}
     */
    @Api
    @NotNull
    User user();

    @Override
    @Api
    @NotNull
    MetaDataStorage metadata();

    /**
     * @return the amount of wool the user can carry.
     */
    @Api
    int maxWoolSize();

    /**
     * @return the amount of wool added when breaking a block.
     */
    @Api
    int woolBreakAmount();

    @Api
    int woolCount();

    @Api
    void woolCount(int woolCount);

    /**
     * Gives the player wool.
     *
     * @param count the amount of wool to add
     * @return the actual amount of wool added
     */
    @Api
    default int addWool(int count) {
        return addWool(count, true);
    }

    /**
     * Gives the player wool.
     *
     * @param count      the amount of wool to add
     * @param dropIfFull whether the wool should drop on full inventory
     * @return the actual amount of wool added
     */
    @Api
    int addWool(int count, boolean dropIfFull);

    /**
     * Takes wool from the player.
     *
     * @param count the amount of wool to take
     * @return the actual amount of wool taken
     */
    @Api
    default int removeWool(int count) {
        return removeWool(count, true);
    }

    /**
     * Takes wool from the player.
     *
     * @param count           the amount of wool to take
     * @param updateInventory whether the inventory should be affected. This is used to remove wool silently without the player noticing. This can cause the inventory to become out-of-sync. This can also help to get the inventory back in sync.
     * @return the actual amount of wool taken.
     */
    @ApiStatus.Experimental
    @Api
    int removeWool(int count, boolean updateInventory);

    @Api
    @Nullable
    Game game();

    /**
     * @return the UUID for this player
     */
    @Api
    @NotNull
    UUID uniqueId();

    /**
     * @return the player's name
     */
    @Api
    @NotNull
    String playerName();

    /**
     * @return the team color combined with the players name
     */
    @Api
    @NotNull
    Component teamPlayerName();

    /**
     * @return this player's language
     */
    @Override
    @Api
    default @NotNull Language language() {
        return user().language();
    }

    /**
     * @return the team this player is in
     */
    @Api
    @Nullable
    Team team();

    /**
     * Sets the team this player is in
     *
     * @param team the new {@link Team}
     */
    @Api
    void team(@NotNull Team team);

    @Api
    @Nullable
    World world();

    @Override
    @Api
    @Nullable
    Location location();

    @Override
    @Api
    @Nullable
    Location eyeLocation();

    /**
     * @return a copy of this user's {@link PerksStorage}
     */
    @Api
    @NotNull
    PerksStorage perksStorage();

    /**
     * Set the player's perks. This will only affect storage (which will persist over games), and
     * not the current in-game perks. Use {@link #perks()} for that functionality
     *
     * @param perks the perks to store
     */
    @Api
    void perksStorage(@NotNull PerksStorage perks);

    @Api
    @NotNull
    WoolSubtractDirection woolSubtractDirection();

    @Api
    void woolSubtractDirection(@NotNull WoolSubtractDirection woolSubtractDirection);

    @Api
    @NotNull
    HeightDisplay heightDisplay();

    @Api
    void heightDisplay(@NotNull HeightDisplay heightDisplay);

    /**
     * Queries the ingame perks.
     * This will <b>ONLY</b> affect the in-game perks but storage will not be updated and the perks will not persist over games.
     * Use {@link #perksStorage()} and {@link #perksStorage(PerksStorage)} for that functionality.
     *
     * @return the in-game perks.
     */
    @Api
    @NotNull
    UserPerks perks();

    @Api
    boolean particles();

    @Api
    void particles(boolean particles);

    /**
     * Checks whether the user can see another user. This state should be aware of game state and team.
     *
     * @param other the other user
     * @return if this user can see the other user
     */
    @Api
    boolean canSee(@NotNull WBUser other);

    void kick();
}
