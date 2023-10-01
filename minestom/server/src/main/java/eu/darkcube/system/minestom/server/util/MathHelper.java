package eu.darkcube.system.minestom.server.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import static eu.darkcube.system.minestom.server.util.BinaryOperations.*;

public class MathHelper {
    public static int[] bresenheimOffsets(float radius) {
        IntList offsetsList = new IntArrayList();
        bresenheimAll(0, 0, radius, (centerX, centerY, relativeX, relativeY) -> {
            var key = combine((short) relativeX, (short) relativeY);
            offsetsList.add(key);
        });
        offsetsList.sort((k1, k2) -> {
            var k1x = x(k1);
            var k1y = y(k1);
            var k2x = x(k2);
            var k2y = y(k2);
            return Integer.compare(k1x * k1x + k1y * k1y, k2x * k2x + k2y * k2y);
        });
        return offsetsList.toIntArray();
    }

    private static void bresenheim(int centerX, int centerY, float radius, CircleConsumer consumer) {
        float t1 = radius / 16;
        int x = (int) radius;
        int y = 0;
        while (!(x < y)) {
            consumer.color(centerX, centerY, x, y);
            y++;
            t1 += y;
            float t2 = t1 - x;
            if (t2 >= 0) {
                t1 = t2;
                if (y <= x) consumer.color(centerX, centerY, y - 1, x);
                x--;
            }
        }
    }

    public static void bresenheimAll(int centerX, int centerY, float radius, CircleConsumer consumer) {
        bresenheim(centerX, centerY, radius, (centerX1, centerY1, relativeX, relativeY) -> {
//            consumer.color(centerX1, centerY1, relativeX, relativeY);
//            consumer.color(centerX1, centerY1, -relativeX, relativeY);
            bresenheimAllLoadRect(centerX1, centerY1, -relativeX, relativeY, relativeX, relativeY, consumer);
            if (relativeY != 0)
                bresenheimAllLoadRect(centerX1, centerY1, -relativeX, -relativeY, relativeX, -relativeY, consumer);
        });
    }

    private static void bresenheimAllLoadRect(int centerX, int centerY, int x1, int y1, int x2, int y2, CircleConsumer consumer) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                consumer.color(centerX, centerY, x, y);
            }
        }
    }

    public interface CircleConsumer {
        void color(int centerX, int centerY, int relativeX, int relativeY);
    }
}
