/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.replay.module;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.concurrent.atomic.AtomicLong;

public class ModuleReplayDataByteWriterStream implements AutoCloseable {

	private final SeekableByteChannel channel;
	private final AtomicLong length = new AtomicLong(0);

	public ModuleReplayDataByteWriterStream(SeekableByteChannel channel) {
		this.channel = channel;
	}

	public int write(ByteBuffer buffer) {
		try {
			if (channel.position() == 0) {
				ByteBuffer lbuf = ByteBuffer.allocate(Long.BYTES);
				lbuf.putLong(-1);
				lbuf.flip();
				while (lbuf.hasRemaining()) {
					channel.write(lbuf);
					lbuf.compact();
				}
			}
			int written = channel.write(buffer);
			length.addAndGet(written);
			return written;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			channel.position(0);
			ByteBuffer lbuf = ByteBuffer.allocate(Long.BYTES);
			lbuf.putLong(length.get());
			lbuf.flip();
			while (lbuf.hasRemaining()) {
				channel.write(lbuf);
				lbuf.compact();
			}
			channel.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
