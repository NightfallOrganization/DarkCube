/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.map;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer.DontSerialize;
import eu.darkcube.minigame.woolbattle.util.Serializable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DefaultMap implements Map, Serializable {

    private String name;
    private boolean enabled;
    private int deathHeight;
    private ItemStack icon;
    private MapSize size;
    @DontSerialize
    private @Nullable MapIngameData ingameData;

    public DefaultMap(String name, boolean enabled, int deathHeight, ItemStack icon, MapSize size, @Nullable MapIngameData ingameData) {
        this.name = name;
        this.enabled = enabled;
        this.deathHeight = deathHeight;
        this.icon = icon;
        this.size = size;
        this.ingameData = ingameData;
    }

    public DefaultMap(String name, MapSize size) {
        this.name = name;
        this.size = size;
        enabled = false;
        icon = new ItemStack(Material.GRASS);
    }

    @Override public int deathHeight() {
        return deathHeight;
    }

    @Override public void deathHeight(int deathHeight) {
        this.deathHeight = deathHeight;
        save();
    }

    @Override public boolean isEnabled() {
        return enabled;
    }

    @Override public ItemStack getIcon() {
        return icon;
    }

    @Override public void setIcon(ItemStack icon) {
        this.icon = icon;
        save();
    }

    @Override public void enable() {
        enabled = true;
        save();
    }

    @Override public void disable() {
        enabled = false;
        save();
    }

    @Override public MapSize size() {
        return size;
    }

    public void ingameData(MapIngameData ingameData) {
        this.ingameData = ingameData;
    }

    @Override public @Nullable MapIngameData ingameData() {
        return ingameData;
    }

    @Override public String getName() {
        return name;
    }

    @Override public String serialize() {
        return Serializable.super.serialize();
    }

    JsonDocument toDocument() {
        return JsonDocument.newDocument(serialize());
    }

    void save() {
        ((DefaultMapManager) WoolBattleBukkit.instance().mapManager()).save(this);
    }
}
