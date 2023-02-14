/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceId;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.executor.PlayerExecutor;
import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperUpdateInfo;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class NodePServer implements PServer {

	private UniqueId id;
	private ServiceId serviceId;
	private ServiceInfoSnapshot snapshot;
	private State state = State.OFFLINE;
	private boolean temporary;
	private long startedAt = System.currentTimeMillis();
	private Collection<UUID> owners;
	private String serverName;
	private String taskName;

	private JsonDocument data;
	private ServiceConfiguration conf;
	private PServerSerializable serializable;

	private volatile boolean stopping = false;

	public NodePServer(UniqueId id, boolean temporary, String serverName, String taskName,
			ServiceConfiguration conf, JsonDocument data) {
		this.taskName = taskName;
		this.id = id;
		this.data = data;
		this.temporary = temporary;
		setOwners(null);
		this.serverName = serverName;
		this.conf = conf;
		this.createSerializable();
	}

	public void createSerializable() {
		this.serializable =
				new PServerSerializable(id, getOnlinePlayers(), temporary, startedAt, taskName,
						serverName, state);
	}

	public void rebuildSerializable() {
		this.createSerializable();
		new PacketNodeWrapperUpdateInfo(serializable).sendAsync();
	}

	public ServiceInfoSnapshot getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(ServiceInfoSnapshot snapshot) {
		this.snapshot = snapshot;
		this.serviceId = snapshot == null ? null : snapshot.getServiceId();
		rebuildSerializable();
	}

	@Override
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
		rebuildSerializable();
		if (state == State.OFFLINE) {
			stopping = false;
		}
	}

	@Override
	public boolean start() {
		if (state == State.RUNNING || state == State.STARTING) {
			return false;
		}
		System.out.println("Starting PServer " + getServerName());
		setState(State.STARTING);
		conf.createNewServiceAsync().fireExceptionOnFailure().onComplete(s -> {
			setSnapshot(s);
			snapshot.provider().startAsync().fireExceptionOnFailure().onComplete(v -> {
				if (!stopping) {
					System.out.println("Started PServer " + getServerName());
				}
			});
		});
		return true;
	}

	@Override
	public boolean stop() {
		stopping = true;
		if (state == State.RUNNING) {
			System.out.println("Stopping PServer " + getServerName());
			setState(State.STOPPING);

			snapshot.provider().stopAsync().fireExceptionOnFailure().onComplete(s -> {
				snapshot.provider().deleteAsync().fireExceptionOnFailure().onComplete(f -> {
					setState(State.OFFLINE);
					setSnapshot(null);
					System.out.println("Stopped PServer " + getServerName());
				});
			});
			return true;
		} else if (state == State.STARTING) {
			System.out.println("Killing PServer " + getServerName());
			setState(State.STOPPING);
			snapshot.provider().deleteAsync().onComplete(s -> {
				setState(State.OFFLINE);
				setSnapshot(null);
				System.out.println("Killed PServer " + getServerName());
			});
			return true;
		}
		return false;
	}

	@Override
	public void remove() {
		stop();
		NodePServerProvider.getInstance().remove(this);
	}

	@Override
	public int getOnlinePlayers() {
		if (snapshot == null) {
			return 0;
		}
		Optional<Integer> o = snapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT);
		return (state == State.RUNNING && o.isPresent()) ? o.get() : 0;
	}

	@Override
	public boolean isGamemode() {
		return temporary;
	}

	@Override
	public boolean isPrivate() {
		if (data.contains("private")) {
			return data.getBoolean("private");
		}
		setPrivate(true);
		return true;
	}

	@Override
	public void setPrivate(boolean privateServer) {
		data.append("private", privateServer);
		NodePServerProvider.getInstance().setPServerData(id, data);
	}

	@Override
	public boolean isPublic() {
		return !isPrivate();
	}

	@Override
	public UniqueId getId() {
		return id;
	}

	@Override
	public long getOntime() {
		return System.currentTimeMillis() - startedAt;
	}

	@Override
	public Collection<UUID> getOwners() {
		return owners;
	}

	private void setOwners(Collection<UUID> owners) {
		if (owners == null) {
			owners = DatabaseProvider.get("pserver").cast(PServerDatabase.class).getOwners(id);
		}
		this.owners = owners;
	}

	@Override
	public String getServerName() {
		return serverName;
	}

	@Override
	public long getStartedAt() {
		return startedAt;
	}

	@Override
	public JsonDocument getData() {
		return data;
	}

	@Override
	public PServerSerializable getSerializable() {
		return serializable;
	}

	@Override
	public String getTaskName() {
		return taskName;
	}

	@Override
	public void connectPlayer(UUID player) {
		ServiceInfoSnapshot snapshot = this.snapshot;
		if (snapshot == null) {
			return;
		}
		AsyncExecutor.service().submit(() -> {
			IPlayerManager pm = CloudNetDriver.getInstance().getServicesRegistry()
					.getFirstService(IPlayerManager.class);
			ICloudPlayer cp = pm.getOnlinePlayer(player);
			if (cp == null) {
				return;
			}
			PlayerExecutor ex = pm.getPlayerExecutor(cp);
			ex.connect(snapshot.getName());
		});
	}

	public ServiceId getServiceId() {
		return serviceId;
	}
}
