/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.bukkit.link;

import eu.darkcube.system.annotations.Api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

@Api public class LinkManager {
    private Collection<Link> links = new ArrayList<>();

    @Api public boolean addLink(LinkSupplier supplier) {
        boolean success = false;
        try {
            this.links.add(supplier.get());
            success = true;
        } catch (Throwable t) {
            Logger.getLogger("LinkManager").warning(t.getLocalizedMessage());
        }
        return success;
    }

    @Api public void enableLinks() {
        links.forEach(Link::enable);
        links.stream().filter(PluginLink.class::isInstance).map(PluginLink.class::cast).forEach(PluginLink::registerListener);
    }

    @Api public Collection<Link> links() {
        return Collections.unmodifiableCollection(links);
    }

    @Api public void unregisterLinks() {
        new ArrayList<>(this.links).forEach(this::unregisterLink);
    }

    @Api public void unregisterLink(Link link) {
        if (link instanceof PluginLink) {
            ((PluginLink) link).unregisterListener();
        }
        link.disable();
        link.unlink();
        links.remove(link);
    }

    @Api public interface LinkSupplier {
        @Api Link get() throws Throwable;
    }
}
