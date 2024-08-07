/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.link;

import java.io.Closeable;

public abstract class Link implements Closeable {

    public Link() throws Throwable {
        this.link();
    }

    protected abstract void link() throws Throwable;

    protected abstract void unlink();

    @Override public final void close() {
        unlink();
    }
}
