/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.old;

import eu.darkcube.minigame.woolbattle.user.HeightDisplay;
import eu.darkcube.minigame.woolbattle.util.WoolSubtractDirection;

public class DefaultUserData implements UserData {

	private WoolSubtractDirection woolSubtractDirection = WoolSubtractDirection.getDefault();
	private PlayerPerksOld perks = new PlayerPerksOld();
	@SuppressWarnings("FieldCanBeLocal")
	private boolean particles = false;
	private HeightDisplay display = HeightDisplay.getDefault();

	@Override
	public PlayerPerksOld getPerks() {
		return perks;
	}

	@Override
	public WoolSubtractDirection getWoolSubtractDirection() {
		return woolSubtractDirection;
	}

	@Override
	public void setWoolSubtractDirection(WoolSubtractDirection dir) {
		this.woolSubtractDirection = dir;
	}

	@Override
	public HeightDisplay getHeightDisplay() {
		return display;
	}

	@Override
	public void setHeightDisplay(HeightDisplay display) {
		this.display = display;
	}

	@Override
	public boolean isParticles() {
		return particles;
	}

	@Override
	public String toString() {
		return serialize();
	}
}
