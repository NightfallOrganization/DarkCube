/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util.communication;

public final class Messenger {

	public static Messenger messager = new Messenger();
	public static final String MESSAGE_ID_NAME = "msgId";
	public static final String CHANNEL = "darkcube-cloudban-main";
//	public static final String CHANNEL_RECEIVE;
	public static final String CHANNEL_LOG = "darkcube-cloudban-log";
//	private static final int pid = CloudNetDriver.getInstance().getOwnPID();
	
//	private static Set<Integer> USED_IDS = new HashSet<>();
//	private static Map<Message, CountDownLatch> latches = new HashMap<>();
//	private static Map<Message, MessageData> datas = new HashMap<>();
//
//	static {
//		String send = "darkcube-cloudban-client";
//		String receive = "darkcube-cloudban-module";
//		if(CloudNetDriver.getInstance().getDriverEnvironment() == DriverEnvironment.CLOUDNET) {
//			String temp = receive;
//			receive = send;
//			send = temp;
//		}
//		
//		CHANNEL_SEND = send;
//		CHANNEL_RECEIVE = receive;
//	}
	
//	private Messenger() {
//		CloudNetDriver.getInstance().getEventManager().registerListener(this);
//	}
//
//	@EventListener
//	public void handle(ChannelMessageReceiveEvent e) {
//		if (!e.getChannel().equals(CHANNEL_RECEIVE)) {
//			return;
//		}
//		int id = decodeId(e.getData().getString(MESSAGE_ID_NAME));
//		synchronized (latches) {
//			for (Message msg : latches.keySet()) {
//				if (msg.getId() == id) {
//					MessageData data = new MessageData(e.getChannel(), e.getMessage(), e.getData());
//					datas.put(msg, data);
//					latches.get(msg).countDown();
//					return;
//				}
//			}
//		}
//	}
//
//	public static final CompletableFuture<MessageData> awaitMessageAsync(Message message) {
//		return CompletableFuture.supplyAsync(() -> awaitMessage(message));
//	}
//
//	public static final MessageData awaitMessage(Message message) {
//		CountDownLatch latch = new CountDownLatch(1);
//		latches.put(message, latch);
//		boolean val = true;
//		while (latch.getCount() != 0 && val) {
//			try {
//				val = latch.await(5, TimeUnit.SECONDS);
//			} catch (InterruptedException ex) {
//				ex.printStackTrace();
//			}
//		}
//		latches.remove(message);
//		freeId(message.getId());
//		return datas.remove(message);
//	}
//
//	public static final Message sendMessage(ChannelMessage message, JsonDocument data) {
//		int id = getNextId();
//		useId(id);
//		sendMessage(message, data, encode(id));
//		return new Message(id);
//	}
//
//	public static final void sendMessage(ChannelMessage message, JsonDocument data, String messageId) {
//		CloudNetDriver.getInstance().getMessenger().sendChannelMessage(CHANNEL_SEND, message.getMessage(),
//				data.append(MESSAGE_ID_NAME, messageId));
//	}
//
//	public static final void freeId(int id) {
//		USED_IDS.remove(id);
//	}
//
//	private static final void useId(int id) {
//		USED_IDS.add(id);
//	}
//
//	private static final int getNextId() {
//		int id = 1;
//		while (USED_IDS.contains(id)) {
//			id++;
//		}
//		return id;
//	}
//
//	private static final String encode(int id) {
//		return Integer.toString(pid) + "." + Integer.toString(id);
//	}
//
//	public static final int decodeId(String encoded) {
//		return Integer.parseInt(encoded.split("\\.")[1]);
//	}
//
//	public static final class MessageData {
//		private String channel;
//		private String message;
//		private JsonDocument data;
//
//		private MessageData(String channel, String message, JsonDocument data) {
//			this.channel = channel;
//			this.message = message;
//			this.data = data;
//		}
//
//		public String getChannel() {
//			return channel;
//		}
//
//		public JsonDocument getData() {
//			return data;
//		}
//
//		public String getMessage() {
//			return message;
//		}
//	}
//
//	public static final class Message {
//		private int id;
//
//		private Message(int id) {
//			this.id = id;
//		}
//
//		public int getId() {
//			return id;
//		}
//	}
}
