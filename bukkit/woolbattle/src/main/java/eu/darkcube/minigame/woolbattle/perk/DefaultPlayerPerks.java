/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk;

import eu.darkcube.minigame.woolbattle.util.Slot;

public class DefaultPlayerPerks implements PlayerPerks {

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

	public DefaultPlayerPerks() {
		reset();
	}

	@Override
	public void reset() {
		active1 = PerkType.CAPSULE.getPerkName();
		active2 = PerkType.SWITCHER.getPerkName();
		passive = PerkType.EXTRA_WOOL.getPerkName();
		slotActive1 = Slot.Hotbar.SLOT_3;
		slotActive2 = Slot.Hotbar.SLOT_4;
		slotPassive = Slot.Hotbar.SLOT_9;
		slotBow = Slot.Hotbar.SLOT_1;
		slotShears = Slot.Hotbar.SLOT_2;
		slotPearl = Slot.Hotbar.SLOT_5;
		slotArrow = Slot.Inventory.SLOT_9;
	}

	@Override
	public PerkName getActivePerk1() {
		return active1;
	}

	@Override
	public PerkName getActivePerk2() {
		return active2;
	}

	@Override
	public PerkName getPassivePerk() {
		return passive;
	}

	@Override
	public void setActivePerk1(PerkName perk) {
		active1 = perk;
	}

	@Override
	public void setActivePerk2(PerkName perk) {
		active2 = perk;
	}

	@Override
	public void setPassivePerk(PerkName perk) {
		passive = perk;
	}

	@Override
	public int getSlotActivePerk1() {
		return slotActive1;
	}

	@Override
	public int getSlotActivePerk2() {
		return slotActive2;
	}

	@Override
	public int getSlotPassivePerk() {
		return slotPassive;
	}

	@Override
	public void setSlotActivePerk1(int slot) {
		slotActive1 = slot;
	}

	@Override
	public void setSlotActivePerk2(int slot) {
		slotActive2 = slot;
	}

	@Override
	public void setSlotPassivePerk(int slot) {
		slotPassive = slot;
	}

	@Override
	public int getSlotBow() {
		return slotBow;
	}

	@Override
	public int getSlotShears() {
		return slotShears;
	}

	@Override
	public int getSlotPearl() {
		return slotPearl;
	}

	@Override
	public int getSlotArrow() {
		return slotArrow;
	}

	@Override
	public void setSlotBow(int slot) {
		this.slotBow = slot;
	}

	@Override
	public void setSlotShears(int slot) {
		this.slotShears = slot;
	}

	@Override
	public void setSlotPearl(int slot) {
		this.slotPearl = slot;
	}

	@Override
	public void setSlotArrow(int slot) {
		this.slotArrow = slot;
	}
}
