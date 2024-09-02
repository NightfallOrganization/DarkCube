/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums.hall;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.TeleportLocations;
import eu.darkcube.system.woolmania.enums.TeleporterAreas;
import eu.darkcube.system.woolmania.enums.Tiers;
import eu.darkcube.system.woolmania.registry.WoolRegistry;
import eu.darkcube.system.woolmania.util.message.Message;

public enum Halls {
    HALL1(Message.HALL, TeleportLocations.HALL1, PoolAreas.HALL1, HallAreas.HALL1, TeleporterAreas.HALL1, List.of(1), HallValue.HALL_VALUE_1, Tiers.TIER1, true, 0, 1),
    HALL2(Message.HALL, TeleportLocations.HALL2, PoolAreas.HALL2, HallAreas.HALL2, TeleporterAreas.HALL2, List.of(2, 1), HallValue.HALL_VALUE_2, Tiers.TIER2, true, 15000, 20),
    HALL3(Message.HALL, TeleportLocations.HALL3, PoolAreas.HALL3, HallAreas.HALL3, TeleporterAreas.HALL3, List.of(3, 1), HallValue.HALL_VALUE_3, Tiers.TIER3, true, 45000, 50),

    HALL4(Message.HALL, TeleportLocations.HALL4, PoolAreas.HALL4, HallAreas.HALL4, TeleporterAreas.HALL4, List.of(4), HallValue.HALL_VALUE_4, Tiers.TIER4, true, 75000, 80),
    HALL5(Message.HALL, TeleportLocations.HALL5, PoolAreas.HALL5, HallAreas.HALL5, TeleporterAreas.HALL5, List.of(5, 4), HallValue.HALL_VALUE_5, Tiers.TIER5, true, 105000, 110),
    HALL6(Message.HALL, TeleportLocations.HALL6, PoolAreas.HALL6, HallAreas.HALL6, TeleporterAreas.HALL6, List.of(6, 4), HallValue.HALL_VALUE_6, Tiers.TIER6, true, 135000, 140),

    HALL7(Message.HALL, TeleportLocations.HALL7, PoolAreas.HALL7, HallAreas.HALL7, TeleporterAreas.HALL7, List.of(7), HallValue.HALL_VALUE_7, Tiers.TIER7, true, 165000, 170),
    HALL8(Message.HALL, TeleportLocations.HALL8, PoolAreas.HALL8, HallAreas.HALL8, TeleporterAreas.HALL8, List.of(8, 7), HallValue.HALL_VALUE_8, Tiers.TIER8, true, 195000, 200),
    HALL9(Message.HALL, TeleportLocations.HALL9, PoolAreas.HALL9, HallAreas.HALL9, TeleporterAreas.HALL9, List.of(9, 7), HallValue.HALL_VALUE_9, Tiers.TIER9, true, 225000, 230),

    HALL10(Message.HALL, TeleportLocations.HALL10, PoolAreas.HALL10, HallAreas.HALL10, TeleporterAreas.HALL10, List.of(10), HallValue.HALL_VALUE_10, Tiers.TIER10, true, 255000, 260),
    HALL11(Message.HALL, TeleportLocations.HALL11, PoolAreas.HALL11, HallAreas.HALL11, TeleporterAreas.HALL11, List.of(11, 10), HallValue.HALL_VALUE_11, Tiers.TIER11, true, 285000, 290),
    HALL12(Message.HALL, TeleportLocations.HALL12, PoolAreas.HALL12, HallAreas.HALL12, TeleporterAreas.HALL12, List.of(12, 10), HallValue.HALL_VALUE_12, Tiers.TIER12, true, 315000, 320),

    HALL13(Message.HALL, TeleportLocations.HALL13, PoolAreas.HALL13, HallAreas.HALL13, TeleporterAreas.HALL13, List.of(9, 3), HallValue.HALL_VALUE_14, Tiers.TIER13, true, 345000, 350),
    HALL14(Message.HALL, TeleportLocations.HALL14, PoolAreas.HALL14, HallAreas.HALL14, TeleporterAreas.HALL14, List.of(10, 4), HallValue.HALL_VALUE_15, Tiers.TIER14, true, 375000, 380),
    HALL15(Message.HALL, TeleportLocations.HALL15, PoolAreas.HALL15, HallAreas.HALL15, TeleporterAreas.HALL15, List.of(11, 5), HallValue.HALL_VALUE_16, Tiers.TIER15, true, 405000, 410),

    // HALL16(Message.HALL, TeleportLocations.HALL16, PoolAreas.HALL16, HallAreas.HALL16, TeleporterAreas.HALL16, List.of(12, 6), HallValue.HALL_VALUE_17, Tiers.TIER16, true, 435000, 440),
    // HALL17(Message.HALL, TeleportLocations.HALL17, PoolAreas.HALL17, HallAreas.HALL17, TeleporterAreas.HALL17, List.of(13, 7), HallValue.HALL_VALUE_18, Tiers.TIER17, true, 465000, 470),
    // HALL18(Message.HALL, TeleportLocations.HALL18, PoolAreas.HALL18, HallAreas.HALL18, TeleporterAreas.HALL18, List.of(14, 8), HallValue.HALL_VALUE_19, Tiers.TIER18, true, 495000, 500),
    //
    // HALL19(Message.HALL, TeleportLocations.HALL19, PoolAreas.HALL19, HallAreas.HALL19, TeleporterAreas.HALL19, List.of(15, 9), HallValue.HALL_VALUE_20, Tiers.TIER19, true, 525000, 530),
    // HALL20(Message.HALL, TeleportLocations.HALL20, PoolAreas.HALL20, HallAreas.HALL20, TeleporterAreas.HALL20, List.of(16, 10), HallValue.HALL_VALUE_21, Tiers.TIER20, true, 555000, 560),


    // HALL4(Message.HALL, TeleportLocations.HALL4, PoolAreas.HALL4, HallAreas.HALL4, TeleportAreas.HALL4, Material.MAGENTA_WOOL, HallValue.HALL_VALUE_3, Tiers.TIER3, true, 45000, 50),

    // Halle 4 cost: 135000
    ;

    private final Message name;
    private final TeleportLocations spawn;
    private final PoolAreas pool;
    private final HallAreas area;
    private final TeleporterAreas teleportArea;
    private final List<WoolRegistry.Entry> wool;
    private final HallValue hallValue;
    private final Tiers tier;
    private final boolean locked;
    private final int cost;
    private final int level;

    Halls(Message name, TeleportLocations spawn, PoolAreas pool, HallAreas area, TeleporterAreas teleportArea, List<Integer> wool, HallValue hallValue, Tiers tier, boolean locked, int cost, int level) {
        this.name = name;
        this.spawn = spawn;
        this.pool = pool;
        this.area = area;
        this.teleportArea = teleportArea;
        this.wool = wool.stream().map(id -> WoolMania.getInstance().getWoolRegistry().get(id)).toList(); // Konvertiert ID zu Entry
        this.hallValue = hallValue;
        this.tier = tier;
        this.locked = locked;
        this.cost = cost;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public int getCost() {
        return cost;
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

    public TeleporterAreas getTeleportArea() {
        return teleportArea;
    }

    public List<WoolRegistry.Entry> getWoolEntries() {
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
