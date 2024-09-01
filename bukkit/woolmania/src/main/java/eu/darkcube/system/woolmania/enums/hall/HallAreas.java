/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums.hall;

import static eu.darkcube.system.woolmania.manager.WorldManager.HALLS;

import eu.darkcube.system.woolmania.util.area.Area;
import org.bukkit.World;

public enum HallAreas implements Area {

    HALL1(HALLS, -12, 178, 48,      142, 51, -61),
    HALL2(HALLS, -12, 178, 1048,      142, 51, 939),
    HALL3(HALLS, -12, 178, 2048,      142, 51, 1939),
    HALL4(HALLS, -12, 178, 3048,      142, 51, 2939),
    HALL5(HALLS, -12, 178, 4048,      142, 51, 3939),
    HALL6(HALLS, -12, 178, 5048,      142, 51, 4939),
    HALL7(HALLS, -12, 178, 6048,      142, 51, 5939),
    HALL8(HALLS, -12, 178, 7048,      142, 51, 6939),
    HALL9(HALLS, -12, 178, 8048,      142, 51, 7939),
    HALL10(HALLS, -12, 178, 9048,      142, 51, 8939),
    HALL11(HALLS, -12, 178, 10048,     142, 51, 9939),
    HALL12(HALLS, -12, 178, 11048,     142, 51, 10939),
    HALL13(HALLS, -12, 178, 12048,     142, 51, 11939),
    HALL14(HALLS, -12, 178, 13048,     142, 51, 12939),
    HALL15(HALLS, -12, 178, 14048,     142, 51, 13939),
    HALL16(HALLS, -12, 178, 15048,     142, 51, 14939),
    HALL17(HALLS, -12, 178, 16048,     142, 51, 15939),
    HALL18(HALLS, -12, 178, 17048,     142, 51, 16939),
    HALL19(HALLS, -12, 178, 18048,     142, 51, 17939),
    HALL20(HALLS, -12, 178, 19048,     142, 51, 18939),

    ;

    private final World world;
    private final double[] coordinate1;
    private final double[] coordinate2;
    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;

    HallAreas(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = world;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;

        this.coordinate1 = new double[]{x1, y1, z1};
        this.coordinate2 = new double[]{x2, y2, z2};
    }

    public World getWorld() {
        return world;
    }

    public double[] getCoordinate1() {
        return this.coordinate1;
    }

    public double[] getCoordinate2() {
        return this.coordinate2;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getZ1() {
        return z1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public int getZ2() {
        return z2;
    }

}
