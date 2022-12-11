/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
