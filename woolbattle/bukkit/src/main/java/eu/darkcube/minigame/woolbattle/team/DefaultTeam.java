/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.team;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

class DefaultTeam implements Team {

    private final UUID uuid;
    private final TeamType type;
    private final WoolBattleBukkit woolbattle;
    private int lifes;

    public DefaultTeam(TeamType type, WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        this.uuid = UUID.randomUUID();
        this.type = type;
    }

    public static Style convert(ChatColor color) {
        return switch (color) {
            case BLACK -> Style.style(NamedTextColor.BLACK);
            case DARK_BLUE -> Style.style(NamedTextColor.DARK_BLUE);
            case DARK_GREEN -> Style.style(NamedTextColor.DARK_GREEN);
            case DARK_AQUA -> Style.style(NamedTextColor.DARK_AQUA);
            case DARK_RED -> Style.style(NamedTextColor.DARK_RED);
            case DARK_PURPLE -> Style.style(NamedTextColor.DARK_PURPLE);
            case GOLD -> Style.style(NamedTextColor.GOLD);
            case GRAY -> Style.style(NamedTextColor.GRAY);
            case DARK_GRAY -> Style.style(NamedTextColor.DARK_GRAY);
            case BLUE -> Style.style(NamedTextColor.BLUE);
            case GREEN -> Style.style(NamedTextColor.GREEN);
            case AQUA -> Style.style(NamedTextColor.AQUA);
            case RED -> Style.style(NamedTextColor.RED);
            case LIGHT_PURPLE -> Style.style(NamedTextColor.LIGHT_PURPLE);
            case YELLOW -> Style.style(NamedTextColor.YELLOW);
            case WHITE -> Style.style(NamedTextColor.WHITE);
            case MAGIC -> Style.style(TextDecoration.OBFUSCATED);
            case BOLD -> Style.style(TextDecoration.BOLD);
            case STRIKETHROUGH -> Style.style(TextDecoration.STRIKETHROUGH);
            case UNDERLINE -> Style.style(TextDecoration.UNDERLINED);
            case ITALIC -> Style.style(TextDecoration.ITALIC);
            default -> throw new IllegalArgumentException(Objects.toString(color));
        };
    }

    @Override public int compareTo(Team o) {
        return getType().compareTo(o.getType());
    }

    @Override public UUID getUniqueId() {
        return uuid;
    }

    @Override public boolean isSpectator() {
        return getType().getDisplayNameKey().equals("spectator");
    }

    @Override public boolean canPlay() {
        return !isSpectator();
    }

    @Override public Component getName(CommandExecutor executor) {
        return Message
                .getMessage(Message.TEAM_PREFIX + getType().getDisplayNameKey().toUpperCase(Locale.ROOT), executor.language())
                .style(getPrefixStyle());
    }

    @Override public Style getPrefixStyle() {
        return convert(getType().getNameColor());
    }

    @Override public TeamType getType() {
        return type;
    }

    @Override public Collection<? extends WBUser> getUsers() {
        return WBUser.onlineUsers().stream().filter(user -> user.getTeam().equals(this)).collect(Collectors.toSet());
    }

    @Override public boolean contains(UUID user) {
        for (WBUser u : getUsers()) {
            if (u.getUniqueId().equals(user)) {
                return true;
            }
        }
        return false;
    }

    @Override public int getLifes() {
        return lifes;
    }

    @Override public void setLifes(int lifes) {
        this.lifes = lifes;
        woolbattle.ingame().playerUtil().reloadScoreboardLifes();
    }

    @Override public void setSpawn(Map map, Location location) {
        map.ingameData().spawn(getType().getDisplayNameKey(), location);
    }

    @Override public Location getSpawn(Map map) {
        return map.ingameData().spawn(getType().getDisplayNameKey());
    }

    @Override public Location getSpawn() {
        return getSpawn(woolbattle.gameData().map());
    }

    @Override public void setSpawn(Location location) {
        setSpawn(woolbattle.gameData().map(), location);
    }
}
