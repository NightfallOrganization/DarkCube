/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.util.convertingrule;

import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.Arrays.ConvertingRule;

public class RuleTeam extends ConvertingRule<Team> {

	@Override
	public Class<Team> getConvertingClass() {
		return Team.class;
	}

	@Override
	public String convert(Team object) {
		return object.getTranslationName();
	}
}
