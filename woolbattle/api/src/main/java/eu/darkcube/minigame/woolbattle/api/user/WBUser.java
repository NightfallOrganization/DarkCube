/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.user;

import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerks;
import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;

public interface WBUser {
    WoolBattleApi woolbattle();

    /**
     * @return the user from the {@link UserAPI UserAPI}
     */
    User user();

    int woolCount();

    /**
     * Gives the player wool.
     *
     * @param count the amount of wool to add
     * @return the actual amount of wool added
     */
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
    int addWool(int count, boolean dropIfFull);

    /**
     * Takes wool from the player.
     *
     * @param count the amount of wool to take
     * @return the actual amount of wool taken
     */
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
    int removeWool(int count, boolean updateInventory);

    Game game();

    /**
     * @return the UUID for this player
     */
    UUID uniqueId();

    /**
     * @return the player's name
     */
    String playerName();

    /**
     * @return the team color combined with the players name
     */
    Component teamPlayerName();

    /**
     * @return this player's language
     */
    default Language language() {
        return user().language();
    }

    /**
     * @return the team this player is in
     */
    Team team();

    /**
     * Sets the team this player is in
     *
     * @param team the new {@link Team}
     */
    void team(Team team);

    /**
     * @return a copy of this user's {@link PlayerPerks} for storage
     */
    PerksStorage perksStorage();

    /**
     * Set the player's perks. This will only affect storage (which will persist over games), and
     * not the current in-game perks. Use {@link #perks()} for that functionality
     *
     * @param perks the perks to store
     */
    void perksStorage(PerksStorage perks);

    /**
     * Queries the ingame perks.
     * This will <b>ONLY</b> affect the in-game perks but storage will not be updated and the perks will not persist over games.
     * Use {@link #perksStorage()} and {@link #perksStorage(PerksStorage)} for that functionality.
     *
     * @return the in-game perks.
     */
    UserPerks perks();

    boolean particles();

    void particles(boolean particles);
}
