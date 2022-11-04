package eu.darkcube.system.cloudban.util.ban;

import java.util.ArrayList;
import java.util.List;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;

public class BanInformation {

	private List<Ban> bans;
	
	public BanInformation() {
		bans = new ArrayList<>();
	}
	
	public List<Ban> getBans() {
		return bans;
	}
	
	@Override
	public String toString() {
		return serialize();
	}
	
	public String serialize() {
		return BanUtil.GSON.toJson(this);
	}
	
	public static BanInformation deserialize(String json) {
		return BanUtil.GSON.fromJson(json, BanInformation.class);
	}
	
	public JsonDocument toMySQLDocument() {
		return JsonDocument.newDocument(serialize());
	}
	
	public static BanInformation fromMySQLDocument(JsonDocument doc) {
		if(doc == null)
			return new BanInformation();
		return deserialize(doc.toJson());
	}

	public boolean isEmpty() {
		return bans.isEmpty();
	}
}
