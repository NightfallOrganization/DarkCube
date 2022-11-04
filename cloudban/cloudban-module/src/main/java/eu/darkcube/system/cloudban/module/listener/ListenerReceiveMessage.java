package eu.darkcube.system.cloudban.module.listener;

import java.util.*;
import java.util.stream.*;

import com.google.gson.*;

import de.dytanic.cloudnet.common.document.gson.*;
import de.dytanic.cloudnet.driver.*;
import de.dytanic.cloudnet.driver.channel.*;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.*;
import de.dytanic.cloudnet.ext.bridge.player.*;
import eu.darkcube.system.cloudban.util.*;
import eu.darkcube.system.cloudban.util.communication.*;

public class ListenerReceiveMessage {

	private static Gson gson = new Gson();

	public ListenerReceiveMessage() {
		CloudNetDriver.getInstance().getEventManager().registerListener(this);
	}

	private Collection<Report> pending = new HashSet<>();

	private Collection<Integer> getIds() {
		return pending.stream().map(Report::getId).collect(Collectors.toSet());
	}

	@EventListener
	public void handle(ChannelMessageReceiveEvent e) {
//		CloudNetDriver.getInstance().getLogger().warning("received: " + e.getMessage() + ", " + e.getData().toJson());
//		if (e.getMessage().equals(EnumChannelMessage.BOT_LOG.getMessage())) {
////			Module.getCloudNet().getLogger().warning("logging " + e.getData().getString("msg"));
//			Main.getInstance().getModuleBans().log(e.getData().getString("msg") + "");
//			return;
//		}
		if (e.getChannel().equals(Messenger.CHANNEL_LOG)) {
			// TODO Reimplement DiscordBot
//			Bot.getInstance().getModuleBans().log(e.getMessage());
			return;
		}
		if (!e.getChannel().equals(Messenger.CHANNEL)) {
			return;
		}
		if (e.getMessage().equals(EnumChannelMessage.REPORT_PENDING_NEW.getMessage())) {
			Report report = Report.fromDocument(e.getData());
			pending.add(report);
			JsonArray array = new JsonArray();
			array.add(gson.fromJson(report.toDocument().append("uuid", "all").toJson(), JsonObject.class));
//			Messenger.sendMessage(EnumChannelMessage.REPORT_SEND_USER_DETAILS, new JsonDocument().append("data", array),
//					"0.0");
			ChannelMessage.builder().targetServices().channel(Messenger.CHANNEL).json(new JsonDocument().append("data", array)).message(EnumChannelMessage.REPORT_SEND_USER_DETAILS.getMessage()).build().send();
			// TODO Reimplement DiscordBot
//			Bot.getInstance().getModuleBans().log(report.getPlayer().getName()
//							+ " wurde von " + report.getCreator().getName()
//							+ " für " + report.getReason().getKey() + " ("
//							+ report.getReason().getDisplay() + ") reportet!");
		} else if (e.getMessage().equals(EnumChannelMessage.REPORT_GET_NEW_ID.getMessage())) {
			Random r = new Random();
			int id = r.nextInt();
			do {
				id = r.nextInt();
			} while (getIds().contains(id));
//			Messenger.sendMessage(EnumChannelMessage.ANSWER, new JsonDocument().append("repid", Integer.toString(id)),
//					e.getData().getString(Messenger.MESSAGE_ID_NAME));
			e.setQueryResponse(ChannelMessage.buildResponseFor(e.getChannelMessage()).json(new JsonDocument().append("repid", Integer.toString(id))).build());
		} else if (e.getMessage().equals(EnumChannelMessage.REPORT_GET_ALL.getMessage())) {
			JsonArray data = new JsonArray();
			for (Report report : pending) {
				data.add(new Gson().fromJson(report.toDocument().toJson(), com.google.gson.JsonElement.class));
			}
			JsonDocument doc = new JsonDocument();
			doc.append("data", data.toString());
//			Messenger.sendMessage(EnumChannelMessage.ANSWER, doc, e.getData().getString(Messenger.MESSAGE_ID_NAME));
			e.setQueryResponse(ChannelMessage.buildResponseFor(e.getChannelMessage()).json(doc).build());
		} else if (e.getMessage().equals(EnumChannelMessage.REPORT_GET_BY_ID.getMessage())) {
			int id = e.getData().getInt("repid");
			for (Report r : pending) {
				if (r.getId() == id) {
//					Messenger.sendMessage(EnumChannelMessage.ANSWER, r.toDocument(),
//							e.getData().getString(Messenger.MESSAGE_ID_NAME));
					e.setQueryResponse(ChannelMessage.buildResponseFor(e.getChannelMessage()).json(r.toDocument()).build());
					return;
				}
			}
			e.setQueryResponse(ChannelMessage.buildResponseFor(e.getChannelMessage()).json(new JsonDocument()).build());
//			Messenger.sendMessage(EnumChannelMessage.ANSWER, new JsonDocument(),
//					e.getData().getString(Messenger.MESSAGE_ID_NAME));
		} else if (e.getMessage().equals(EnumChannelMessage.REPORT_LIST_FOR_USER.getMessage())) {
			UUID uuid = UUID.fromString(e.getData().getString("uuid"));
			ICloudPlayer p = Util.getManager().getOnlinePlayer(uuid);
			if (p == null) {
				return;
			}
			if (pending.size() == 0) {
				Util.getManager().getPlayerExecutor(p).sendChatMessage("§cEs gibt keine Ausstehenden Reports");
			} else {
				List<Report> l = new ArrayList<>(pending);
				JsonArray array = new JsonArray();
				for (int i = 0; i < pending.size(); i++) {
					array.add(gson.fromJson(l.get(i).toDocument().append("uuid", "all").toJson(), JsonObject.class));
//					Messager.sendMessage(ChannelMessage.REPORT_SEND_USER_DETAILS,
//							l.get(i).toDocument().append("uuid", uuid.toString()), "0.0");
				}
//				Messenger.sendMessage(EnumChannelMessage.REPORT_SEND_USER_DETAILS,
//						new JsonDocument().append("data", array), "0.0");
				
				e.setQueryResponse(ChannelMessage.buildResponseFor(e.getChannelMessage()).json(new JsonDocument().append("data", array)).build());
			}
		} else if (e.getMessage().equals(EnumChannelMessage.REPORT_REMOVE.getMessage())) {
			Report report = null;
			int id = e.getData().getInt("id");
			for (Report r : pending) {
				if (r.getId() == id) {
					report = r;
					break;
				}
			}
			if (report == null) {
				return;
			}
			pending.remove(report);
		}
	}
}