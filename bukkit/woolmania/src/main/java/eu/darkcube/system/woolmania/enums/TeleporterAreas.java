/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

import static eu.darkcube.system.woolmania.manager.WorldManager.HALLS;

import eu.darkcube.system.woolmania.util.area.Area;
import org.bukkit.World;

public enum TeleporterAreas implements Area {
    HALL1(HALLS, -79, 207, 110,      183, 29, -105),
    HALL2(HALLS, -79, 207, 1110,      183, 29, 895),
    HALL3(HALLS, -79, 207, 2110,      183, 29, 1895),
    HALL4(HALLS, -79, 207, 3110,      183, 29, 2895),
    HALL5(HALLS, -79, 207, 4110,      183, 29, 3895),
    HALL6(HALLS, -79, 207, 5110,      183, 29, 4895),
    HALL7(HALLS, -79, 207, 6110,      183, 29, 5895),
    HALL8(HALLS, -79, 207, 7110,      183, 29, 6895),
    HALL9(HALLS, -79, 207, 8110,      183, 29, 7895),
    HALL10(HALLS, -79, 207, 9110,      183, 29, 8895),
    HALL11(HALLS, -79, 207, 10110,     183, 29, 9895),
    HALL12(HALLS, -79, 207, 11110,     183, 29, 10895),
    HALL13(HALLS, -79, 207, 12110,     183, 29, 11895),
    HALL14(HALLS, -79, 207, 13110,     183, 29, 12895),
    HALL15(HALLS, -79, 207, 14110,     183, 29, 13895),
    HALL16(HALLS, -79, 207, 15110,     183, 29, 14895),
    HALL17(HALLS, -79, 207, 16110,     183, 29, 15895),
    HALL18(HALLS, -79, 207, 17110,     183, 29, 16895),
    HALL19(HALLS, -79, 207, 18110,     183, 29, 17895),
    HALL20(HALLS, -79, 207, 19110,     183, 29, 18895),

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

    TeleporterAreas(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
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
