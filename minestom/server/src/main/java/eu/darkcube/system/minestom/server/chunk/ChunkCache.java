package eu.darkcube.system.minestom.server.chunk;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.server.player.PlayerManager;
import eu.darkcube.system.minestom.server.util.*;
import io.vavr.collection.List;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import net.minestom.server.entity.Player;

import javax.swing.plaf.TableHeaderUI;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static eu.darkcube.system.minestom.server.util.BinaryOperations.combine;

public class ChunkCache<T> {
    private static final int STATE_INITIALIZED = 0;
    private static final int STATE_GENERATING_INIT = STATE_INITIALIZED + 1;
    private static final int STATE_GENERATING = STATE_GENERATING_INIT + 1;
    private static final int STATE_GENERATING_FINISHED = STATE_GENERATING + 1;
    private static final int STATE_GENERATED = STATE_GENERATING_FINISHED + 1;
    private static final int STATE_GENERATED_UNLOADED = STATE_GENERATED + 1;
    private static final int STATE_RE_PRIORITIZING = STATE_GENERATED_UNLOADED + 1;
    private static final int STATE_RELEASING = STATE_RE_PRIORITIZING + 1;
    private static final VarHandle STATE;
    private static final VarHandle COUNT;

    static {
        try {
            var lookup = MethodHandles.lookup();
            COUNT = lookup.findVarHandle(Entry.class, "count", int.class);
            STATE = lookup.findVarHandle(Entry.class, "state", int.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    // A cache to load Entry instances. Also ensures there are never multiple entry instances with the same key
//    private final LoadingCache<Long, Entry<T>> loader = Caffeine.newBuilder().weakValues().build(Entry::new);
    private final WeakLoadingCache<Long, Entry<T>> loader = new WeakLoadingCache<>(Entry::new);
    // The cache where all entries are saved
    private final ConcurrentHashMap<Long, Entry<T>> cache = new ConcurrentHashMap<>();
    private final Long2ObjectFunction<T> generator;
    private final PrioritizedExecutor executor;
    private final GeneratedCallback<T> generatedCallback;

    public ChunkCache(Long2ObjectFunction<T> generator, PrioritizedExecutor executor, GeneratedCallback<T> generatedCallback) {
        this.generator = generator;
        this.executor = executor;
        this.generatedCallback = generatedCallback;
    }

    public NavigableSet<Ticket> tickets(int chunkX, int chunkY) {
        var key = combine(chunkX, chunkY);
        var entry = loader.get(key);
        return entry.tickets();
    }

    public @NotNull RequireResult<T> require(int chunkX, int chunkY, int priority) {
        var key = combine(chunkX, chunkY);
        var entry = loader.get(key);
        var ticket = new Ticket(priority);
        var count = entry.addTicket(ticket);

        if (count == 0) {
            // we are the ones to have required the first ticket for the entry. We are responsible for loading the data
            while (true) {
                // we just re-created the entry. Someone might still be freeing this though. Well just wait for them to update the state
                if (STATE.compareAndSet(entry, STATE_INITIALIZED, STATE_GENERATING_INIT)) {
                    entry.taskPriority = priority;
                    entry.task = executor.submit(new GeneratorTask<>(priority, entry.future.future, generator, entry, key, generatedCallback));
                    cache.put(key, entry);
                    entry.state = STATE_GENERATING;
                    return new RequireResult<>(entry.future, ticket, true);
                }
                if (entry.state == STATE_GENERATED_UNLOADED) {
                    cache.put(key, entry);
                    generatedCallback.call(chunkX, chunkY, entry.future.future.resultNow());
                    entry.state = STATE_GENERATED;
                    return new RequireResult<>(entry.future, ticket, true);
                }
                Thread.onSpinWait();
            }
        }
        maybeRePrioritize(key, entry);
        return new RequireResult<>(entry.future, ticket, false);
    }

    private void maybeRePrioritize(long key, Entry<T> entry) {
        while (true) {
            var state = entry.state;
            if (state == STATE_GENERATED) return;
            if (state == STATE_INITIALIZED) {
                // The entry might not yet be initialized by the first person to require it
                Thread.onSpinWait();
                continue;
            }

            var lastEntry = entry.tickets.lastEntry();
            if (lastEntry == null) return; // The ticket is being cleaned up by someone else (while we are removing a reference)
            Ticket last = lastEntry.getKey();

            if (last.priority == entry.taskPriority) {
                // The task already has the current priority
                return;
            }

            if (state == STATE_GENERATING) {
                if (STATE.compareAndSet(entry, STATE_GENERATING, STATE_RE_PRIORITIZING)) {
                    entry.task.cancel();
                    entry.taskPriority = last.priority;
                    entry.task = executor.submit(new GeneratorTask<>(last.priority, entry.future.future, generator, entry, key, generatedCallback));
                    entry.state = STATE_GENERATING;
                }
            }

            Thread.onSpinWait();
        }
    }

    public @NotNull ReleaseResult release(int chunkX, int chunkY, Ticket ticket) {
        var key = combine(chunkX, chunkY);

        waitForConsistentState:
        {
            var entry = loader.get(key);

            while (true) {
                var state = entry.state;
                switch (state) {
                    case STATE_GENERATING, STATE_GENERATED, STATE_RE_PRIORITIZING -> {
                        break waitForConsistentState;
                    }
                }
                Thread.onSpinWait();
            }
        }

        var entry = cache.get(key);
        if (entry == null) {
            entry = loader.get(key);
            throw new IllegalStateException("Wrong reference counting " + entry.count + " " + entry.tickets + " " + entry.state);
        }
        if (entry.removeTicket(ticket)) {
            // someone might require the entry right here, but they will wait until the state is initialized
            while (true) {
                if (STATE.compareAndSet(entry, STATE_GENERATING, STATE_RELEASING)) {
                    entry.taskPriority = 0;
                    entry.task.cancel();
                    entry.task = null;
                    cache.remove(key);
                    entry.state = STATE_INITIALIZED;
                    return new ReleaseResult(true);
                }
                if (STATE.compareAndSet(entry, STATE_GENERATED, STATE_RELEASING)) {
                    cache.remove(key);
                    entry.state = STATE_GENERATED_UNLOADED;
                    return new ReleaseResult(true);
                }
                Thread.onSpinWait();
            }
        } else {
            maybeRePrioritize(key, entry);
        }
        return new ReleaseResult(false);
    }

    public static class GeneratorTask<T> extends PriorityCallable<T> {
        private final Long2ObjectFunction<T> generator;
        private final Entry<T> entry;
        private final long key;
        private final GeneratedCallback<T> generatedCallback;

        public GeneratorTask(int priority, CompletableFuture<T> future, Long2ObjectFunction<T> generator, Entry<T> entry, long key, GeneratedCallback<T> generatedCallback) {
            super(priority, future);
            this.generator = generator;
            this.entry = entry;
            this.key = key;
            this.generatedCallback = generatedCallback;
        }

        @Override public T call() {
            return generator.apply(key);
        }

        @Override protected void complete(T t) {
            while (true) {
                int state = (int) STATE.compareAndExchange(entry, STATE_GENERATING, STATE_GENERATING_FINISHED);
                if (state == STATE_GENERATING) {
                    entry.task = null;
                    entry.taskPriority = 0;
                    future.complete(t);
                    generatedCallback.call(BinaryOperations.x(key), BinaryOperations.y(key), t);
                    entry.state = STATE_GENERATED;
                } else if (state == STATE_GENERATING_INIT || state == STATE_RELEASING) {
                    Thread.onSpinWait();
                    continue;
                }
                break;
            }
        }
    }

    public interface GeneratedCallback<T> {
        void call(int chunkX, int chunkY, T data);
    }

    public record RequireResult<T>(ChunkFuture<T> future, Ticket ticket, boolean required) {
    }

    public static class ChunkFuture<T> {
        private final CompletableFuture<T> future = new CompletableFuture<>();
        private final Collection<Consumer<T>> listeners = new CopyOnWriteArrayList<>();

        public ChunkFuture() {
            future.thenAccept(value -> {
                for (var listener : listeners) {
                    listener.accept(value);
                }
            });
        }

        @Nullable public T getNow() {
            return future.getNow(null);
        }

        public void then(Consumer<T> listener) {
            if (future.isDone()) {
                listener.accept(future.resultNow());
                return;
            }
            listeners.add(listener);
            if (future.isDone()) {
                if (listeners.remove(listener)) {
                    listener.accept(future.resultNow());
                }
            }
        }

        public void remove(Consumer<T> listener) {
            listeners.remove(listener);
        }

        public void await() {
            future.join();
        }
    }

    public record ReleaseResult(boolean released) {
    }

    public static class Ticket implements Comparable<Ticket> {
        private final int priority;

        public Ticket(int priority) {
            this.priority = priority;
        }

        @Override public int compareTo(@NotNull ChunkCache.Ticket o) {
            if (o == this) return 0;
            int cmp = Integer.compare(priority, o.priority);
            if (cmp != 0) return cmp;
            return Integer.compare(hashCode(), o.hashCode());
        }

        @Override public String toString() {
            return Integer.toString(priority);
        }
    }

    @SuppressWarnings({"unused", "FieldMayBeFinal"}) public static class Entry<T> {
        private final ChunkFuture<T> future = new ChunkFuture<>();
        private final ConcurrentSkipListMap<Ticket, Boolean> tickets = new ConcurrentSkipListMap<>();
        private volatile int count;
        private volatile int state = STATE_INITIALIZED;
        private int taskPriority;
        private CancellableTask task;

        public Entry(long key) {
        }

        public ChunkFuture<T> future() {
            return future;
        }

        private int count() {
            return count;
        }

        private NavigableSet<Ticket> tickets() {
            return tickets.keySet();
        }

        /**
         * @return the amount of tickets that this entry had before adding the new ticket
         */
        private int addTicket(Ticket ticket) {
            if (tickets.put(ticket, Boolean.TRUE) == null) {
                return require();
            }
            System.out.println("Failed to add ticket");
            throw new IllegalStateException("Invalid ticket adding");
        }

        /**
         * @return whether the last ticket was removed
         */
        private boolean removeTicket(Ticket ticket) {
            if (tickets.remove(ticket, Boolean.TRUE)) {
                return release();
            }
            System.out.println(count + ": " + tickets);
            throw new IllegalStateException("Invalid ticket removal: " + ticket);
        }

        private int require() {
            return (int) COUNT.getAndAdd(this, 1);
        }

        private boolean release() {
            var n = (int) COUNT.getAndAdd(this, -1) - 1;
            if (n < 0) throw new IllegalStateException(); // This should never happen
            return n == 0;
        }
    }
}
