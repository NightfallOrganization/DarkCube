/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LinkManager {
	private Collection<Link> links = new ArrayList<>();

	public boolean addLink(LinkSupplier supplier) {
		boolean success = false;
		try {
			this.links.add(supplier.get());
			success = true;
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return success;
	}

	public void enableLinks() {
		links.forEach(Link::enable);
		links.stream().filter(PluginLink.class::isInstance).map(PluginLink.class::cast)
				.forEach(PluginLink::registerListener);
	}

	public Collection<Link> links() {
		return Collections.unmodifiableCollection(links);
	}

	public void unregisterLinks() {
		new ArrayList<>(this.links).forEach(this::unregisterLink);
	}

	public void unregisterLink(Link link) {
		if (link instanceof PluginLink) {
			((PluginLink) link).unregisterListener();
		}
		link.disable();
		link.unlink();
		links.remove(link);
	}

	public interface LinkSupplier {
		Link get() throws Throwable;
	}
}
