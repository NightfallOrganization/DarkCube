/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.map;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public class CommonMap implements Map {
    private final @NotNull CommonMapManager mapManager;
    private final @NotNull String name;
    private final @NotNull MapSize size;
    private volatile int deathHeight;
    private volatile @NotNull ItemBuilder icon;
    private volatile boolean enabled;

    public CommonMap(@NotNull CommonMapManager mapManager, @NotNull String name, @NotNull MapSize size, @NotNull ItemBuilder icon) {
        this.mapManager = mapManager;
        this.name = name;
        this.size = size;
        this.icon = icon;
    }

    public CommonMap(@NotNull CommonMapManager mapManager, @NotNull String name, @NotNull MapSize size, int deathHeight, @NotNull ItemBuilder icon, boolean enabled) {
        this.mapManager = mapManager;
        this.name = name;
        this.size = size;
        this.deathHeight = deathHeight;
        this.icon = icon;
        this.enabled = enabled;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public void enabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    @Override
    public int deathHeight() {
        return deathHeight;
    }

    @Override
    public void deathHeight(int deathHeight) {
        this.deathHeight = deathHeight;
        save();
    }

    @Override
    public @NotNull ItemBuilder icon() {
        return icon;
    }

    @Override
    public void icon(@NotNull ItemBuilder icon) {
        this.icon = icon;
        save();
    }

    @Override
    public @NotNull MapSize size() {
        return size;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    public @NotNull Document toDocument() {
        var doc = Document.newJsonDocument();
        doc.append("deathHeight", deathHeight());
        doc.append("enabled", enabled());
        doc.append("name", name());
        doc.append("icon", icon().serialize().toString());
        doc.append("mapSize", size());
        return doc;
    }

    public static CommonMap fromDocument(@NotNull CommonMapManager mapManager, @NotNull Document document) {
        var name = document.getString("name");
        var enabled = document.getBoolean("enabled");
        var deathHeight = document.getInt("deathHeight");
        var size = document.readObject("mapSize", MapSize.class);
        var icon = ItemBuilder.item(new Gson().fromJson(document.getString("icon"), JsonElement.class));
        return new CommonMap(mapManager, name, size, deathHeight, icon, enabled);
    }

    private void save() {
        mapManager.save(this);
    }
}
