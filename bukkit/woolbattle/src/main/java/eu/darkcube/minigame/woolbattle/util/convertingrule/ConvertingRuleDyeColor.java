package eu.darkcube.minigame.woolbattle.util.convertingrule;

import org.bukkit.DyeColor;

import eu.darkcube.minigame.woolbattle.util.Arrays.ConvertingRule;

public class ConvertingRuleDyeColor extends ConvertingRule<DyeColor> {

	@Override
	public Class<DyeColor> getConvertingClass() {
		return DyeColor.class;
	}

	@Override
	public String convert(DyeColor object) {
		return object.name().toLowerCase();
	}
}