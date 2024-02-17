/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.UUID;

public class PServerUser implements User {

    private final eu.darkcube.system.userapi.User user;
    private JsonObject extra;

    public PServerUser(eu.darkcube.system.userapi.User user) {
        this.user = user;
        this.setExtra(UserCache.cache().getEntry(user.uniqueId()).extra);
    }

    @Override public boolean isOnline() {
        Player p = getOnlinePlayer();
        return p != null && p.isOnline();
    }

    public eu.darkcube.system.userapi.User user() {
        return user;
    }

    @Override public Language getLanguage() {
        return user().language();
    }

    @Override public CommandExecutor getCommandExecutor() {
        return user;
    }

    @Override public Player getOnlinePlayer() {
        return Bukkit.getPlayer(user.uniqueId());
    }

    @Override public UUID getUUID() {
        return user.uniqueId();
    }

    @Override public JsonObject getExtra() {
        return extra;
    }

    @Override public void setExtra(JsonObject extra) {
        this.extra = extra == null ? new JsonObject() : extra;
    }

    @Override public void saveExtra() {
        // Saving extra to UserCache
        UserCache.Entry entry = UserCache.cache().getEntry(user.uniqueId());
        for (Entry<String, JsonElement> e : new ArrayList<>(entry.extra.entrySet())) {
            entry.extra.remove(e.getKey());
        }
        for (Entry<String, JsonElement> e : extra.entrySet()) {
            entry.extra.add(e.getKey(), e.getValue());
        }
        UserCache.cache().update(entry, true);
    }
}
