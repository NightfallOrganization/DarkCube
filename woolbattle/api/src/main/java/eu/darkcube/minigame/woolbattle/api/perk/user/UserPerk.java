/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.perk.user;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public interface UserPerk {
    @NotNull Game game();

    /**
     * @return a {@code unique id} for each user for each perk. This {@link UserPerk perk} is
     * identifiable by the {@code id} and no other {@link UserPerk perk} is. A user even might have
     * the same {@link Perk} twice, and they will have different {@code id}s.
     * @see UserPerks#perk(int)
     */
    int id();

    /**
     * @return the {@link Perk} associated with this {@link UserPerk}
     */
    @NotNull Perk perk();

    /**
     * @return the current item for this perk. May change for cooldown or other events at any time
     */
    @NotNull PerkItem currentPerkItem();

    /**
     * Sets the perk to the specified {@code slot}
     * <br>This will update the players inventory if necessary
     *
     * @param slot the slot
     */
    void slot(int slot);

    /**
     * @return the current slot for this perk
     */
    int slot();

    /**
     * @return the current cooldown in ticks for this perk
     */
    int cooldown();

    /**
     * Sets the cooldown for this user's perk. This will also automatically count down
     *
     * @param cooldown the cooldown in ticks
     */
    void cooldown(int cooldown);

    void slotSilent(int slot);

    @NotNull WBUser owner();

}
