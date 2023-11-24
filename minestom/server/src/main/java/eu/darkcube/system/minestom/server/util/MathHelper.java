package eu.darkcube.system.minestom.server.util;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.util.ArrayList;
import java.util.List;

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

    private static class CoordinateUtils {
        static long getChunkKey(int x, int y) {
            return BinaryOperations.combine(x, y);
        }

        static int getChunkX(long key) {
            return BinaryOperations.x(key);
        }

        static int getChunkZ(long key) {
            return BinaryOperations.y(key);
        }

        static int getChunkKey(short x, short y) {
            return BinaryOperations.combine(x, y);
        }

        static short getChunkX(int key) {
            return BinaryOperations.x(key);
        }

        static short getChunkZ(int key) {
            return BinaryOperations.y(key);
        }
    }

    public static int[] generateBFSOrder(final int radius) {
        final var chunks = new IntArrayList();
        final var queue = new IntArrayFIFOQueue();
        final var seen = new IntOpenHashSet();

        seen.add(CoordinateUtils.getChunkKey((short) 0, (short) 0));
        queue.enqueue(CoordinateUtils.getChunkKey((short) 0, (short) 0));
        while (!queue.isEmpty()) {
            final var chunk = queue.dequeueInt();
            final var chunkX = CoordinateUtils.getChunkX(chunk);
            final var chunkZ = CoordinateUtils.getChunkZ(chunk);

            // important that the addition to the list is here, rather than when enqueueing neighbours
            // ensures the order is actually kept
            chunks.add(chunk);

            // -x
            final var n1 = CoordinateUtils.getChunkKey((short) (chunkX - 1), chunkZ);
            // -z
            final var n2 = CoordinateUtils.getChunkKey(chunkX, (short) (chunkZ - 1));
            // +x
            final var n3 = CoordinateUtils.getChunkKey((short) (chunkX + 1), chunkZ);
            // +z
            final var n4 = CoordinateUtils.getChunkKey(chunkX, (short) (chunkZ + 1));

            final var list = new int[]{n1, n2, n3, n4};

            for (final int neighbour : list) {
                final int neighbourX = CoordinateUtils.getChunkX(neighbour);
                final int neighbourZ = CoordinateUtils.getChunkZ(neighbour);
                if (Math.max(Math.abs(neighbourX), Math.abs(neighbourZ)) > radius) {
                    // don't enqueue out of range
                    continue;
                }
                if (!seen.add(neighbour)) {
                    continue;
                }
                queue.enqueue(neighbour);
            }
        }

        // to increase generation parallelism, we want to space the chunks out so that they are not nearby when generating
        // this also means we are minimising locality
        // but, we need to maintain sorted order by manhatten distance

        // first, build a map of manhatten distance -> chunks
        final List<IntArrayList> byDistance = new ArrayList<>();
        for (final IntIterator iterator = chunks.iterator(); iterator.hasNext(); ) {
            final var chunkKey = iterator.nextInt();

            final var chunkX = CoordinateUtils.getChunkX(chunkKey);
            final var chunkZ = CoordinateUtils.getChunkZ(chunkKey);

            final int dist = Math.abs(chunkX) + Math.abs(chunkZ);
            if (dist == byDistance.size()) {
                final var list = new IntArrayList();
                list.add(chunkKey);
                byDistance.add(list);
                continue;
            }

            byDistance.get(dist).add(chunkKey);
        }

        // per distance we transform the chunk list so that each element is maximally spaced out from each other
        for (int i = 0, len = byDistance.size(); i < len; ++i) {
            final var notAdded = byDistance.get(i).clone();
            final var added = new IntArrayList();

            while (!notAdded.isEmpty()) {
                if (added.isEmpty()) {
                    added.add(notAdded.removeInt(notAdded.size() - 1));
                    continue;
                }

                var maxChunk = -1;
                var maxDist = 0;

                // select the chunk from the not yet added set that has the largest minimum distance from
                // the current set of added chunks

                for (final var iterator = notAdded.iterator(); iterator.hasNext(); ) {
                    final var chunkKey = iterator.nextInt();
                    final var chunkX = CoordinateUtils.getChunkX(chunkKey);
                    final var chunkZ = CoordinateUtils.getChunkZ(chunkKey);

                    int minDist = Integer.MAX_VALUE;

                    for (final var iterator2 = added.iterator(); iterator2.hasNext(); ) {
                        final var addedKey = iterator2.nextInt();
                        final var addedX = CoordinateUtils.getChunkX(addedKey);
                        final var addedZ = CoordinateUtils.getChunkZ(addedKey);

                        // here we use square distance because chunk generation uses neighbours in a square radius
                        final var dist = Math.max(Math.abs(addedX - chunkX), Math.abs(addedZ - chunkZ));

                        if (dist < minDist) {
                            minDist = dist;
                        }
                    }

                    if (minDist > maxDist) {
                        maxDist = minDist;
                        maxChunk = chunkKey;
                    }
                }

                // move the selected chunk from the not added set to the added set

                if (!notAdded.rem(maxChunk)) {
                    throw new IllegalStateException();
                }

                added.add(maxChunk);
            }

            byDistance.set(i, added);
        }

        // now, rebuild the list so that it still maintains manhatten distance order
        final var ret = new IntArrayList(chunks.size());

        for (final var dist : byDistance) {
            ret.addAll(dist);
        }

        return ret.toIntArray();
    }

    public static void bresenheimAll(int centerX, int centerY, float radius, CircleConsumer consumer) {
        bresenheim(centerX, centerY, radius, (centerX1, centerY1, relativeX, relativeY) -> {
//            consumer.color(centerX1, centerY1, relativeX, relativeY);
//            consumer.color(centerX1, centerY1, -relativeX, relativeY);
            bresenheimAllLoadRect(centerX1, centerY1, -relativeX, relativeY, relativeX, relativeY, consumer);
            if (relativeY != 0) bresenheimAllLoadRect(centerX1, centerY1, -relativeX, -relativeY, relativeX, -relativeY, consumer);
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
