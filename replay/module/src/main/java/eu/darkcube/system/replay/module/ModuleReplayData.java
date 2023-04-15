/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.replay.module;

import eu.darkcube.system.replay.api.ReplayData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

public class ModuleReplayData implements ReplayData {
	private final int id;
	private final ReplayModule module;
	private final Path path;
	private final CompletableFuture<Void> completeFuture = new CompletableFuture<>();
	private volatile boolean complete = false;

	public ModuleReplayData(ReplayModule module, int id) {
		this.module = module;
		this.id = id;
		this.path = this.module.dataDirectory().resolve(id + ".replay");
		if (!Files.exists(path)) {
			try {
				Files.createFile(path);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		completeFuture.thenRun(() -> complete = true);
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public ModuleReplayDataByteReaderStream createReaderStream() {
		try {
			return new ModuleReplayDataByteReaderStream(
					Files.newByteChannel(path, StandardOpenOption.READ));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public ModuleReplayDataByteWriterStream createWriterStream() {
		try {
			return new ModuleReplayDataByteWriterStream(
					Files.newByteChannel(path, StandardOpenOption.WRITE,
					                     StandardOpenOption.TRUNCATE_EXISTING));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean complete() {
		return complete;
	}

	@Override
	public CompletableFuture<Void> completeFuture() {
		return completeFuture;
	}

	public Path path() {
		return path;
	}

	public ReplayModule module() {
		return module;
	}
}
