/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.user;

import eu.darkcube.minigame.woolbattle.gadget.Gadget;
import eu.darkcube.minigame.woolbattle.perk.DefaultPlayerPerks;
import eu.darkcube.minigame.woolbattle.perk.PlayerPerks;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer.DontSerialize;
import eu.darkcube.minigame.woolbattle.util.WoolSubtractDirection;
import eu.darkcube.system.language.core.Language;

public class DefaultUserData implements UserData {

	@DontSerialize
	private Language language = Language.DEFAULT;
	private Gadget gadget = Gadget.HOOK_ARROW;
	private WoolSubtractDirection woolSubtractDirection = WoolSubtractDirection.getDefault();
	private DefaultPlayerPerks perks = new DefaultPlayerPerks();
	private boolean particles = false;
	private HeightDisplay display = HeightDisplay.getDefault();

	@Override
	public Language getLanguage() {
		return language;
	}

	@Override
	public PlayerPerks getPerks() {
		return perks;
	}

	@Override
	public Gadget getGadget() {
		return gadget;
	}

	@Override
	public void setGadget(Gadget gadget) {
		this.gadget = gadget;
	}

	@Override
	public void setLanguage(Language language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return serialize();
	}

	@Override
	public boolean isParticles() {
		return particles;
	}

	@Override
	public void setParticles(boolean particles) {
		this.particles = particles;
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
	public WoolSubtractDirection getWoolSubtractDirection() {
		return woolSubtractDirection;
	}

	@Override
	public void setWoolSubtractDirection(WoolSubtractDirection dir) {
		this.woolSubtractDirection = dir;
	}
}
