/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package de.dasbabypixel.prefixplugin;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import eu.darkcube.system.userapi.User;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PrefixPlugin {

    private static PrefixPlugin plugin = new PrefixPlugin(PrefixPluginBukkit.instance());
    private PrefixPluginBukkit main;

    private PrefixPlugin(PrefixPluginBukkit main) {
        this.main = main;
    }

    @Api
    public static PrefixPlugin api() {
        return plugin;
    }

    /**
     * Reloads the entire system
     */
    @Api
    public void reload() {
        main.getScoreboardManager().reload();
    }

    /**
     * Reloads a player
     */
    @Api
    public void reload(Player player) {
        main.getScoreboardManager().reload(player.getUniqueId());
    }

    /**
     * Reloads a player
     */
    @Api
    public void reload(User user) {
        main.getScoreboardManager().reload(user.getUniqueId());
    }

    /**
     * Reloads a player
     */
    @Api
    public void reload(UUID uuid) {
        main.getScoreboardManager().reload(uuid);
    }

    /**
     * Gets the name with prefix and suffix for displaying purposes
     *
     * @param p the player
     *
     * @return the name
     */
    @Api
    public String name(Player p) {
        return main.getScoreboardManager().replacePlaceHolders(p, "%prefix%%name%%suffix%");
    }

    /**
     * @deprecated stupidity
     */
    @Deprecated
    @ScheduledForRemoval
    public void setSuffix(UUID uuid, String suffix) {
        main.getScoreboardManager().setSuffix(uuid, suffix);
    }

    /**
     * @deprecated stupidity
     */
    @Deprecated
    @ScheduledForRemoval
    public void setPrefix(UUID uuid, String prefix) {
        main.getScoreboardManager().setPrefix(uuid, prefix);
    }

    /**
     * @deprecated stupidity
     */
    @Deprecated
    @ScheduledForRemoval
    public String getPrefix(UUID uuid) {
        return main.getScoreboardManager().getPrefix(uuid);
    }

    /**
     * @deprecated stupidity
     */
    @Deprecated
    @ScheduledForRemoval
    public String getSuffix(UUID uuid) {
        return main.getScoreboardManager().getSuffix(uuid);
    }
}
