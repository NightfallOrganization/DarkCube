/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import de.dytanic.cloudnet.driver.service.*;
import de.dytanic.cloudnet.driver.template.TemplateStorage;
import de.dytanic.cloudnet.template.LocalTemplateStorage;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
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
	private final Database pserverData;
	private final SingleThreadExecutor executor = new SingleThreadExecutor();
	private final ConcurrentMap<UniqueId, SoftReference<NodePServerExecutor>> weakpservers =
			new ConcurrentHashMap<>();
	private final ReferenceQueue<NodePServerExecutor> referenceQueue = new ReferenceQueue<>();
	private final Map<UniqueId, NodePServerExecutor> pservers = new HashMap<>();
	public ServiceTask pserverTask;
	public ServiceTemplate globalTemplate;
	public ServiceTemplate worldTemplate;
	private TemplateStorage storage = CloudNetDriver.getInstance().getLocalTemplateStorage();

	private NodePServerProvider() {

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
		th.start();

		// ServiceTask
		{
			if (!CloudNet.getInstance().getServiceTaskProvider()
					.isServiceTaskPresent(NodePServerProvider.pserverTaskName)) {
				Collection<ServiceRemoteInclusion> includes = new ArrayList<>();
				Collection<ServiceTemplate> templates = new ArrayList<>();
				Collection<ServiceDeployment> deployments = new ArrayList<>();
				String runtime = "jvm";
				boolean maintenance = false;
				boolean autoDeleteOnStop = true;
				boolean staticServices = false;
				Collection<String> associatedNodes = new ArrayList<>();
				Collection<String> groups = new ArrayList<>();
				Collection<String> deletedFilesAfterStop = new ArrayList<>();
				ProcessConfiguration processConfiguration =
						new ProcessConfiguration(ServiceEnvironmentType.MINECRAFT_SERVER, 312,
								new ArrayList<>());
				int startPort = 20000;
				int minServiceCount = 0;

				this.pserverTask = new ServiceTask(includes, templates, deployments,
						NodePServerProvider.pserverTaskName, runtime, maintenance, autoDeleteOnStop,
						staticServices, associatedNodes, groups, deletedFilesAfterStop,
						processConfiguration, startPort, minServiceCount);
				CloudNet.getInstance().getServiceTaskProvider()
						.addPermanentServiceTask(this.pserverTask);
			} else {
				this.pserverTask = CloudNet.getInstance().getServiceTaskProvider()
						.getServiceTask(NodePServerProvider.pserverTaskName);
			}
		}
		// ServiceTemplates
		{
			String name = ".global";
			this.globalTemplate = new ServiceTemplate(PServerProvider.templatePrefix(), name,
					LocalTemplateStorage.LOCAL_TEMPLATE_STORAGE);

			if (!this.storage.has(this.globalTemplate)) {
				this.storage.create(this.globalTemplate);
			}
			name = ".world";
			this.worldTemplate = new ServiceTemplate(PServerProvider.templatePrefix(), name,
					LocalTemplateStorage.LOCAL_TEMPLATE_STORAGE);
			if (!this.storage.has(this.worldTemplate)) {
				this.storage.create(this.worldTemplate);
			}
		}
		this.pserverData =
				CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("pserver_data");
	}

	public static NodePServerProvider instance() {
		return NodePServerProvider.instance;
	}

	public static void init() {
		NodePServerProvider.instance = new NodePServerProvider();
	}

	public void unload(UniqueId id) {
		executor.execute(() -> {
			if (pservers.containsKey(id)) {
				weakpservers.put(id, new SoftReference<>(pservers.remove(id), referenceQueue));
			}
		});
	}

	@Override
	public void setPServerCommand(BiFunction<Object, String[], Boolean> command)
	throws IllegalStateException {
		throw new IllegalStateException();
	}

	@Override
	public boolean isPServer() {
		return false;
	}

	@Override
	public PServerExecutor currentPServer() throws IllegalStateException {
		throw new IllegalStateException();
	}

	@Override
	public @NotNull CompletableFuture<@Nullable NodePServerExecutor> pserver(UniqueId pserver) {
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

	@Override
	public CompletableFuture<@NotNull Boolean> pserverExists(UniqueId pserver) {
		CompletableFuture<Boolean> fut = new CompletableFuture<>();
		executor.execute(() -> {
			if (pserver == null)
				fut.complete(false);
			fut.complete(pservers.containsKey(pserver));
		});
		return fut;
	}

	@Override
	public CompletableFuture<PServerExecutor> createPServer(PServerBuilder builder) {
		CompletableFuture<PServerExecutor> fut = new CompletableFuture<>();
		executor.execute(() -> {
			UniqueId uid = NodeUniqueIdProvider.instance().newUniqueId();
			String taskName =
					builder.taskName() == null ? this.pserverTask.getName() : builder.taskName();
			NodePServerExecutor ex = new NodePServerExecutor(this, uid, builder.type(), taskName);
			ex.accessLevel(builder.accessLevel());
			weakpservers.put(uid, new SoftReference<>(ex, referenceQueue));
			fut.complete(ex);
		});
		return fut;
	}

	@Override
	public CompletableFuture<Collection<? extends PServerExecutor>> pservers() {
		CompletableFuture<Collection<? extends PServerExecutor>> fut = new CompletableFuture<>();
		executor.execute(() -> {
			List<PServerExecutor> l = new ArrayList<>(pservers.values());
			fut.complete(l);
		});
		return fut.thenApplyAsync(s -> s);
	}

	@Override
	public CompletableFuture<Collection<UniqueId>> pservers(UUID owner) {
		return CompletableFuture.completedFuture(
				DatabaseProvider.get("pserver").cast(PServerDatabase.class).getPServers(owner));
	}

	public Collection<String> getAllTemplateIDs() {
		return this.storage.getTemplates().stream()
				.filter(s -> s.getPrefix().equals(PServerProvider.templatePrefix()))
				.map(ServiceTemplate::getName)
				.filter(s -> !(s.equals(".world") || s.equals(".global")))
				.collect(Collectors.toList());
	}

	public Database pserverData() {
		return pserverData;
	}

	private static class SingleThreadExecutor extends Thread {
		private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
		private final Lock lock = new ReentrantLock();
		private final Condition condition = lock.newCondition();
		private volatile boolean exit = false;

		public SingleThreadExecutor() {
			super("SingleThreadPServerExecutor");
			start();
		}

		@Override
		public void run() {
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
