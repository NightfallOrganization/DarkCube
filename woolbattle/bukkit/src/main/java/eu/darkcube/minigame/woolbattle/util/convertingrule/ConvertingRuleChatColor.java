/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.convertingrule;

import eu.darkcube.minigame.woolbattle.util.Arrays.ConvertingRule;
import org.bukkit.ChatColor;

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
