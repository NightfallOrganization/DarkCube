/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public class DefaultTeamManager implements TeamManager {

    private final Database database = InjectionLayer.boot().instance(DatabaseProvider.class).database("woolbattle_teams_old");
    private final Collection<Team> teams;
    private final WoolBattleBukkit woolbattle;
    private final Map<MapSize, Collection<TeamType>> teamTypes = new HashMap<>();
    private Team spectator;

    public DefaultTeamManager(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        teams = new HashSet<>();

        loadSpectator();
        loadTeams();
    }

    @Override
    public void save(TeamType teamType) {
        MapSize size = teamType.mapSize();
        if (size == null) throw new IllegalStateException("MapSize null");
        Document document = database.get(size.toString());
        if (document == null) {
            document = Document.newJsonDocument();
            database.insert(size.toString(), document);
        }
        Document.Mutable doc = Document.newJsonDocument();
        doc.append("weight", teamType.getWeight());
        doc.append("woolColor", teamType.getWoolColor().name().toLowerCase(Locale.ROOT));
        doc.append("nameColor", teamType.getNameColor().name().toLowerCase(Locale.ROOT));
        doc.append("enabled", teamType.isEnabled());

        database.insert(size.toString(), document.mutableCopy().append(teamType.getDisplayNameKey(), doc));
    }

    @Override
    public void delete(TeamType teamType) {
        MapSize size = teamType.mapSize();
        if (size == null) throw new IllegalStateException("MapSize null");
        Document document = database.get(size.toString());
        if (document == null) throw new IllegalStateException("Already deleted " + teamType.getDisplayNameKey());
        if (!document.contains(teamType.getDisplayNameKey())) throw new IllegalStateException("Already deleted " + teamType.getDisplayNameKey());
        document = document.mutableCopy().remove(teamType.getDisplayNameKey());
        teamTypes.get(size).remove(teamType);
        if (teamTypes.get(size).isEmpty()) teamTypes.remove(size);
        if (!document.empty()) database.insert(size.toString(), document);
        else database.delete(size.toString());
    }

    private void loadTeam(MapSize mapSize, String key, Document document) {
        int uniqueId = 1;
        int weight = document.getInt("weight");
        DyeColor woolcolor = DyeColor.valueOf(document.getString("woolColor").toUpperCase(Locale.ROOT));
        ChatColor namecolor = ChatColor.valueOf(document.getString("nameColor").toUpperCase(Locale.ROOT));
        boolean enabled = document.getBoolean("enabled");
        w:
        do {
            for (TeamType type : teamTypes.get(mapSize)) {
                if (type.getUniqueId() == uniqueId) {
                    uniqueId++;
                    continue w;
                }
            }
            break;
        } while (true);

        TeamType type = new TeamType(woolbattle, uniqueId, mapSize, key, weight, woolcolor, namecolor, enabled);
        teamTypes.get(mapSize).add(type);
    }

    private void loadTeams() {
        for (Map.Entry<String, Document> entry : database.entries().entrySet()) {
            loadMapSize(entry.getKey(), entry.getValue());
        }
    }

    private void loadMapSize(String sizeString, Document document) {
        MapSize size = MapSize.fromString(sizeString);
        if (size == null) return;
        woolbattle.knownMapSizes().add(size);
        teamTypes.put(size, new ArrayList<>());
        for (String key : document.keys()) loadTeam(size, key, document.readDocument(key));
    }

    private void loadSpectator() {
        int uniqueId = 0;
        String displayNameKey = "spectator";
        int weight = 99;
        DyeColor woolcolor = DyeColor.BLACK;
        ChatColor namecolor = ChatColor.GRAY;
        TeamType spectatorType = new TeamType(woolbattle, uniqueId, null, displayNameKey, weight, woolcolor, namecolor, false);
        spectator = new DefaultTeam(spectatorType, woolbattle);
    }

    @Override
    public Team getTeam(UUID id) {
        for (Team team : teams) {
            if (team.getUniqueId().equals(id)) {
                return team;
            }
        }
        return null;
    }

    @Override
    public Team getTeam(String displayNameKey) {
        Set<Team> teams = getTeams().stream().filter(t -> t.getType().getDisplayNameKey().equals(displayNameKey)).collect(Collectors.toSet());
        for (Team team : teams) return team;
        return null;
    }

    @Override
    public Team getTeam(TeamType type) {
        for (Team team : teams) {
            if (team.getType().equals(type)) {
                return team;
            }
        }
        return spectator;
    }

    @Override
    public Team loadTeam(TeamType type) {
        Team team = new DefaultTeam(type, woolbattle);
        teams.add(team);
        return team;
    }

    @Override
    public void unloadTeam(Team team) {
        teams.remove(team);
    }

    @Override
    public Team getSpectator() {
        return spectator;
    }

    @Override
    public Team getTeam(WBUser user) {
        return user.getTeam();
    }

    @Override
    public void setTeam(WBUser user, Team team) {
        user.setTeam(team);
    }

    @Override
    public Collection<? extends Team> getTeams() {
        return Collections.unmodifiableCollection(teams);
    }

    @Override
    public TeamType create(MapSize mapSize, String displayNameKey, int weight, DyeColor woolcolor, ChatColor namecolor, boolean enabled) {
        int uid = 0;
        Collection<TeamType> values = teamTypes(mapSize);
        w:
        while (true) {
            for (TeamType value : values) {
                if (value.getUniqueId() == uid) {
                    uid++;
                    continue w;
                }
            }
            break;
        }
        TeamType teamType = new TeamType(woolbattle, uid, mapSize, displayNameKey, weight, woolcolor, namecolor, enabled);
        if (!teamTypes.containsKey(mapSize)) teamTypes.put(mapSize, new ArrayList<>());
        teamTypes.get(mapSize).add(teamType);
        save(teamType);
        return teamType;
    }

    @Override
    public TeamType byDisplayNameKey(MapSize size, String displayNameKey) {
        for (Map.Entry<MapSize, Collection<TeamType>> entry : teamTypes.entrySet()) {
            if (size == null || entry.getKey().equals(size)) {
                for (TeamType team : entry.getValue()) {
                    if (team.getDisplayNameKey().equals(displayNameKey)) {
                        return team;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Collection<TeamType> teamTypes() {
        Collection<TeamType> teamTypes = new HashSet<>();
        teamTypes.add(spectator.getType());
        for (Collection<TeamType> t : this.teamTypes.values()) {
            teamTypes.addAll(t);
        }
        return teamTypes;
    }

    @Override
    public Collection<TeamType> teamTypes(MapSize mapSize) {
        return teamTypes.getOrDefault(mapSize, Collections.emptyList());
    }
}
