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
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.common.PServerBuilder;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;

public class NodePServerProvider extends PServerProvider {

    public static final String pserverTaskName = "pserver";
    private static NodePServerProvider instance;
    private final Map<UniqueId, Reference<? extends NodePServerExecutor>> weakPServers = new HashMap<>();
    private final Map<Reference<? extends NodePServerExecutor>, UniqueId> weakPServersReverse = new HashMap<>();
    private final ReferenceQueue<NodePServerExecutor> referenceQueue = new ReferenceQueue<>();
    private final Map<UniqueId, NodePServerExecutor> pservers = new HashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "PServerThread"));
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
            var serviceTaskProvider = InjectionLayer.boot().instance(ServiceTaskProvider.class);
            if (serviceTaskProvider.serviceTask(NodePServerProvider.pserverTaskName) == null) {
                var includes = new ArrayList<ServiceRemoteInclusion>();
                var templates = new ArrayList<ServiceTemplate>();
                var deployments = new ArrayList<ServiceDeployment>();
                var runtime = "jvm";
                boolean maintenance = false;
                boolean autoDeleteOnStop = true;
                boolean staticServices = false;
                var properties = Document.newJsonDocument();
                var associatedNodes = new ArrayList<String>();
                var groups = new ArrayList<String>();
                var deletedFilesAfterStop = new ArrayList<String>();
                var processConfiguration = ProcessConfiguration
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
            var groupConfigurationProvider = InjectionLayer.boot().instance(GroupConfigurationProvider.class);
            if (groupConfigurationProvider.groupConfiguration("pserver-global") == null) {
                groupConfigurationProvider.addGroupConfiguration(GroupConfiguration.builder().name("pserver-global").build());
            }
            if (groupConfigurationProvider.groupConfiguration("pserver-world") == null) {
                groupConfigurationProvider.addGroupConfiguration(GroupConfiguration.builder().name("pserver-world").build());
            }
        }
        // ServiceTemplates
        {
            var name = ".global";

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
        var databaseProvider = InjectionLayer.boot().instance(DatabaseProvider.class);
        this.pserverData = databaseProvider.database("pserver_data");
    }

    public static @NotNull NodePServerProvider instance() {
        return NodePServerProvider.instance;
    }

    public static void init() {
        NodePServerProvider.instance = new NodePServerProvider();
    }

    private void putWeakPServer(NodePServerExecutor executor) {
        var ref = new SoftReference<>(executor, referenceQueue);
        weakPServers.put(executor.id(), ref);
        weakPServersReverse.put(ref, executor.id());
    }

    private @NotNull NodePServerExecutor pserverInternal(@NotNull UniqueId pserver) {
        if (pservers.containsKey(pserver)) return pservers.get(pserver);
        var ref = weakPServers.get(pserver);
        if (ref != null) {
            var ex = ref.get();
            if (ex != null) return ex;
        }
        var ex = new NodePServerExecutor(this, pserver);
        putWeakPServer(ex);
        return ex;
    }

    private @NotNull NodePServerExecutor createPServerInternal(@NotNull PServerBuilder builder) {
        var uid = NodeUniqueIdProvider.instance().newUniqueId();
        var taskName = builder.taskName() == null ? this.pserverTask.name() : builder.taskName();
        var ex = new NodePServerExecutor(this, uid, builder.type(), taskName);
        ex.accessLevel(builder.accessLevel());
        putWeakPServer(ex);
        return ex;
    }

    void holdReference(NodePServerExecutor executor) {
        this.executor.execute(() -> pservers.put(executor.id(), executor));
    }

    void releaseReference(NodePServerExecutor executor) {
        this.executor.execute(() -> pservers.remove(executor.id(), executor));
    }

    private boolean pserverExistsInternal(@NotNull UniqueId pserver) {
        return pservers.containsKey(pserver);
    }

    public void cleanup() {
        executor.shutdown();
        instance = null;
    }

    public void lifecycleStopped(UniqueId id) {
        executor.execute(() -> {
            var ex = pservers.remove(id);
            if (ex != null) {
                ex.stop();
            }
        });
    }

    ServiceTemplate worldTemplate() {
        return worldTemplate;
    }

    ServiceTemplate globalTemplate() {
        return globalTemplate;
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

    @Override public @NotNull CompletableFuture<@NotNull NodePServerExecutor> pserver(@NotNull UniqueId pserver) {
        return CompletableFuture.supplyAsync(() -> pserverInternal(pserver), executor);
    }

    @Override public @NotNull CompletableFuture<@NotNull Boolean> pserverExists(@NotNull UniqueId pserver) {
        return CompletableFuture.supplyAsync(() -> pserverExistsInternal(pserver), executor);
    }

    @Override public @NotNull CompletableFuture<@NotNull NodePServerExecutor> createPServer(PServerBuilder builder) {
        return CompletableFuture.supplyAsync(() -> createPServerInternal(builder), executor);
    }

    @Override public CompletableFuture<Collection<? extends PServerExecutor>> pservers() {
        var fut = new CompletableFuture<Collection<? extends PServerExecutor>>();
        executor.execute(() -> {
            var l = List.copyOf(pservers.values());
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

    public Database pserverData() {
        return pserverData;
    }

    @Override public @NotNull CompletableFuture<@NotNull Collection<@NotNull UniqueId>> registeredPServers() {
        var db = eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider.get("pserver").cast(PServerDatabase.class);
        return CompletableFuture.completedFuture(db.getUsedPServerIDs());
    }

    @NotNull private Thread thread() {
        var th = new Thread(() -> {
            while (true) {
                try {
                    var ref = referenceQueue.remove();
                    executor.submit(() -> {
                        var id = weakPServersReverse.remove(ref);
                        weakPServers.remove(id, ref);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        th.setName("PServerGCHandler");
        th.setDaemon(true);
        return th;
    }
}
