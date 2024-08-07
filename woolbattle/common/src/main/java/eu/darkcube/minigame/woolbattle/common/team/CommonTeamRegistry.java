/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.team;

import static eu.darkcube.minigame.woolbattle.api.util.LogUtil.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.property.DocProperty;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.team.TeamConfiguration;
import eu.darkcube.minigame.woolbattle.api.team.TeamRegistry;
import eu.darkcube.minigame.woolbattle.api.team.TeamType;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.util.AdventureUtil;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

public class CommonTeamRegistry implements TeamRegistry {
    private final CommonWoolBattleApi woolbattle;
    private final DocProperty<ColoredWool> woolColor;
    private final DocProperty<Style> nameColor;
    private final Database database;
    private final Map<MapSize, Map<String, CommonTeamConfiguration>> teams = new ConcurrentHashMap<>();

    public CommonTeamRegistry(CommonWoolBattleApi woolbattle) {
        this.woolbattle = woolbattle;
        this.database = InjectionLayer.boot().instance(DatabaseProvider.class).database("woolbattle_teams");
        this.woolColor = DocProperty.property("woolColor", String.class).withReadWriteRewrite(serialized -> this.woolbattle.woolProvider().deserializeFromString(serialized), woolColor -> this.woolbattle.woolProvider().serializeToString(woolColor));
        this.nameColor = DocProperty.property("nameColor", String.class).withReadWriteRewrite(AdventureUtil::deserialize, AdventureUtil::serialize);
    }

    @ApiStatus.Internal
    public void init() {
        for (var entry : this.database.entries().entrySet()) {
            var mapSize = MapSize.fromString(entry.getKey());
            if (mapSize == null) {
                LOGGER.warn("Failed to load {} from Database: {}", entry.getKey(), database.name());
                continue;
            }
            for (var key : entry.getValue().keys()) {
                var doc = entry.getValue().readDocument(key);
                var nameStyle = doc.readProperty(this.nameColor);
                var woolColor = doc.readProperty(this.woolColor);
                var teamType = key.equals(SPECTATOR_KEY) ? TeamType.SPECTATOR : TeamType.PLAYER;
                var configuration = new CommonTeamConfiguration(key, mapSize, teamType, nameStyle, woolColor);

                var map = teams.computeIfAbsent(mapSize, _ -> new ConcurrentHashMap<>());
                map.put(key, configuration);
            }
            defaultSpectatorIfRequired(mapSize);
        }
    }

    private void defaultSpectatorIfRequired(@NotNull MapSize mapSize) {
        var map = teams.get(mapSize);
        if (map != null) {
            if (!map.containsKey(SPECTATOR_KEY)) {
                var configuration = new CommonTeamConfiguration(SPECTATOR_KEY, mapSize, TeamType.SPECTATOR, Style.style(NamedTextColor.GRAY), woolbattle.woolProvider().defaultWool());
                updateConfiguration(configuration);
            }
        }
    }

    @Override
    public @NotNull @Unmodifiable Collection<CommonTeamConfiguration> teamConfigurations(@NotNull MapSize mapSize) {
        return List.copyOf(teams.getOrDefault(mapSize, Map.of()).values());
    }

    @Override
    public @NotNull @Unmodifiable Collection<CommonTeamConfiguration> teamConfigurations() {
        return teams.values().stream().map(Map::values).flatMap(Collection::stream).toList();
    }

    @Override
    public synchronized @NotNull CommonTeamConfiguration createConfiguration(@NotNull MapSize mapSize, @NotNull String key) {
        var woolColor = woolbattle.woolProvider().defaultWool();
        var configuration = new CommonTeamConfiguration(key, mapSize, TeamType.PLAYER, Style.empty(), woolColor);
        updateConfiguration(configuration);
        return configuration;
    }

    @Override
    public synchronized void updateConfiguration(@NotNull TeamConfiguration configuration) {
        if (!SPECTATOR_KEY.equals(configuration.key())) defaultSpectatorIfRequired(configuration.mapSize());
        var commonConfiguration = (CommonTeamConfiguration) configuration.clone();
        var map = teams.computeIfAbsent(configuration.mapSize(), _ -> new ConcurrentHashMap<>());
        map.put(configuration.key(), commonConfiguration);
        // save to storage
        database.insert(configuration.mapSize().toString(), serialize(map));
    }

    private @NotNull Document serialize(@NotNull Map<String, CommonTeamConfiguration> map) {
        var doc = Document.newJsonDocument();
        for (var configuration : map.values()) {
            var entry = Document.newJsonDocument();
            entry.writeProperty(woolColor, configuration.woolColor());
            entry.writeProperty(nameColor, configuration.nameStyle());

            doc.append(configuration.key(), entry);
        }
        return doc;
    }

    @Override
    public @Nullable CommonTeamConfiguration configuration(@NotNull MapSize mapSize, @NotNull String key) {
        return teams.getOrDefault(mapSize, Map.of()).get(key);
    }
}
