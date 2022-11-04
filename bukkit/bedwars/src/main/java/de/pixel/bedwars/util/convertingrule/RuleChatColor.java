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
