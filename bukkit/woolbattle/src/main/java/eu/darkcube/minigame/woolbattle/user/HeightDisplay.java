/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.user;

import org.bukkit.ChatColor;

public class HeightDisplay {

	public boolean enabled;
	public int maxDistance;
	public char color;

	public HeightDisplay() {
	}

	public HeightDisplay(boolean enabled, int maxDistance, char color) {
		this.enabled = enabled;
		this.maxDistance = maxDistance;
		this.color = color;
	}

	public static HeightDisplay getDefault() {
		return new HeightDisplay(true, -1, ChatColor.GOLD.getChar());
	}

	public int getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public char getColorRaw() {
		return color;
	}

	public ChatColor getColor() {
		return ChatColor.getByChar(color);
	}

	public void setColorRaw(char color) {
		this.color = color;
	}

	public void setColor(ChatColor color) {
		this.color = color.getChar();
	}

}
