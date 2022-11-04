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
