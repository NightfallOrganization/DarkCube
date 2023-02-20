/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common;

import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;
import eu.darkcube.system.pserver.common.PServerExecutor.Type;

import java.util.concurrent.CompletableFuture;

public final class PServerBuilder {

	private AccessLevel accessLevel = AccessLevel.PUBLIC;
	private Type type = Type.WORLD;
	private String taskName = null;

	public PServerBuilder() {
	}

	public AccessLevel accessLevel() {
		return accessLevel;
	}

	public PServerBuilder accessLevel(AccessLevel accessLevel) {
		this.accessLevel = accessLevel;
		return this;
	}

	public Type type() {
		return type;
	}

	public PServerBuilder type(Type type) {
		this.type = type;
		return this;
	}

	public String taskName() {
		return taskName;
	}

	public PServerBuilder taskName(String taskName) {
		this.taskName = taskName;
		return this;
	}

	public CompletableFuture<PServerExecutor> create() {
		return PServerProvider.instance().createPServer(this);
	}

	@Override
	public PServerBuilder clone() {
		return new PServerBuilder().type(type).taskName(taskName).accessLevel(accessLevel);
	}
}
