package eu.darkcube.system.pserver.plugin.link;

import java.io.Closeable;
import java.io.IOException;

public abstract class Link implements Closeable {

	public Link() throws Throwable {
		this.link();
	}

	protected abstract void link() throws Throwable;

	protected abstract void unlink();

	@Override
	public final void close() throws IOException {
		unlink();
	}
}
