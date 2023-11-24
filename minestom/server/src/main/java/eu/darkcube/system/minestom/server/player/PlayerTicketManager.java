package eu.darkcube.system.minestom.server.player;

import eu.darkcube.system.minestom.server.chunk.ChunkCache;
import eu.darkcube.system.minestom.server.chunk.ChunkManager;
import eu.darkcube.system.minestom.server.chunk.ChunkViewer;
import eu.darkcube.system.minestom.server.util.BinaryOperations;
import eu.darkcube.system.minestom.server.util.MathHelper;
import eu.darkcube.system.minestom.server.util.PriorityCalculator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PlayerTicketManager<T> {

    private static final int[][] SEARCH_RADIUS_ITERATION_LIST;

    static {
        SEARCH_RADIUS_ITERATION_LIST = new int[65][];
        for (int i = 0; i < SEARCH_RADIUS_ITERATION_LIST.length; i++) {
            SEARCH_RADIUS_ITERATION_LIST[i] = MathHelper.generateBFSOrder(i);
        }
    }

    private final ChunkManager<T> chunkManager;
    private final PriorityCalculator priorityCalculator;
    private final ChunkViewer<T> viewer;
    private final ConcurrentHashMap<Long, Entry<T>> chunks = new ConcurrentHashMap<>();
    private final LongOpenHashSet sentChunks = new LongOpenHashSet();
    private final AreaMap<PlayerTicketManager<T>> sentChunkMap = new AreaMap<>(this) {
        @Override protected void addCallback(PlayerTicketManager<T> parameter, int chunkX, int chunkZ) {

        }

        @Override protected void removeCallback(PlayerTicketManager<T> parameter, int chunkX, int chunkZ) {
            parameter.sendUnloadChunk(chunkX, chunkZ);
        }
    };
    private int[] offsets;
    private int radius = -1;
    private boolean unloaded = true;
    private int x = 0;
    private int z = 0;

    public PlayerTicketManager(ChunkManager<T> chunkManager, PriorityCalculator priorityCalculator, int radius, ChunkViewer<T> viewer) {
        this.chunkManager = chunkManager;
        this.priorityCalculator = priorityCalculator;
        this.viewer = viewer;
        resize(radius);
    }

    public ChunkManager<?> chunkManager() {
        return chunkManager;
    }

    public void resize(int radius) {
        synchronized (this) {
            if (this.radius == radius) return;
            this.radius = radius;
            offsets = SEARCH_RADIUS_ITERATION_LIST[radius];

            int[][] out = new int[radius * 2 + 1][radius * 2 + 1];
            for (var i = 0; i < offsets.length; i++) {
                var offset = offsets[i];
                var x = BinaryOperations.x(offset);
                var y = BinaryOperations.y(offset);
                out[y + radius][x + radius] = i;
            }
            for (int[] ints : out) {
                System.out.println(Arrays.toString(ints));
            }

            if (!unloaded) {
                unloaded = true;
                remove();
            }
        }
    }

    public void unload() {
        synchronized (this) {
            if (unloaded) return;
            unloaded = true;
            remove();
        }
    }

    private void unload(int[] offsets, ChunkCache.Ticket[] tickets) {
        for (int i = 0; i < offsets.length; i++) {
            var offset = offsets[i];
            var relativeX = (int) BinaryOperations.x(offset);
            var relativeY = (int) BinaryOperations.y(offset);
            chunkManager.release(x + relativeX, z + relativeY, tickets[i]);
            tickets[i] = null;
        }
    }

//    public void move(int newX, int newY) {
//        move(newX, newY, offsets, offsets);
//    }

    private boolean wantChunkLoaded(int centerX, int centerZ, int chunkX, int chunkZ, int sendRadius) {
        return isWithinDistance(centerX, centerZ, sendRadius, chunkX, chunkZ, true);
    }

    static boolean isWithinDistance(int centerX, int centerZ, int viewDistance, int x, int z, boolean includeEdge) {
        int i = Math.max(0, Math.abs(x - centerX) - 1);
        int j = Math.max(0, Math.abs(z - centerZ) - 1);
        long l = Math.max(0, Math.max(i, j) - (includeEdge ? 1 : 0));
        long m = Math.min(i, j);
        long n = m * m + l * l;
        int k = viewDistance * viewDistance;
        return n < (long) k;
    }

    public void move(int newX, int newZ) {
        synchronized (this) {
            move0(newX, newZ);
        }
    }

    private void remove() {
        sentChunkMap.remove();
    }

    private void add(int newX, int newZ) {
        x = newX;
        z = newZ;
        sentChunkMap.add(newX, newZ, radius);
    }

    private void move0(int newX, int newZ) {
        synchronized (this) {
            var oldX = x;
            var oldZ = z;
            if ((oldX == newX && oldZ == newZ) && !unloaded) return;
            final var sendDistance = radius;

            int cnt = 0;
            int skipped = 0;
            System.out.println("1: " + -(oldX - newX) + " " + (newZ - oldZ));

            sentChunkMap.update(newX, newZ, sendDistance + 1);

            for (final var deltaChunk : offsets) {
                final var dx = BinaryOperations.x(deltaChunk);
                final var dz = BinaryOperations.y(deltaChunk);
                final var chunkX = dx + oldX;
                final var chunkZ = dz + oldZ;
                final var chunk = BinaryOperations.combine(chunkX, chunkZ);
                final var squareDistance = Math.max(Math.abs(dx), Math.abs(dz));
                final var manhattanDistance = Math.abs(dx) + Math.abs(dz);

                final boolean sendChunk = (squareDistance <= (sendDistance + 1)) && wantChunkLoaded(x, z, chunkX, chunkZ, sendDistance);
                final boolean sentChunk = sendChunk ? this.sentChunks.contains(chunk) : this.sentChunks.remove(chunk);

                if (!sendChunk && sentChunk) {
                    // chunk not wanted anymore. Unload.
                    sendUnloadChunk(chunkX, chunkZ);
                }
            }

            System.out.println("Sent " + cnt);
            System.out.println("Skipped: " + skipped);
            x = newX;
            z = newZ;
            unloaded = false;
        }
    }

    private void sendUnloadChunk(int x, int z) {
        viewer.unloadChunk(x, z);
    }

    private void loadChunks(int x, int z, int[] offsets) {
        for (var i = 0; i < offsets.length; i++) {
            var offset = offsets[i];
            var relativeX = (int) BinaryOperations.x(offset);
            var relativeY = (int) BinaryOperations.y(offset);
            var priority = priorityCalculator.priority(x, z, relativeX, relativeY);

            var realX = x + relativeX;
            var realY = z + relativeY;
            var key = BinaryOperations.combine(realX, realY);

            // Load the coordinate
            var result = chunkManager.require(realX, realY, priority);
            var future = result.future();
            var e = chunks.computeIfAbsent(key, nkey -> {
                var listener = new Consumer<T>() {
                    @Override public void accept(T t) {
                        viewer.loadChunk(realX, realY, t);
                    }
                };
                var entry = new Entry<>(future, listener);
                future.then(listener);
                return entry;
            });
            e.ticket(result.ticket());
            e.requires++;
        }
    }

    private static final class Entry<T> {
        private final ChunkCache.ChunkFuture<T> future;
        private ChunkCache.Ticket oldTicket;
        private ChunkCache.Ticket ticket;
        private final Consumer<T> listener;
        private int requires = 0;

        private Entry(ChunkCache.ChunkFuture<T> future, Consumer<T> listener) {
            this.future = future;
            this.ticket = ticket;
            this.listener = listener;
        }

        public void ticket(ChunkCache.Ticket ticket) {
            this.oldTicket = this.ticket;
            this.ticket = ticket;
        }

        public ChunkCache.Ticket ticket() {
            return ticket;
        }

        public Consumer<T> listener() {
            return listener;
        }

        @Override public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Entry<T>) obj;
            return Objects.equals(this.ticket, that.ticket) && Objects.equals(this.listener, that.listener);
        }

        @Override public int hashCode() {
            return Objects.hash(ticket, listener);
        }

        @Override public String toString() {
            return "Entry[" + "ticket=" + ticket + ", " + "listener=" + listener + ']';
        }
    }

    public float radius() {
        synchronized (this) {
            return radius;
        }
    }

    public int x() {
        synchronized (this) {
            return x;
        }
    }

    public int y() {
        synchronized (this) {
            return z;
        }
    }
}
