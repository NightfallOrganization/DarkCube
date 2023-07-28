/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.team;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.commandapi.v3.ILanguagedCommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.util.AdventureSupport;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public Component getName(ILanguagedCommandExecutor executor) {
        return Message.getMessage(Message.TEAM_PREFIX + getType().getDisplayNameKey(),
                executor.getLanguage()).style(getPrefixStyle());
    }

    @Override
    public Style getPrefixStyle() {
        return AdventureSupport.convert(ChatColor.getByChar(getType().getNameColor()));
    }

    @Override
    public TeamType getType() {
        return type;
    }

    @Override
    public Collection<? extends WBUser> getUsers() {
        return WBUser.onlineUsers().stream().filter(user -> user.getTeam().equals(this))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean contains(UUID user) {
        for (WBUser u : getUsers()) {
            if (u.getUniqueId().equals(user)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getLifes() {
        return lifes;
    }

    @Override
    public void setLifes(int lifes) {
        this.lifes = lifes;
        WBUser.onlineUsers()
                .forEach(u -> WoolBattle.instance().ingame().reloadScoreboardLifes(u));
    }

    @Override
    public void setSpawn(Map map, Location location) {
        map.ingameData().spawn(getType().getDisplayNameKey(), location);
    }

    @Override
    public Location getSpawn(Map map) {
        return map.ingameData().spawn(getType().getDisplayNameKey());
    }

    @Override
    public Location getSpawn() {
        return getSpawn(WoolBattle.instance().gameData().map());
    }

    @Override
    public void setSpawn(Location location) {
        setSpawn(WoolBattle.instance().gameData().map(), location);
    }
}
