/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.chunk;

import eu.darkcube.system.minestom.server.util.PriorityCalculator;

public class NormalPriorityCalculator implements PriorityCalculator {
    private final int priorityCount;

    public NormalPriorityCalculator(int priorityCount) {
        this.priorityCount = priorityCount;
    }

    @Override public int priority(int centerX, int centerY, int relativeX, int relativeY) {
        int distXSq = relativeX * relativeX;
        int distYSq = relativeY * relativeY;
        int prio = (int) Math.sqrt(distXSq + distYSq);
        return Math.clamp(prio, 0, priorityCount - 1);
    }
}
