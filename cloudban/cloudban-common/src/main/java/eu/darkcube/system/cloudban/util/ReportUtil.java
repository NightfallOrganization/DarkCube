package eu.darkcube.system.cloudban.util;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import eu.darkcube.system.cloudban.util.communication.EnumChannelMessage;
import eu.darkcube.system.cloudban.util.communication.Messenger;

public class ReportUtil {

	public static Report getReport(int id) {
//		Message msg = Messenger.sendMessage(EnumChannelMessage.REPORT_GET_BY_ID, new JsonDocument().append("repid", id));
//		MessageData data = Messenger.awaitMessage(msg);
//		if (!data.getData().contains("id")) {
//			return null;
//		}
//		return Report.fromDocument(data.getData());
		ChannelMessage query = ChannelMessage.builder().channel(Messenger.CHANNEL)
				.message(EnumChannelMessage.REPORT_GET_BY_ID.getMessage()).json(new JsonDocument().append("repid", id))
				.build().sendSingleQuery();
		if (query == null || !query.getJson().contains("id")) {
			return null;
		}
		return Report.fromDocument(query.getJson());
	}

	public static int newId() {
//		Message msg = Messenger.sendMessage(EnumChannelMessage.REPORT_GET_NEW_ID, new JsonDocument());
//		MessageData data = Messenger.awaitMessage(msg);
//		return Integer.parseInt(data.getData().getString("repid") == null ? "0" : data.getData().getString("repid"));
		ChannelMessage query = ChannelMessage.builder().channel(Messenger.CHANNEL)
				.message(EnumChannelMessage.REPORT_GET_NEW_ID.getMessage()).build().sendSingleQuery();
		if (query == null)
			return 0;
		return query.getJson().getString("repid") == null ? 0 : Integer.parseInt(query.getJson().getString("repid"));
	}
}
