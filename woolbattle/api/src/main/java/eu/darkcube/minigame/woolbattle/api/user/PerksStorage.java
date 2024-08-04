/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.user;

import java.util.Map;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface PerksStorage extends Cloneable {
    @NotNull
    PerksStorage clone();

    /**
     * @param type the {@link ActivationType} to get the perks from
     * @return the perks for the {@link ActivationType}
     */
    @NotNull
    PerkName @NotNull [] perks(@NotNull ActivationType type);

    /**
     * @param type     the {@link ActivationType} to get the perk from
     * @param perkSlot the perkSlot - From 0 (inclusive) -
     *                 {@link ActivationType#maxCount() type.maxCount() (exclusive)}
     * @return the perk at the given index
     * @throws ArrayIndexOutOfBoundsException perkSlot out of range
     */
    @NotNull
    PerkName perk(@NotNull ActivationType type, int perkSlot);

    /**
     * Sets the perk at the given index
     *
     * @param type     the {@link ActivationType} to set the perk
     * @param perkSlot the perkSlot - From 0 (inclusive) -
     *                 {@link ActivationType#maxCount() type.maxCount() (exclusive)}
     * @param perk     the new perk
     * @throws ArrayIndexOutOfBoundsException perkSlot out of range
     */
    void perk(@NotNull ActivationType type, int perkSlot, @NotNull PerkName perk);

    /**
     * @param type the {@link ActivationType} to get the perk slots from
     * @return the perk inventory slots for the {@link ActivationType}
     */
    int @NotNull [] perkInvSlots(@NotNull ActivationType type);

    /**
     * @param type     the {@link ActivationType} to get the perk slot from
     * @param perkSlot the perkSlot - From 0 (inclusive) -
     *                 {@link ActivationType#maxCount() type.maxCount() (exclusive)}
     * @return the perk inventory slot at the given index
     * @throws ArrayIndexOutOfBoundsException perkSlot out of range
     */
    int perkInvSlot(@NotNull ActivationType type, int perkSlot);

    /**
     * Sets the perk inventory slot at the given index
     *
     * @param type     the {@link ActivationType} to set the perk inventory slot
     * @param perkSlot the perkSlot - From 0 (inclusive) -
     *                 {@link ActivationType#maxCount() type.maxCount() (exclusive)}
     * @param slot     the new perk inventory slot
     * @throws ArrayIndexOutOfBoundsException perkSlot out of range
     */
    void perkInvSlot(@NotNull ActivationType type, int perkSlot, int slot);

    @NotNull
    Map<@NotNull ActivationType, @NotNull PerkName @NotNull []> perks();

    void perks(@NotNull Map<@NotNull ActivationType, @NotNull PerkName @NotNull []> perks);

    void reset();
}
