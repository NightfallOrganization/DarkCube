/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.convertingrule;

import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.util.Arrays.ConvertingRule;

public class ConvertingRuleTeam extends ConvertingRule<Team> {

    @Override
    public Class<Team> getConvertingClass() {
        return Team.class;
    }

    @Override
    public String convert(Team object) {
        return object.getType().getDisplayNameKey();
    }
}
