/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.team;

import java.util.Collection;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

public interface Team {
    @NotNull TeamType type();

    default boolean spectator() {
        return type() == TeamType.SPECTATOR;
    }

    default boolean canPlay() {
        return type() == TeamType.PLAYER;
    }

    /**
     * Gets the translated name of this team for the specific executor.
     *
     * @param executor the executor which language to use
     * @return a translated name
     */
    @NotNull Component getName(@NotNull CommandExecutor executor);

    @NotNull Style prefixStyle();

    @Unmodifiable @NotNull Collection<WBUser> users();

    int lifes();

    void lifes(int lifes);

    /**
     * Adds lifes to this team.
     *
     * @param count the amount of lifes to add
     * @return how many lifes the team now has
     */
    int addLifes(int count);

    /**
     * Removes lifes from this team.
     *
     * @param count the amount of lifes to remove
     * @return how many lifes the team now has
     */
    int removeLifes(int count);
}
