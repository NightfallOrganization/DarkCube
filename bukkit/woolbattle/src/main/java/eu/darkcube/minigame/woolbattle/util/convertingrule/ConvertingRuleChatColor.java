/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.convertingrule;

import org.bukkit.ChatColor;

import eu.darkcube.minigame.woolbattle.util.Arrays.ConvertingRule;

public class ConvertingRuleChatColor extends ConvertingRule<ChatColor> {

	@Override
	public Class<ChatColor> getConvertingClass() {
		return ChatColor.class;
	}

	@Override
	public String convert(ChatColor object) {
		return object.name().toLowerCase();
	}

}
