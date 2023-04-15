/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.replay.api;

import java.util.concurrent.CompletableFuture;

/**
 * This represents the data of a replay. This data may be incomplete and already accessible, to show
 * a part of the replay while still downloading the rest, for example
 */
public interface ReplayData {

	int id();

	ReplayDataByteReaderStream createReaderStream();

	boolean complete();

	CompletableFuture<Void> completeFuture();

}
