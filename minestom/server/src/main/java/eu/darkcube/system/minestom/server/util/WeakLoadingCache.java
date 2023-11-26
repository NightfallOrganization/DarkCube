package eu.darkcube.system.minestom.server.util;

import java.lang.ref.Cleaner;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

public class WeakLoadingCache<K, V> {

    private static final Cleaner CLEANER = Cleaner.create();

    private final Loader<K, V> loader;
    private final ConcurrentHashMap<K, WeakReference<V>> backend = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<WeakReference<V>, K> inverse = new ConcurrentHashMap<>();
    private final ReferenceQueue<V> queue = new ReferenceQueue<>();

    public WeakLoadingCache(Loader<K, V> loader) {
        this.loader = loader;
        var cleanerThread = new CleanerThread<>(queue, backend, inverse);
        cleanerThread.start();
        CLEANER.register(this, cleanerThread::exit);
    }

    public V get(K key) {
        while (true) {
            var ref = backend.get(key);
            if (ref != null) {
                var value = ref.get();
                if (value != null) return value;
                if (!backend.remove(key, ref)) continue;
            }
            var value = loader.load(key);
            ref = new WeakReference<>(value, queue);
            if (backend.putIfAbsent(key, ref) == null) {
                inverse.put(ref, key);
                return value;
            }
        }
    }

    public interface Loader<K, V> {
        V load(K key);
    }

    private static class CleanerThread<K, V> extends Thread {
        private volatile boolean exit = false;
        private final ReferenceQueue<V> referenceQueue;
        private final ConcurrentHashMap<K, WeakReference<V>> map;
        private final ConcurrentHashMap<WeakReference<V>, K> inverse;

        public CleanerThread(ReferenceQueue<V> referenceQueue, ConcurrentHashMap<K, WeakReference<V>> map, ConcurrentHashMap<WeakReference<V>, K> inverse) {
            super("WeakLoadingCacheCleaner");
            this.referenceQueue = referenceQueue;
            this.map = map;
            this.inverse = inverse;
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while (!exit) {
                try {
                    var reference = referenceQueue.remove();
                    var key = inverse.remove(reference);
                    map.remove(key, reference);
                } catch (InterruptedException ignored) {
                }
            }
        }

        private void exit() {
            exit = true;
            this.interrupt();
        }
    }
}
