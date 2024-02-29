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
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.Language;

public interface WBUser {
    WoolBattleApi woolbattle();

    /**
     * @return the user from the {@link eu.darkcube.system.userapi.UserAPI UserAPI}
     */
    User user();

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
