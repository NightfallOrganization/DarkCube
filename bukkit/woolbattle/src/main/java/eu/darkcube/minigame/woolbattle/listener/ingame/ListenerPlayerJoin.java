/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import eu.darkcube.minigame.woolbattle.*;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.*;

public class ListenerPlayerJoin extends Listener<PlayerJoinEvent> {
	@Override
	@EventHandler
	public void handle(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		final User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
		p.setGameMode(GameMode.SURVIVAL);
		p.resetMaxHealth();
		p.resetPlayerTime();
		p.resetPlayerWeather();
		p.setAllowFlight(true);
		p.setExhaustion(0);
		p.setExp(0);
		p.setLevel(0);
		p.setFoodLevel(20);
		p.setHealth(20);
		p.setItemOnCursor(null);
		p.setSaturation(0);
		e.setJoinMessage(null);
		user.setTicksAfterLastHit(1200);
		WoolBattle.getInstance().getTeamManager().setTeam(user, WoolBattle.getInstance().getTeamManager().getSpectator());
//		ingame.setSpectator(user);
	}
}
