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

public enum PoolAreas implements Area {

    HALL1(HALLS, 25, 110, 19,       99, 68, -19),
    HALL2(HALLS, 25, 110, 1019,     99, 68, 981),
    HALL3(HALLS, 25, 110, 2019,     99, 68, 1981),
    HALL4(HALLS, 25, 110, 3019,     99, 68, 2981),
    HALL5(HALLS, 25, 110, 4019,     99, 68, 3981),
    HALL6(HALLS, 25, 110, 5019,     99, 68, 4981),
    HALL7(HALLS, 25, 110, 6019,     99, 68, 5981),
    HALL8(HALLS, 25, 110, 7019,     99, 68, 6981),
    HALL9(HALLS, 25, 110, 8019,     99, 68, 7981),
    HALL10(HALLS, 25, 110, 9019,    99, 68, 8981),
    HALL11(HALLS, 25, 110, 10019,   99, 68, 9981),
    HALL12(HALLS, 25, 110, 11019,   99, 68, 10981),
    HALL13(HALLS, 25, 110, 12019,   99, 68, 11981),
    HALL14(HALLS, 25, 110, 13019,   99, 68, 12981),
    HALL15(HALLS, 25, 110, 14019,   99, 68, 13981),
    HALL16(HALLS, 25, 110, 15019,   99, 68, 14981),
    HALL17(HALLS, 25, 110, 16019,   99, 68, 15981),
    HALL18(HALLS, 25, 110, 17019,   99, 68, 16981),
    HALL19(HALLS, 25, 110, 18019,   99, 68, 17981),
    HALL20(HALLS, 25, 110, 19019,   99, 68, 18981),

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

    PoolAreas(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
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
