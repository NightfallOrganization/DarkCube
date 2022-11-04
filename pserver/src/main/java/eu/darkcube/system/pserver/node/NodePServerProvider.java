package eu.darkcube.system.pserver.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.*;
import java.util.stream.Collectors;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ProcessConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceDeployment;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceRemoteInclusion;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import de.dytanic.cloudnet.driver.template.TemplateStorage;
import de.dytanic.cloudnet.template.LocalTemplateStorage;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperAddOwner;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperAddPServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperRemoveOwner;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperRemovePServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperUpdateInfo;
import eu.darkcube.system.pserver.node.database.DatabaseProvider;
import eu.darkcube.system.pserver.node.database.PServerDatabase;

public class NodePServerProvider extends PServerProvider {

	private static final ExecutorService executor = Executors.newFixedThreadPool(1);

	private static NodePServerProvider instance = new NodePServerProvider();

	public ServiceTask worldPServer;
	public ServiceTemplate globalTemplate;
	public ServiceTemplate worldTemplate;
	private TemplateStorage storage = CloudNetDriver.getInstance().getLocalTemplateStorage();

	private NodePServerProvider() {

		// ServiceTask
		{
			String name = "pserver";
			if (!CloudNet.getInstance().getServiceTaskProvider().isServiceTaskPresent(name)) {
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

				worldPServer = new ServiceTask(includes, templates, deployments, name, runtime, maintenance,
						autoDeleteOnStop, staticServices, associatedNodes, groups, deletedFilesAfterStop,
						processConfiguration, startPort, minServiceCount);
				CloudNet.getInstance().getServiceTaskProvider().addPermanentServiceTask(worldPServer);
			} else {
				worldPServer = CloudNet.getInstance().getServiceTaskProvider().getServiceTask(name);
			}
		}
		// ServiceTemplates
		{
			String name = ".global";
			globalTemplate = new ServiceTemplate(getTemplatePrefix(), name,
					LocalTemplateStorage.LOCAL_TEMPLATE_STORAGE);

			if (!storage.has(globalTemplate)) {
				storage.create(globalTemplate);
				try {
					storage.createDirectory(globalTemplate, "plugins");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				try {
					storage.createFile(globalTemplate, "plugins/" + PServerModule.PLUGIN_NAME);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			name = ".world";
			worldTemplate = new ServiceTemplate(getTemplatePrefix(), name, LocalTemplateStorage.LOCAL_TEMPLATE_STORAGE);
			if (!storage.has(worldTemplate)) {
				storage.create(worldTemplate);
			}
		}

	}

	private Map<UniqueId, NodePServer> pservers = new HashMap<>();

	@Override
	public String newName() {
		Collection<String> used = pservers.values().stream().map(PServer::getServerName).collect(Collectors.toList());
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
	public void clearOwners(UniqueId id) {
		DatabaseProvider.get("pserver").cast(PServerDatabase.class).delete(id);
	}

	@Override
	public Collection<UniqueId> getPServerIDs(UUID owner) {
		return DatabaseProvider.get("pserver").cast(PServerDatabase.class).getPServers(owner);
	}

	public void remove(NodePServer pserver) {
		sendUpdate(pserver);
		new PacketNodeWrapperRemovePServer(pserver.getId()).sendAsync();
		pservers.remove(pserver.getId());
	}

	private void addOrUpdate(NodePServer pserver) {
		if (!pservers.containsKey(pserver.getId())) {
			pservers.put(pserver.getId(), pserver);
			new PacketNodeWrapperAddPServer(pserver.getSerializable()).sendAsync();
		}
		sendUpdate(pserver);
	}

	private void sendUpdate(NodePServer pserver) {
		new PacketNodeWrapperUpdateInfo(pserver.getSerializable()).sendAsync();
	}

	@Override
	public void addOwner(UniqueId id, UUID owner) {
		DatabaseProvider.get("pserver").cast(PServerDatabase.class).update(id, owner);
		getPServerOptional(id).ifPresent(ps -> {
			ps.getOwners().add(owner);
			ps.rebuildSerializable();
		});
		new PacketNodeWrapperAddOwner(id, owner).sendAsync();
	}

	@Override
	public void removeOwner(UniqueId id, UUID owner) {
		DatabaseProvider.get("pserver").cast(PServerDatabase.class).delete(id, owner);
		getPServerOptional(id).ifPresent(ps -> {
			ps.getOwners().remove(owner);
			ps.rebuildSerializable();
		});
		new PacketNodeWrapperRemoveOwner(id, owner).sendAsync();
	}

	@Override
	public Optional<NodePServer> getPServerOptional(UniqueId uuid) {
		return Optional.ofNullable(getPServer(uuid));
	}

	@Override
	public NodePServer getPServer(UniqueId uuid) {
		return pservers.getOrDefault(uuid, null);
	}

	@Override
	public Collection<NodePServer> getPServers() {
		return pservers.values();
	}

	@Override
	public Collection<UUID> getOwners(UniqueId pserver) {
		return DatabaseProvider.get("pserver").cast(PServerDatabase.class).getOwners(pserver);
	}

	public static NodePServerProvider getInstance() {
		return instance;
	}

	@Override
	public NodePServer createPServer(PServerSerializable configuration) {
		return createPServer(configuration, null);
	}

	@Override
	public NodePServer createPServer(PServerSerializable configuration, ServiceTask task) {
		if (isPServer(configuration.id)) {
			return getPServer(configuration.id);
		}
		if (task == null) {
			task = worldPServer;
		}
		NodePServer pserver = newPServer(configuration, task);
		addOrUpdate(pserver);
		return pserver;
	}

	public Collection<ServiceTemplate> getAllTemplates() {
		return storage.getTemplates().stream().filter(s -> s.getPrefix().equals(getTemplatePrefix()))
				.collect(Collectors.toList());
	}

	public Collection<String> getAllTemplateIDs() {
		return storage.getTemplates().stream().filter(s -> s.getPrefix().equals(getTemplatePrefix()))
				.map(ServiceTemplate::getName).filter(s -> !(s.equals(".world") || s.equals(".global")))
				.collect(Collectors.toList());
	}

	@Override
	public void delete(UniqueId pserver) {
		executor.submit(() -> {
			getPServerOptional(pserver).ifPresent(ps -> {
				ServiceInfoSnapshot snap = ps.getSnapshot();
				ps.stop();
				if (snap != null) {
					if (snap.provider() != null)
						snap.provider().kill();
				}
			});
			ServiceTemplate template = PServerProvider.getTemplate(pserver);
			if (storage.has(template)) {
				storage.delete(template);
			}
		});
	}

	public NodePServer newPServer(PServerSerializable s, ServiceTask task) {
		ServiceConfiguration.Builder confb = ServiceConfiguration.builder().task(task).task(worldPServer.getName())
				.staticService(false).addTemplates(this.globalTemplate);
		if (!s.temporary) {
			confb.addTemplates(this.worldTemplate);
			ServiceTemplate template = PServerProvider.getTemplate(s.id);
			storage.create(template);
			confb.addTemplates(template);
			ServiceDeployment deployment = new ServiceDeployment(template,
					PServerModule.getInstance().getDeploymentExclusions());
			confb.addDeployments(deployment);
		}
		confb.node(CloudNet.getInstance().getConfig().getIdentity().getUniqueId());
		ServiceConfiguration conf = confb.build();

		NodePServer ps = new NodePServer(s.id, s.privateServer, s.temporary, s.owners, s.serverName, task.getName(),
				conf);
		return ps;
	}

	public static void init() {

	}

	public boolean isPServer(UniqueId uniqueId) {
		return pservers.containsKey(uniqueId);
	}

	@Override
	public boolean isPServer() {
		return false;
	}

	@Override
	public void setPServerCommand(BiFunction<Object, String[], Boolean> command) throws IllegalStateException {
		throw new IllegalStateException();
	}

	@Override
	public PServer getCurrentPServer() throws IllegalStateException {
		throw new IllegalStateException();
	}
}
