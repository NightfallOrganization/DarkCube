/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.team;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;

class DefaultTeam implements Team {

	private UUID uuid;
	private TeamType type;
	private int lifes;

	public DefaultTeam(TeamType type) {
		this.uuid = UUID.randomUUID();
		this.type = type;
	}

	@Override
	public int compareTo(Team o) {
		return getType().compareTo(o.getType());
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public boolean isSpectator() {
		return getType().equals(TeamType.SPECTATOR);
	}

	@Override
	public boolean canPlay() {
		return !isSpectator();
	}

	@Override
	public String getName(User user) {
		return getPrefix()
//		return Message.getMessage(getType().getDisplayNameKey(), user.getLanguage())
				+ Message.getMessage(Message.TEAM_PREFIX + getType().getDisplayNameKey(), user.getLanguage());
	}

	@Override
	public TeamType getType() {
		return type;
	}

	@Override
	public Collection<? extends User> getUsers() {
		return WoolBattle.getInstance().getUserWrapper().getUsers().stream().filter(user -> user.getTeam().equals(this))
				.collect(Collectors.toSet());
	}

	@Override
	public boolean contains(UUID user) {
		for (User u : getUsers()) {
			if (u.getUniqueId().equals(user)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setLifes(int lifes) {
		this.lifes = lifes;
		WoolBattle m = WoolBattle.getInstance();
		m.getUserWrapper().getUsers().forEach(u -> m.getIngame().reloadScoreboardLifes(u));
	}

	@Override
	public int getLifes() {
		return lifes;
	}

	@Override
	public void setSpawn(Map map, Location location) {
		map.setSpawn(getType().getDisplayNameKey(), location);
	}

	@Override
	public void setSpawn(Location location) {
		setSpawn(WoolBattle.getInstance().getMap(), location);
	}

	@Override
	public Location getSpawn(Map map) {
		return map.getSpawn(getType().getDisplayNameKey());
	}

	@Override
	public Location getSpawn() {
		return getSpawn(WoolBattle.getInstance().getMap());
	}

	@Override
	public String getPrefix() {
		return ChatColor.getByChar(getType().getNameColor()).toString();
	}
}
