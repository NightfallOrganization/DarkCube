package eu.darkcube.system.pserver.plugin.link;

import java.util.ArrayList;
import java.util.Collection;

public class LinkManager {

	private Collection<Link> links = new ArrayList<>();

	public boolean addLink(LinkSupplier supplier) {
		boolean success = false;
		try {
			this.links.add(supplier.get());
			success = true;
		} catch (Throwable ex) {
		}
		return success;
	}

	public Collection<Link> getLinks() {
		return links;
	}

	public void unregisterLinks() {
		new ArrayList<>(this.links).forEach(this::unregisterLink);
	}

	public void unregisterLink(Link link) {
		link.unlink();
		links.remove(link);
	}

	public static interface LinkSupplier {
		Link get() throws Throwable;
	}
}