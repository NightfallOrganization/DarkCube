package eu.darkcube.system.cloudban.util;

import java.util.*;

import com.google.gson.*;

import de.dytanic.cloudnet.common.document.gson.*;
import de.dytanic.cloudnet.ext.bridge.player.*;
import eu.darkcube.system.cloudban.util.ban.*;

public class Report {

//	public static final Collection<Report> PENDING_REPORTS = new ArrayList<>();
//	private static Collection<Integer> ids = new HashSet<>();

	private int id;
	private Reason reason;
	private ICloudOfflinePlayer player;
	private ICloudOfflinePlayer creator;

	public Report(Reason reason, ICloudOfflinePlayer player, ICloudOfflinePlayer creator, int id) {
		this.reason = reason;
		this.player = player;
		this.creator = creator;
		this.id = id;
	}

	public ICloudOfflinePlayer getPlayer() {
		return player;
	}

	public Reason getReason() {
		return reason;
	}

	public ICloudOfflinePlayer getCreator() {
		return creator;
	}

	public int getId() {
		return id;
	}

	public static int newId() {
//		int id = 0;
//		do {
//			id = new Random().nextInt();
//		} while (ids.contains(id));
//		return id;
		return ReportUtil.newId();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Report) {
			Report r = (Report) obj;
			return r.reason.equals(reason) && r.player.equals(player) && r.creator.equals(creator);
		}
		return super.equals(obj);
	}

	public static Report byId(int id) {
//		if(!ids.contains(id)) {
//			return null;
//		}
//		for(Report report : PENDING_REPORTS) {
//			if(report.getId() == id) {
//				return report;
//			}
//		}
		return ReportUtil.getReport(id);
	}

	public JsonDocument toDocument() {
		return new JsonDocument().append("reason", reason.getKey()).append("player", player.getUniqueId().toString())
				.append("creator", creator.getUniqueId().toString()).append("id", id);
	}

	public static Report fromObject(JsonObject o) {
		return new Report(BanUtil.getReasonByKey(o.get("reason").getAsJsonPrimitive().getAsString()),
				Util.getManager().getOfflinePlayer(UUID.fromString(o.getAsJsonPrimitive("player").getAsString())),
				Util.getManager().getOfflinePlayer(UUID.fromString(o.getAsJsonPrimitive("creator").getAsString())),
				o.getAsJsonPrimitive("id").getAsInt());
	}

	public static Report fromDocument(JsonDocument doc) {
		return new Report(BanUtil.getReasonByKey(doc.getString("reason")),
				Util.getManager().getOfflinePlayer(UUID.fromString(doc.getString("player"))),
				Util.getManager().getOfflinePlayer(UUID.fromString(doc.getString("creator"))), doc.getInt("id"));
	}
}
