/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import eu.darkcube.system.util.GameState;
import org.jetbrains.annotations.NotNull;

public class MinigameServerSortingInfo implements Comparable<MinigameServerSortingInfo> {

	public int onPlayers;
	public int maxPlayers;
	public GameState state;

	public MinigameServerSortingInfo(int onPlayers, int maxPlayers, GameState state) {
		this.onPlayers = onPlayers;
		this.maxPlayers = maxPlayers;
		this.state = state;
	}

	@Override
	public int compareTo(@NotNull MinigameServerSortingInfo other) {
		int amt = 0;
		switch (this.state) {
			case LOBBY:
				if (other.state != GameState.LOBBY)
					return -1;
				amt = Integer.compare(other.onPlayers, this.onPlayers);
				break;
			case INGAME:
				if (other.state == GameState.LOBBY)
					return 1;
				if (other.state != GameState.INGAME)
					return -1;
				amt = Integer.compare(other.onPlayers, this.onPlayers);
				break;
			default:
				if (other.state != GameState.UNKNOWN)
					return 1;
				amt = Integer.compare(other.onPlayers, this.onPlayers);
				break;
		}
		if (amt == 0) {
			amt = Integer.compare(other.onPlayers, this.onPlayers);
			if (amt != 0) {
				return amt;
			}
			amt = Integer.compare(this.maxPlayers, other.maxPlayers);
			return amt;
		}
		return amt;
	}
}
