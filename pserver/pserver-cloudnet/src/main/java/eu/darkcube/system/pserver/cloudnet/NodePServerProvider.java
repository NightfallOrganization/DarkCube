/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.GroupConfigurationProvider;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.service.*;
import eu.cloudnetservice.driver.template.TemplateStorage;
import eu.cloudnetservice.driver.template.TemplateStorageProvider;
import eu.cloudnetservice.node.template.LocalTemplateStorage;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.common.PServerBuilder;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class NodePServerProvider extends PServerProvider {

    public static final String pserverTaskName = "pserver";
    private static NodePServerProvider instance;
    final ConcurrentMap<UniqueId, SoftReference<NodePServerExecutor>> weakpservers = new ConcurrentHashMap<>();
    final ReferenceQueue<NodePServerExecutor> referenceQueue = new ReferenceQueue<>();
    final Map<UniqueId, NodePServerExecutor> pservers = new HashMap<>();
    final SingleThreadExecutor executor = new SingleThreadExecutor();
    private final Database pserverData;
    private ServiceTask pserverTask;
    private ServiceTemplate globalTemplate;
    private ServiceTemplate worldTemplate;
    private TemplateStorageProvider templateStorageProvider = InjectionLayer.boot().instance(TemplateStorageProvider.class);
    private TemplateStorage storage = templateStorageProvider.templateStorage("local");

    private NodePServerProvider() {
        Thread th = thread();
        th.start();

        // ServiceTask
        {
            ServiceTaskProvider serviceTaskProvider = InjectionLayer.boot().instance(ServiceTaskProvider.class);
            if (serviceTaskProvider.serviceTask(NodePServerProvider.pserverTaskName) == null) {
                Collection<ServiceRemoteInclusion> includes = new ArrayList<>();
                Collection<ServiceTemplate> templates = new ArrayList<>();
                Collection<ServiceDeployment> deployments = new ArrayList<>();
                String runtime = "jvm";
                boolean maintenance = false;
                boolean autoDeleteOnStop = true;
                boolean staticServices = false;
                Document properties = Document.newJsonDocument();
                Collection<String> associatedNodes = new ArrayList<>();
                Collection<String> groups = new ArrayList<>();
                Collection<String> deletedFilesAfterStop = new ArrayList<>();
                ProcessConfiguration.Builder processConfiguration = ProcessConfiguration
                        .builder()
                        .environment(ServiceEnvironmentType.MINECRAFT_SERVER)
                        .maxHeapMemorySize(312)
                        .jvmOptions(new ArrayList<>())
                        .processParameters(new ArrayList<>())
                        .environmentVariables(new HashMap<>());
                int startPort = 20000;
                int minServiceCount = 0;

                this.pserverTask = ServiceTask
                        .builder()
                        .name(NodePServerProvider.pserverTaskName)
                        .runtime(runtime)
                        .maintenance(maintenance)
                        .autoDeleteOnStop(autoDeleteOnStop)
                        .staticServices(staticServices)
                        .groups(groups)
                        .associatedNodes(associatedNodes)
                        .deletedFilesAfterStop(deletedFilesAfterStop)
                        .processConfiguration(processConfiguration)
                        .startPort(startPort)
                        .minServiceCount(minServiceCount)
                        .templates(templates)
                        .deployments(deployments)
                        .inclusions(includes)
                        .properties(properties)
                        .build();

                serviceTaskProvider.addServiceTask(this.pserverTask);
            } else {
                this.pserverTask = serviceTaskProvider.serviceTask(NodePServerProvider.pserverTaskName);
            }
        }
        // Groups
        {
            GroupConfigurationProvider groupConfigurationProvider = InjectionLayer.boot().instance(GroupConfigurationProvider.class);
            if (groupConfigurationProvider.groupConfiguration("pserver-global") == null) {
                groupConfigurationProvider.addGroupConfiguration(GroupConfiguration.builder().name("pserver-global").build());
            }
            if (groupConfigurationProvider.groupConfiguration("pserver-world") == null) {
                groupConfigurationProvider.addGroupConfiguration(GroupConfiguration.builder().name("pserver-world").build());
            }
        }
        // ServiceTemplates
        {
            String name = ".global";

            this.globalTemplate = ServiceTemplate
                    .builder()
                    .name(name)
                    .prefix(PServerProvider.templatePrefix())
                    .storage(LocalTemplateStorage.LOCAL_TEMPLATE_STORAGE)
                    .build();

            if (!this.storage.contains(this.globalTemplate)) {
                this.storage.create(this.globalTemplate);
            }
            name = ".world";
            this.worldTemplate = ServiceTemplate
                    .builder()
                    .name(name)
                    .prefix(PServerProvider.templatePrefix())
                    .storage(LocalTemplateStorage.LOCAL_TEMPLATE_STORAGE)
                    .build();
            if (!this.storage.contains(this.worldTemplate)) {
                this.storage.create(this.worldTemplate);
            }
        }
        DatabaseProvider databaseProvider = InjectionLayer.boot().instance(DatabaseProvider.class);
        this.pserverData = databaseProvider.database("pserver_data");
    }

    public static @NotNull NodePServerProvider instance() {
        return NodePServerProvider.instance;
    }

    public static void init() {
        NodePServerProvider.instance = new NodePServerProvider();
    }

    @NotNull private Thread thread() {
        Thread th = new Thread(() -> {
            while (true) {
                try {
                    Reference<? extends NodePServerExecutor> ref = referenceQueue.remove();
                    for (UniqueId id : weakpservers.keySet()) {
                        if (weakpservers.get(id).equals(ref)) {
                            weakpservers.remove(id);
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        th.setName("PServerGCHandler");
        th.setDaemon(true);
        return th;
    }

    public void unload(UniqueId id) {
        executor.execute(() -> {
            if (pservers.containsKey(id)) {
                weakpservers.put(id, new SoftReference<>(pservers.remove(id), referenceQueue));
            }
        });
    }

    ServiceTemplate worldTemplate() {
        return worldTemplate;
    }

    ServiceTemplate globalTemplate() {
        return globalTemplate;
    }

    ServiceTask pserverTask() {
        return pserverTask;
    }

    TemplateStorage storage() {
        return storage;
    }

    @Override public void setPServerCommand(BiFunction<Object, String[], Boolean> command) throws IllegalStateException {
        throw new IllegalStateException();
    }

    @Override public boolean isPServer() {
        return false;
    }

    @Override public @NotNull PServerExecutor currentPServer() throws IllegalStateException {
        throw new IllegalStateException();
    }

    @Override public @NotNull CompletableFuture<@Nullable NodePServerExecutor> pserver(@NotNull UniqueId pserver) {
        CompletableFuture<NodePServerExecutor> fut = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                if (pservers.containsKey(pserver)) {
                    fut.complete(pservers.get(pserver));
                    return;
                }
                SoftReference<NodePServerExecutor> ref = weakpservers.get(pserver);
                if (ref != null) {
                    NodePServerExecutor ex = ref.get();
                    if (ex != null) {
                        fut.complete(ex);
                    } else {
                        weakpservers.remove(pserver);
                    }
                }
                NodePServerExecutor ex = new NodePServerExecutor(this, pserver);
                ref = new SoftReference<>(ex, referenceQueue);
                weakpservers.put(pserver, ref);
                fut.complete(ex);
            } catch (Throwable t) {
                fut.completeExceptionally(t);
            }
        });
        return fut;
    }

    @Override public @NotNull CompletableFuture<@NotNull Boolean> pserverExists(@NotNull UniqueId pserver) {
        CompletableFuture<Boolean> fut = new CompletableFuture<>();
        executor.execute(() -> {
            if (pserver == null) fut.complete(false);
            fut.complete(pservers.containsKey(pserver));
        });
        return fut;
    }

    @Override public CompletableFuture<PServerExecutor> createPServer(PServerBuilder builder) {
        CompletableFuture<PServerExecutor> fut = new CompletableFuture<>();
        executor.execute(() -> {
            UniqueId uid = NodeUniqueIdProvider.instance().newUniqueId();
            String taskName = builder.taskName() == null ? this.pserverTask.name() : builder.taskName();
            NodePServerExecutor ex = new NodePServerExecutor(this, uid, builder.type(), taskName);
            ex.accessLevel(builder.accessLevel());
            weakpservers.put(uid, new SoftReference<>(ex, referenceQueue));
            fut.complete(ex);
        });
        return fut;
    }

    @Override public CompletableFuture<Collection<? extends PServerExecutor>> pservers() {
        CompletableFuture<Collection<? extends PServerExecutor>> fut = new CompletableFuture<>();
        executor.execute(() -> {
            List<PServerExecutor> l = new ArrayList<>(pservers.values());
            fut.complete(l);
        });
        return fut;
    }

    @Override public CompletableFuture<Collection<UniqueId>> pservers(UUID owner) {
        return CompletableFuture.completedFuture(eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider
                .get("pserver")
                .cast(PServerDatabase.class)
                .getPServers(owner));
    }

    public Collection<String> getAllTemplateIDs() {
        return this.storage
                .templates()
                .stream()
                .filter(s -> s.prefix().equals(PServerProvider.templatePrefix()))
                .map(ServiceTemplate::name)
                .filter(s -> !(s.equals(".world") || s.equals(".global")))
                .collect(Collectors.toList());
    }

    public Database pserverData() {
        return pserverData;
    }

    static class SingleThreadExecutor extends Thread {
        private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
        private final Lock lock = new ReentrantLock();
        private final Condition condition = lock.newCondition();
        private volatile boolean exit = false;

        public SingleThreadExecutor() {
            super("SingleThreadPServerExecutor");
            start();
        }

        @Override public void run() {
            while (!exit) {
                while (canPoll()) {
                    workQueue();
                }
                lock.lock();
                if (canPoll()) {
                    lock.unlock();
                    continue;
                }
                condition.awaitUninterruptibly();
                lock.unlock();
            }
        }

        private boolean canPoll() {
            return !queue.isEmpty();
        }

        private void workQueue() {
            Runnable r;
            while ((r = queue.poll()) != null) {
                r.run();
            }
        }

        public void execute(Runnable runnable) {
            if (Thread.currentThread() == this) {
                runnable.run();
                return;
            }
            queue.offer(runnable);
            lock.lock();
            condition.signalAll();
            lock.unlock();
        }
    }

}
