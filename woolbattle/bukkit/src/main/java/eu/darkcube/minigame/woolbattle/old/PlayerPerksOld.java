/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.old;

import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.active.CapsulePerk;
import eu.darkcube.minigame.woolbattle.perk.perks.active.SwitcherPerk;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.ExtraWoolPerk;
import eu.darkcube.minigame.woolbattle.util.Slot;

public class PlayerPerksOld {

    private PerkName active1;
    private PerkName active2;
    private PerkName passive;
    private int slotActive1;
    private int slotActive2;
    private int slotPassive;
    private int slotBow;
    private int slotShears;
    private int slotArrow;
    private int slotPearl;

    public PlayerPerksOld() {
        reset();
    }

    public void reset() {
        active1 = CapsulePerk.CAPSULE;
        active2 = SwitcherPerk.SWITCHER;
        passive = ExtraWoolPerk.EXTRA_WOOL;
        slotActive1 = Slot.Hotbar.SLOT_3;
        slotActive2 = Slot.Hotbar.SLOT_4;
        slotPassive = Slot.Hotbar.SLOT_9;
        slotBow = Slot.Hotbar.SLOT_1;
        slotShears = Slot.Hotbar.SLOT_2;
        slotPearl = Slot.Hotbar.SLOT_5;
        slotArrow = Slot.Inventory.SLOT_9;
    }

    public PerkName getActivePerk1() {
        return active1;
    }

    public void setActivePerk1(PerkName perk) {
        active1 = perk;
    }

    public PerkName getActivePerk2() {
        return active2;
    }

    public void setActivePerk2(PerkName perk) {
        active2 = perk;
    }

    public PerkName getPassivePerk() {
        return passive;
    }

    public void setPassivePerk(PerkName perk) {
        passive = perk;
    }

    public int getSlotActivePerk1() {
        return slotActive1;
    }

    public void setSlotActivePerk1(int slot) {
        slotActive1 = slot;
    }

    public int getSlotActivePerk2() {
        return slotActive2;
    }

    public void setSlotActivePerk2(int slot) {
        slotActive2 = slot;
    }

    public int getSlotPassivePerk() {
        return slotPassive;
    }

    public void setSlotPassivePerk(int slot) {
        slotPassive = slot;
    }

    public int getSlotBow() {
        return slotBow;
    }

    public void setSlotBow(int slot) {
        this.slotBow = slot;
    }

    public int getSlotShears() {
        return slotShears;
    }

    public void setSlotShears(int slot) {
        this.slotShears = slot;
    }

    public int getSlotPearl() {
        return slotPearl;
    }

    public void setSlotPearl(int slot) {
        this.slotPearl = slot;
    }

    public int getSlotArrow() {
        return slotArrow;
    }

    public void setSlotArrow(int slot) {
        this.slotArrow = slot;
    }
}
