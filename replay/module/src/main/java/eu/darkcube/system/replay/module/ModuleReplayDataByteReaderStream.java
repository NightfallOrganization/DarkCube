/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.replay.module;

import eu.darkcube.system.replay.api.ReplayDataByteReaderStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.concurrent.atomic.AtomicLong;

public class ModuleReplayDataByteReaderStream implements ReplayDataByteReaderStream {
	private final SeekableByteChannel channel;
	private final AtomicLong pos = new AtomicLong(0);
	private final ByteBuffer lbuf;
	private volatile long length = -1;

	public ModuleReplayDataByteReaderStream(SeekableByteChannel channel) {
		this.channel = channel;
		try {
			channel.position(channel.position() + 8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		lbuf = ByteBuffer.allocate(Long.BYTES);
	}

	@Override
	public int read(ByteBuffer buffer) {
		try {
			int len = channel.read(buffer);
			if (len == -1) {
				if (length == -1)
					queryLength();
				if (length == -1)
					return 0;
				if (this.pos.get() < length) {
					return 0;
				}
			} else
				this.pos.addAndGet(len);
			return len;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			channel.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void queryLength() throws IOException {
		long pos = channel.position();
		if (length == -1) {
			channel.position(0);
			int r = channel.read(lbuf);
			if (r == 8) {
				length = lbuf.getLong();
			}
			channel.position(pos);
		}
	}

}
