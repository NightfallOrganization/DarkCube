/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.team;

import java.util.Collection;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import org.bukkit.Location;

public interface Team extends Comparable<Team> {

    UUID getUniqueId();

    boolean isSpectator();

    boolean canPlay();

    Component getName(CommandExecutor executor);

    Style getPrefixStyle();

    TeamType getType();

    Collection<? extends WBUser> getUsers();

    boolean contains(UUID user);

    int getLifes();

    void setLifes(int lifes);

    void setSpawn(Map map, Location location);

    Location getSpawn(Map map);

    Location getSpawn();

    void setSpawn(Location location);

}
