package eu.darkcube.system.minestom.server.chunk;

import eu.darkcube.system.minestom.server.util.ConcurrentLinkedNodeDeque;
import eu.darkcube.system.minestom.server.util.PrioritizedExecutor;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;

import java.util.Collection;
import java.util.NavigableSet;

public class ChunkManager<T> {

    private final PrioritizedExecutor executor;
    private final Callbacks<T> callbacks;
    private final ChunkCache<T> chunkCache;

    public ChunkManager(Callbacks<T> callbacks, int priorityCount, Long2ObjectFunction<T> generator) {
        this.callbacks = callbacks;
        executor = new PrioritizedExecutor.Default(priorityCount);
        chunkCache = new ChunkCache<>(generator, executor, callbacks::generated);
    }

    public ChunkCache.RequireResult<T> require(int x, int y, int priority) {
        var result = chunkCache.require(x, y, priority);
        if (result.required()) {
            callbacks.loaded(x, y);
        }
        return result;
    }

    public NavigableSet<ChunkCache.Ticket> tickets(int x, int y) {
        return chunkCache.tickets(x, y);
    }

    public ChunkCache.ReleaseResult release(int x, int y, ChunkCache.Ticket ticket) {
        var result = chunkCache.release(x, y, ticket);
        if (result.released()) {
            callbacks.unloaded(x, y);
        }
        ConcurrentLinkedNodeDeque
        return result;
    }

    public void tick() {
    }

    public interface Callbacks<T> {
        void loaded(int chunkX, int chunkY);

        void unloaded(int chunkX, int chunkY);

        void generated(int chunkX, int chunkY, T data);
    }

//    public void loadAround(int x, int y, float range) {
//        bresenheim(x, y, range, (centerX, centerY, relativeX, relativeY) -> {
//            loadRect(centerX, centerY, -relativeX, relativeY, relativeX, relativeY);
//            if (relativeY != 0) loadRect(centerX, centerY, -relativeX, -relativeY, relativeX, -relativeY);
//        });
//    }
//
//    public void unloadAround(int x, int y, float range) {
//        bresenheim(x, y, range, (centerX, centerY, relativeX, relativeY) -> {
//            unloadRect(centerX, centerY, -relativeX, relativeY, relativeX, relativeY);
//            if (relativeY != 0) unloadRect(centerX, centerY, -relativeX, -relativeY, relativeX, -relativeY);
//        });
//    }
//
//    public void loadRect(int centerX, int centerY, int x1, int y1, int x2, int y2) {
//        for (int x = x1; x <= x2; x++) {
//            for (int y = y1; y <= y2; y++) {
//                int realX = centerX + x;
//                int realY = centerY + y;
//                require(realX, realY, priorityCalculator.priority(centerX, centerY, realX, realY));
//            }
//        }
//    }
//
//    public void unloadRect(int centerX, int centerY, int x1, int y1, int x2, int y2) {
//        for (int x = x1; x <= x2; x++) {
//            for (int y = y1; y <= y2; y++) {
//                int realX = centerX + x;
//                int realY = centerY + y;
//                release(realX, realY);
//            }
//        }
//    }
}
