package eu.darkcube.minigame.woolbattle.util.convertingrule;

import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.util.Arrays.ConvertingRule;

public class ConvertingRuleMap extends ConvertingRule<Map> {

	@Override
	public Class<Map> getConvertingClass() {
		return Map.class;
	}

	@Override
	public String convert(Map object) {
		return object.getName();
	}

}
