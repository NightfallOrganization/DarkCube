/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.util.convertingrule;

import org.bukkit.ChatColor;

import de.pixel.bedwars.util.Arrays.ConvertingRule;

public class RuleChatColor extends ConvertingRule<ChatColor> {

	@Override
	public Class<ChatColor> getConvertingClass() {
		return ChatColor.class;
	}

	@Override
	public String convert(ChatColor object) {
		return object.name();
	}

}
