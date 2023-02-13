/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.team;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.util.Language;

import java.util.Collection;
import java.util.UUID;

public interface TeamManager {

	Team getTeam(UUID id);
	Team getTeam(String displayNameKey);
	Team getTeam(TeamType type);
	Team getTeam(String displayName, Language inLanguage);
	Team getOrCreateTeam(TeamType type);
	Team getSpectator();
	
	Team getTeam(WBUser user);
	void setTeam(WBUser user, Team team);
	
	Collection<? extends Team> getTeams();
}
