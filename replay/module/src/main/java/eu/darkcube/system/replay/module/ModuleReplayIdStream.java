/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.replay.module;

import eu.darkcube.system.replay.api.ReplayIdStream;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class ModuleReplayIdStream implements ReplayIdStream {
	private static final String FPATTERN = "*.replay";
	private static final Pattern JPATTERN = Pattern.compile("^[0-9]+$");
	private final DirectoryStream<Path> replays;
	private final Iterator<Path> iterator;
	private boolean hasNext;
	private int next;

	public ModuleReplayIdStream(ReplayModule module) {
		try {
			replays = Files.newDirectoryStream(module.dataDirectory(), FPATTERN);
			iterator = replays.iterator();
			next0();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void next0() {
		do {
			if (!iterator.hasNext()) {
				hasNext = false;
				return;
			}
			Path next = iterator.next();
			String s = next.getFileName().toString();
			s = s.substring(0, s.length() - FPATTERN.length() + 1);
			if (!JPATTERN.matcher(s).matches()) {
				continue;
			}
			try {
				this.next = Integer.parseInt(s);
			} catch (Throwable e) {
				e.printStackTrace();
				continue;
			}
			hasNext = true;
		} while (true);
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public int next() {
		if (!hasNext)
			throw new NoSuchElementException();
		int cur = next;
		next0();
		return cur;
	}

	@Override
	public void close() {
		try {
			replays.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
