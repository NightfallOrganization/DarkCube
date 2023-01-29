/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bungee.party;

import java.io.Closeable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;
import net.md_5.bungee.event.EventHandler;

public class Party implements Closeable {

	private static BiMap<UUID, Party> parties = HashBiMap.create();

	private UUID owner;
	private Map<UUID, Permission> players = new HashMap<>();
	private Set<UUID> invited = new HashSet<>();

	private Listener listener = new Listener();

	public Party(UUID owner) {
		setOwner(owner);
		ProxyServer.getInstance().getPluginManager().registerListener(Main.getInstance(), listener);
	}

	private class Listener implements net.md_5.bungee.api.plugin.Listener {

		@EventHandler
		public void handle(PlayerDisconnectEvent e) {
			players.remove(e.getPlayer().getUniqueId());
			invited.remove(e.getPlayer().getUniqueId());
			if (e.getPlayer().getUniqueId().equals(owner)) {
				Party.this.close();
			}
		}
	}

	public static class Permission {

		public boolean owner = false;
		public boolean canKick = false;
		public boolean canInvite = false;
		public boolean canPromoteToModerator = false;
		public boolean canDemoteToPlayer = false;
		public boolean canPull = false;
		public boolean canJump = true;

	}

	public void addPlayer(UUID uuid) {
		setPermission(uuid, new Permission());
	}

	public void setPermission(UUID uuid, Permission permission) {
		players.put(uuid, permission);
	}

	public Set<UUID> getPlayers() {
		return players.keySet();
	}

	public void invite(ProxiedPlayer p) {
		invited.add(p.getUniqueId());
		Main.sendMessage(p,
				"Du wurdest in die Party von " + ProxyServer.getInstance().getPlayer(owner).getName() + " eingeladen!");
	}
	
	public void kick() {
		
	}

	public void jump(ProxiedPlayer p) {
		ProxiedPlayer op = ProxyServer.getInstance().getPlayer(owner);
		p.connect(op.getServer().getInfo(), Reason.PLUGIN);
	}

	public void pull(ProxiedPlayer p) {
		pull(p.getServer().getInfo());
	}

	public void pull(ServerInfo info) {
		ProxyServer s = ProxyServer.getInstance();
		for (UUID uuid : getPlayers()) {
			ProxiedPlayer p = s.getPlayer(uuid);
			p.connect(info, Reason.PLUGIN);
		}
	}

	public Permission getPermission(UUID uuid) {
		return players.get(uuid);
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
		parties.inverse().put(this, owner);
		Permission perm = new Permission();
		perm.canKick = true;
		perm.canInvite = true;
		perm.canPromoteToModerator = true;
		perm.canDemoteToPlayer = true;
		perm.canPull = true;
		perm.canJump = true;
		players.put(owner, perm);
	}

	public UUID getOwner() {
		return owner;
	}

	@Override
	public void close() {
		ProxyServer.getInstance().getPluginManager().unregisterListener(listener);
	}

	public static Party getParty(UUID uuid) {
		return parties.get(uuid);
	}
}
