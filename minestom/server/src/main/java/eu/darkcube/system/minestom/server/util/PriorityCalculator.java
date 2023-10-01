package eu.darkcube.system.minestom.server.util;

public interface PriorityCalculator {
    int priority(int centerX, int centerY, int relativeX, int relativeY);
}