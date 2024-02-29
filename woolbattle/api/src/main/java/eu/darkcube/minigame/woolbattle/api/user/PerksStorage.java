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

public interface PerksStorage extends Cloneable {
    PerksStorage clone();

    /**
     * @param type the {@link ActivationType} to get the perks from
     * @return the perks for the {@link ActivationType}
     */
    PerkName[] perks(ActivationType type);

    /**
     * @param type     the {@link ActivationType} to get the perk from
     * @param perkSlot the perkSlot - From 0 (inclusive) -
     *                 {@link ActivationType#maxCount() type.maxCount() (exclusive)}
     * @return the perk at the given index
     * @throws ArrayIndexOutOfBoundsException perkSlot out of range
     */
    PerkName perk(ActivationType type, int perkSlot);

    /**
     * Sets the perk at the given index
     *
     * @param type     the {@link ActivationType} to set the perk
     * @param perkSlot the perkSlot - From 0 (inclusive) -
     *                 {@link ActivationType#maxCount() type.maxCount() (exclusive)}
     * @param perk     the new perk
     * @throws ArrayIndexOutOfBoundsException perkSlot out of range
     */
    void perk(ActivationType type, int perkSlot, PerkName perk);

    /**
     * @param type the {@link ActivationType} to get the perk slots from
     * @return the perk inventory slots for the {@link ActivationType}
     */
    int[] perkInvSlots(ActivationType type);

    /**
     * @param type     the {@link ActivationType} to get the perk slot from
     * @param perkSlot the perkSlot - From 0 (inclusive) -
     *                 {@link ActivationType#maxCount() type.maxCount() (exclusive)}
     * @return the perk inventory slot at the given index
     * @throws ArrayIndexOutOfBoundsException perkSlot out of range
     */
    int perkInvSlot(ActivationType type, int perkSlot);

    /**
     * Sets the perk inventory slot at the given index
     *
     * @param type     the {@link ActivationType} to set the perk inventory slot
     * @param perkSlot the perkSlot - From 0 (inclusive) -
     *                 {@link ActivationType#maxCount() type.maxCount() (exclusive)}
     * @param slot     the new perk inventory slot
     * @throws ArrayIndexOutOfBoundsException perkSlot out of range
     */
    void perkInvSlot(ActivationType type, int perkSlot, int slot);

    Map<ActivationType, PerkName[]> perks();

    void perks(Map<ActivationType, PerkName[]> perks);

    void reset();
}
