/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums.hall;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.woolmania.enums.TeleportAreas;
import eu.darkcube.system.woolmania.enums.TeleportLocations;
import eu.darkcube.system.woolmania.enums.Tiers;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.Material;

public enum Halls {
    HALL1(Message.HALL, TeleportLocations.HALL1, PoolAreas.HALL1, HallAreas.HALL1, TeleportAreas.HALL1, Material.WHITE_WOOL, HallValue.HALL_VALUE_1, Tiers.TIER1, true),
    HALL2(Message.HALL, TeleportLocations.HALL2, PoolAreas.HALL2, HallAreas.HALL2, TeleportAreas.HALL2, Material.ORANGE_WOOL, HallValue.HALL_VALUE_2, Tiers.TIER2, true),
    HALL3(Message.HALL, TeleportLocations.HALL3, PoolAreas.HALL3, HallAreas.HALL3, TeleportAreas.HALL3, Material.MAGENTA_WOOL, HallValue.HALL_VALUE_3, Tiers.TIER3, true),

    ;

    private final Message name;
    private final TeleportLocations spawn;
    private final PoolAreas pool;
    private final HallAreas area;
    private final TeleportAreas teleportArea;
    private final Material wool;
    private final HallValue hallValue;
    private final Tiers tier;
    private final boolean locked;

    Halls(Message name, TeleportLocations spawn, PoolAreas pool, HallAreas area, TeleportAreas teleportArea, Material wool, HallValue hallValue, Tiers tier, boolean locked) {
        this.name = name;
        this.spawn = spawn;
        this.pool = pool;
        this.area = area;
        this.teleportArea = teleportArea;
        this.wool = wool;
        this.hallValue = hallValue;
        this.tier = tier;
        this.locked = locked;
    }

    public Message getName() {
        return name;
    }

    public TeleportLocations getSpawn() {
        return spawn;
    }

    public PoolAreas getPool() {
        return pool;
    }

    public HallAreas getHallArea() {
        return area;
    }

    public TeleportAreas getTeleportArea() {
        return teleportArea;
    }

    public Material getWool() {
        return wool;
    }

    public HallValue getHallValue() {
        return hallValue;
    }

    public Tiers getTier() {
        return tier;
    }

    public static List<Halls> unlockedHalls() {
        List<Halls> unlockedHalls = new ArrayList<>();
        for (Halls hall : Halls.values()) {
            if (!hall.isLocked()) {
                unlockedHalls.add(hall);
            }
        }
        return List.copyOf(unlockedHalls);
    }

    public boolean isLocked() {
        return locked;
    }

}
