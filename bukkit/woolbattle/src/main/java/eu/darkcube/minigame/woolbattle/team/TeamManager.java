/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.team;

import java.util.Collection;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.system.language.core.Language;

public interface TeamManager {

	Team getTeam(UUID id);
	Team getTeam(String displayNameKey);
	Team getTeam(TeamType type);
	Team getTeam(String displayName, Language inLanguage);
	Team getOrCreateTeam(TeamType type);
	Team getSpectator();
	
	Team getTeam(User user);
	void setTeam(User user, Team team);
	
	Collection<? extends Team> getTeams();
}
