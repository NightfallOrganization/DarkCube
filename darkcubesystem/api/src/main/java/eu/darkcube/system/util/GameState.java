/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

public enum GameState {
	
	LOBBY,
	INGAME,
	STOPPING,
	UNKNOWN
	
	;
	
	public static GameState fromString(String gameState) {
		for(GameState state : values()) {
			if(state.toString().equals(gameState)) {
				return state;
			}
		}
		return GameState.UNKNOWN;
	}
	
	@Override
	public String toString() {
		return super.name();
	}
}
