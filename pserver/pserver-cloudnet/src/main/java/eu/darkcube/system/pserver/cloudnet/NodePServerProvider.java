/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import de.dytanic.cloudnet.driver.service.ProcessConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceDeployment;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceRemoteInclusion;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import de.dytanic.cloudnet.driver.template.TemplateStorage;
import de.dytanic.cloudnet.service.EmptyGroupConfiguration;
import de.dytanic.cloudnet.template.LocalTemplateStorage;
import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperAddOwner;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperAddPServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperRemoveOwner;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperRemovePServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperUpdateInfo;

public class NodePServerProvider extends PServerProvider {

	private static final ExecutorService executor = Executors.newFixedThreadPool(1);

	private static NodePServerProvider instance;

	public static final String pserverTaskName = "pserver";

	public ServiceTask pserverTask;

	public ServiceTemplate globalTemplate;

	public ServiceTemplate worldTemplate;

	private Database pserverData;

	private TemplateStorage storage = CloudNetDriver.getInstance().getLocalTemplateStorage();

	private NodePServerProvider() {

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
				ProcessConfiguration processConfiguration = new ProcessConfiguration(
						ServiceEnvironmentType.MINECRAFT_SERVER, 312, new ArrayList<String>());
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
			this.globalTemplate = new ServiceTemplate(PServerProvider.getTemplatePrefix(), name,
					LocalTemplateStorage.LOCAL_TEMPLATE_STORAGE);

			if (!this.storage.has(this.globalTemplate)) {
				this.storage.create(this.globalTemplate);
			}
			name = ".world";
			this.worldTemplate = new ServiceTemplate(PServerProvider.getTemplatePrefix(), name,
					LocalTemplateStorage.LOCAL_TEMPLATE_STORAGE);
			if (!this.storage.has(this.worldTemplate)) {
				this.storage.create(this.worldTemplate);
			}
		}
		this.pserverData =
				CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("pserver_data");
	}

	private Map<UniqueId, NodePServer> pservers = new HashMap<>();

	@Override
	public String newName() {
		Collection<String> used = this.pservers.values().stream().map(PServer::getServerName)
				.collect(Collectors.toList());
		for (int big = 1; true; big++) {
			for (int major = 1; major <= 10; major++) {
				for (int minor = 1; minor <= 10; minor++) {
					String name = "ps-" + big + "_" + major + "." + minor;
					if (!used.contains(name)) {
						return name;
					}
				}
			}
		}
	}

	@Override
	public JsonDocument getPServerData(UniqueId pserver) {
		return pserverData.get(pserver.toString());
	}

	@Override
	public void setPServerData(UniqueId pserver, JsonDocument data) {
		pserverData.containsAsync(pserver.toString()).fireExceptionOnFailure().onComplete(con -> {
			if (con) {
				pserverData.update(pserver.toString(), data);
			} else {
				pserverData.insert(pserver.toString(), data);
			}
		});
	}

	@Override
	public void clearOwners(UniqueId id) {
		DatabaseProvider.get("pserver").cast(PServerDatabase.class).delete(id);
	}

	@Override
	public Collection<UniqueId> getPServerIDs(UUID owner) {
		return DatabaseProvider.get("pserver").cast(PServerDatabase.class).getPServers(owner);
	}

	public void remove(NodePServer pserver) {
		this.sendUpdate(pserver);
		new PacketNodeWrapperRemovePServer(pserver.getId()).sendAsync();
		this.pservers.remove(pserver.getId());
	}

	private void addOrUpdate(NodePServer pserver) {
		if (!this.pservers.containsKey(pserver.getId())) {
			this.pservers.put(pserver.getId(), pserver);
			new PacketNodeWrapperAddPServer(pserver.getSerializable()).sendAsync();
		}
		this.sendUpdate(pserver);
	}

	private void sendUpdate(NodePServer pserver) {
		new PacketNodeWrapperUpdateInfo(pserver.getSerializable()).sendAsync();
	}

	@Override
	public void addOwner(UniqueId id, UUID owner) {
		DatabaseProvider.get("pserver").cast(PServerDatabase.class).update(id, owner);
		this.getPServerOptional(id).ifPresent(ps -> {
			ps.getOwners().add(owner);
			ps.rebuildSerializable();
		});
		new PacketNodeWrapperAddOwner(id, owner).sendAsync();
	}

	@Override
	public void removeOwner(UniqueId id, UUID owner) {
		DatabaseProvider.get("pserver").cast(PServerDatabase.class).delete(id, owner);
		this.getPServerOptional(id).ifPresent(ps -> {
			ps.getOwners().remove(owner);
			ps.rebuildSerializable();
		});
		new PacketNodeWrapperRemoveOwner(id, owner).sendAsync();
	}

	@Override
	public Optional<NodePServer> getPServerOptional(UniqueId uuid) {
		return Optional.ofNullable(this.getPServer(uuid));
	}

	@Override
	public NodePServer getPServer(UniqueId uuid) {
		return this.pservers.getOrDefault(uuid, null);
	}

	@Override
	public Collection<NodePServer> getPServers() {
		return this.pservers.values();
	}

	@Override
	public Collection<UUID> getOwners(UniqueId pserver) {
		return DatabaseProvider.get("pserver").cast(PServerDatabase.class).getOwners(pserver);
	}

	public static NodePServerProvider getInstance() {
		return NodePServerProvider.instance;
	}

	@Override
	public NodePServer createPServer(PServerSerializable configuration) {
		return this.createPServer(configuration, null);
	}

	@Override
	public NodePServer createPServer(PServerSerializable configuration, ServiceTask task) {
		if (this.isPServer(configuration.id)) {
			return this.getPServer(configuration.id);
		}
		if (task == null) {
			task = this.pserverTask;
		}
		NodePServer pserver =
				this.newPServer(configuration, getPServerData(configuration.id), task);
		this.addOrUpdate(pserver);
		return pserver;
	}

	public Collection<ServiceTemplate> getAllTemplates() {
		return this.storage.getTemplates().stream()
				.filter(s -> s.getPrefix().equals(PServerProvider.getTemplatePrefix()))
				.collect(Collectors.toList());
	}

	public Collection<String> getAllTemplateIDs() {
		return this.storage.getTemplates().stream()
				.filter(s -> s.getPrefix().equals(PServerProvider.getTemplatePrefix()))
				.map(ServiceTemplate::getName)
				.filter(s -> !(s.equals(".world") || s.equals(".global")))
				.collect(Collectors.toList());
	}

	@Override
	public void delete(UniqueId pserver) {
		NodePServerProvider.executor.submit(() -> {
			this.getPServerOptional(pserver).ifPresent(ps -> {
				ServiceInfoSnapshot snap = ps.getSnapshot();
				ps.stop();
				if (snap != null) {
					if (snap.provider() != null)
						snap.provider().kill();
				}
			});
			ServiceTemplate template = PServerProvider.getTemplate(pserver);
			if (this.storage.has(template)) {
				this.storage.delete(template);
			}
		});
	}

	public NodePServer newPServer(PServerSerializable s, JsonDocument data, ServiceTask task) {
		String groupGlobalName = "pserver-global";
		if (!CloudNet.getInstance().getGroupConfigurationProvider()
				.isGroupConfigurationPresent(groupGlobalName)) {
			CloudNet.getInstance().getGroupConfigurationProvider()
					.addGroupConfiguration(new EmptyGroupConfiguration(groupGlobalName));
		}
		ServiceConfiguration.Builder confb = ServiceConfiguration.builder()
				.task(CloudNet.getInstance().getServiceTaskProvider()
						.getServiceTask(task.getName()))
				.task(NodePServerProvider.pserverTaskName).staticService(false)
				.addTemplates(this.globalTemplate).addGroups("pserver-global");
		if (!s.temporary) {
			String groupWorldName = "pserver-world";
			if (!CloudNet.getInstance().getGroupConfigurationProvider()
					.isGroupConfigurationPresent(groupWorldName)) {
				CloudNet.getInstance().getGroupConfigurationProvider()
						.addGroupConfiguration(new EmptyGroupConfiguration(groupWorldName));
			}
			confb.addGroups(groupWorldName);
			confb.addTemplates(this.worldTemplate);
			ServiceTemplate template = PServerProvider.getTemplate(s.id);
			this.storage.create(template);
			confb.addTemplates(template);
			ServiceDeployment deployment = new ServiceDeployment(template,
					PServerModule.getInstance().getDeploymentExclusions());
			confb.addDeployments(deployment);
		}
		confb.node(CloudNet.getInstance().getConfig().getIdentity().getUniqueId());
		ServiceConfiguration conf = confb.build();

		NodePServer ps = new NodePServer(s.id, s.temporary, s.owners, s.serverName, task.getName(),
				conf, data);
		return ps;
	}

	public static void init() {
		NodePServerProvider.instance = new NodePServerProvider();
	}

	public boolean isPServer(UniqueId uniqueId) {
		return this.pservers.containsKey(uniqueId);
	}

	@Override
	public boolean isPServer() {
		return false;
	}

	@Override
	public void setPServerCommand(BiFunction<Object, String[], Boolean> command)
			throws IllegalStateException {
		throw new IllegalStateException();
	}

	@Override
	public PServer getCurrentPServer() throws IllegalStateException {
		throw new IllegalStateException();
	}

}
