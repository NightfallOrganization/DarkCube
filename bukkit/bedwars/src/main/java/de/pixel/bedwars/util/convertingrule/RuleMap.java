package de.pixel.bedwars.util.convertingrule;

import de.pixel.bedwars.map.Map;
import de.pixel.bedwars.util.Arrays.ConvertingRule;

public class RuleMap extends ConvertingRule<Map> {

	@Override
	public Class<Map> getConvertingClass() {
		return Map.class;
	}

	@Override
	public String convert(Map object) {
		return object.getName();
	}

}
