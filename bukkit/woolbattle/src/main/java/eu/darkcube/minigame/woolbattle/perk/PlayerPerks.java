/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk;

public interface PlayerPerks {

	PerkName getActivePerk1();

	PerkName getActivePerk2();

	PerkName getPassivePerk();

	int getSlotActivePerk1();

	int getSlotActivePerk2();

	int getSlotPassivePerk();

	int getSlotBow();

	int getSlotShears();

	int getSlotPearl();

	int getSlotArrow();

	void setSlotBow(int slot);

	void setSlotShears(int slot);

	void setSlotPearl(int slot);

	void setSlotArrow(int slot);

	void setSlotActivePerk1(int slot);

	void setSlotActivePerk2(int slot);

	void setSlotPassivePerk(int slot);

	void setActivePerk1(PerkName perk);

	void setActivePerk2(PerkName perk);

	void setPassivePerk(PerkName perk);

	void reset();

}
