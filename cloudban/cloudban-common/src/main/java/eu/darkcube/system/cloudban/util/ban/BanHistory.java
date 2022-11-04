package eu.darkcube.system.cloudban.util.ban;

import java.util.ArrayList;
import java.util.List;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;

public class BanHistory {
	
	private List<Ban> bans = new ArrayList<>();
	
	public List<Ban> getBans() {
		return bans;
	}
	
	public String serialize() {
		return BanUtil.GSON.toJson(this);
	}
	
	public static BanHistory deserialize(String json) {
		return BanUtil.GSON.fromJson(json, BanHistory.class);
	}
	
	public JsonDocument toMySQLDocument() {
		return JsonDocument.newDocument(serialize());
	}
	
	@Override
	public String toString() {
		return serialize();
	}
	
	public static BanHistory fromMySQLDocument(JsonDocument doc) {
		if(doc == null)
			return new BanHistory();
		return deserialize(doc.toJson());
	}

	public boolean isEmpty() {
		return bans.isEmpty();
	}
}
