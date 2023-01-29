/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.user;

import eu.darkcube.minigame.woolbattle.gadget.Gadget;
import eu.darkcube.minigame.woolbattle.perk.PlayerPerks;
import eu.darkcube.minigame.woolbattle.util.Serializable;
import eu.darkcube.minigame.woolbattle.util.WoolSubtractDirection;
import eu.darkcube.system.util.Language;

public interface UserData extends Serializable {

	Language getLanguage();

	PlayerPerks getPerks();

	Gadget getGadget();
	
	WoolSubtractDirection getWoolSubtractDirection();
	
	void setWoolSubtractDirection(WoolSubtractDirection dir);
	
	HeightDisplay getHeightDisplay();
	
	void setHeightDisplay(HeightDisplay display);

	void setGadget(Gadget gadget);

	void setLanguage(Language language);

	boolean isParticles();

	void setParticles(boolean particles);
}
