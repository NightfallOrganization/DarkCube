/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common.packets;

import eu.darkcube.system.pserver.common.PServerSerializable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class PacketNodeWrapperPServers extends Packet implements Iterable<PServerSerializable> {

	private Collection<PServerSerializable> pservers = new HashSet<>();

	public PacketNodeWrapperPServers(Collection<PServerSerializable> pservers) {
		this.pservers.addAll(pservers);
	}

	public Collection<PServerSerializable> getPServers() {
		return pservers;
	}

	@Override
	public Iterator<PServerSerializable> iterator() {
		return pservers.iterator();
	}
}
