/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system;

import de.dytanic.cloudnet.driver.service.property.DefaultJsonServiceProperty;
import de.dytanic.cloudnet.driver.service.property.ServiceProperty;
import eu.darkcube.system.util.GameState;

public class DarkCubeServiceProperty {

	public static final ServiceProperty<Boolean> AUTOCONFIGURED =
			DefaultJsonServiceProperty.createFromClass("autoconfigured", Boolean.class);
	public static final ServiceProperty<GameState> GAME_STATE =
			DefaultJsonServiceProperty.createFromClass("gameState", GameState.class)
					.forbidModification();
	public static final ServiceProperty<Integer> PLAYING_PLAYERS =
			DefaultJsonServiceProperty.createFromClass("playingPlayers", Integer.class)
					.forbidModification();
	public static final ServiceProperty<Integer> SPECTATING_PLAYERS =
			DefaultJsonServiceProperty.createFromClass("spectatingPlayers", Integer.class)
					.forbidModification();
	public static final ServiceProperty<Integer> MAX_PLAYING_PLAYERS =
			DefaultJsonServiceProperty.createFromClass("maxPlayingPlayers", Integer.class)
					.forbidModification();
	public static final ServiceProperty<String> DISPLAY_NAME =
			DefaultJsonServiceProperty.createFromClass("displayName", String.class)
					.forbidModification();

}
