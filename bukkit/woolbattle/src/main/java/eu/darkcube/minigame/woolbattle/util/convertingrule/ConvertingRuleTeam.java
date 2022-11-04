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