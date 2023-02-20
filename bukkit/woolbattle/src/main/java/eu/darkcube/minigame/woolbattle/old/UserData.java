/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.old;

import eu.darkcube.minigame.woolbattle.user.HeightDisplay;
import eu.darkcube.minigame.woolbattle.util.Serializable;
import eu.darkcube.minigame.woolbattle.util.WoolSubtractDirection;

public interface UserData extends Serializable {

	PlayerPerksOld getPerks();

	WoolSubtractDirection getWoolSubtractDirection();

	void setWoolSubtractDirection(WoolSubtractDirection dir);

	HeightDisplay getHeightDisplay();

	void setHeightDisplay(HeightDisplay display);

	boolean isParticles();
}
