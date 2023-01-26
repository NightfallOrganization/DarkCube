/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.replay.module.network;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.IPacketListener;
import de.dytanic.cloudnet.driver.network.protocol.Packet;
import de.dytanic.cloudnet.driver.serialization.ProtocolBuffer;
import eu.darkcube.system.replay.api.ReplayConstants;
import eu.darkcube.system.replay.module.ModuleReplayData;
import eu.darkcube.system.replay.module.ModuleReplayDataByteWriterStream;
import eu.darkcube.system.replay.module.ReplayModule;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketReceiver implements IPacketListener {
	private static final AtomicInteger receiverHandlerId = new AtomicInteger(0);
	private final ExecutorService service;
	private final ReplayModule module;
	private final ConcurrentMap<Integer, DownloadEntry> downloading = new ConcurrentHashMap<>();

	public PacketReceiver(ReplayModule module) {
		this.module = module;
		CloudNetDriver.getInstance().getNetworkClient().getPacketRegistry()
				.addListener(ReplayConstants.CHANNEL, this);
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		service = Executors.newCachedThreadPool(r -> {
			Thread t = new Thread(group, r,
			                      "replay-packet-handler-" + receiverHandlerId.incrementAndGet());
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		});
	}

	@Override
	public void handle(INetworkChannel channel, IPacket packet) {
		final ProtocolBuffer buf = packet.getBuffer();
		final int type = buf.readVarInt();
		if (type == ReplayConstants.TYPE_START_DOWNLOAD) { // Start downloading replayData
			final int id = buf.readVarInt();
			ModuleReplayData data = new ModuleReplayData(module, id);
			downloading.put(id, new DownloadEntry(data));
		} else if (type == ReplayConstants.TYPE_DOWNLOAD_DATA) { // Download entry
			final int id = buf.readVarInt();
			DownloadEntry e = downloading.get(id);
			service.submit(() -> {
				byte[] a = buf.readArray();
				int read = 0;
				int wrote = 0;
				do {
					int r = Math.min(e.buffer.capacity(), a.length) - read;
					e.buffer.put(a, read, r);
					read += r;
					e.buffer.flip();
					int wroteHere = 0;
					do {
						int w = e.writer.write(e.buffer);
						wroteHere += w;
					} while (wroteHere != r);
					wrote += wroteHere;
					e.buffer.position(0);
					e.buffer.limit(e.buffer.capacity());
				} while (wrote != a.length);
				e.position.addAndGet(wrote);
				ProtocolBuffer pbuf = ProtocolBuffer.create();
				pbuf.writeVarInt(ReplayConstants.TYPE_REQUEST_MORE_DOWNLOAD_DATA);
				pbuf.writeVarInt(id);
				pbuf.writeVarInt(e.position.get());
				channel.sendPacket(new Packet(-1, pbuf));
			});
		} else if (type == ReplayConstants.TYPE_DOWNLOAD_COMPLETE) { // Download finished
			final int id = buf.readVarInt();
			DownloadEntry e = downloading.remove(id);
			service.submit(e.writer::close);
		}
	}

	public void shutdown() {
		service.shutdown();
	}

	private static class DownloadEntry {
		private final ModuleReplayData data;
		private final ByteBuffer buffer = ByteBuffer.allocate(4096);
		private final ModuleReplayDataByteWriterStream writer;
		private final AtomicInteger position = new AtomicInteger(0);

		public DownloadEntry(ModuleReplayData data) {
			this.data = data;
			this.writer = this.data.createWriterStream();
		}
	}
}
