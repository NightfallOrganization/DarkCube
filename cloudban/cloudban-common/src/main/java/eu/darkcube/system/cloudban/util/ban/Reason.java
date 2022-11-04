package eu.darkcube.system.cloudban.util.ban;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;

public class Reason {

	private String key;
	private String display;
	private Map<Integer, BanDuration> durations;
	private boolean canReportFor;

	public Reason(String key, String display) {
		this(key, display, true);
	}

	public Reason(String key, String display, boolean canReportFor) {
		this(key, display, canReportFor, BanDuration.WARNING_BAN);
	}

	public Reason(String key, String display, boolean canReportFor, BanDuration... durations) {
		this.key = key;
		this.display = display;
		this.durations = new HashMap<>();
		this.canReportFor = canReportFor;
		int index = 1;
		for (BanDuration duration : durations) {
			this.durations.put(index++, duration);
		}
	}

	public Reason(String key, String display, Map<Integer, BanDuration> durations) {
		this(key, display, durations, true);
	}

	public Reason(String key, String display, Map<Integer, BanDuration> durations, boolean canReportFor) {
		this.key = key;
		this.display = display;
		this.durations = durations;
		this.canReportFor = canReportFor;
	}

	public String getDisplay() {
		return display;
	}

	public Map<Integer, BanDuration> getDurations() {
		return durations;
	}

	public void setDurations(Map<Integer, BanDuration> durations) {
		this.durations = durations;
	}

	public String getKey() {
		return key;
	}

	public boolean canReportFor() {
		return canReportFor;
	}

	@Override
	public String toString() {
		return BanUtil.GSON.toJson(this);
	}

	public String serialize() {
		return new Gson().toJson(this);
	}

	public JsonDocument toMySQLDocument() {
//		JsonDocument doc = new JsonDocument().append("display", display).append("durations",
//				new Gson().toJson(durations));
		return new JsonDocument(this);
	}

	public static Reason fromMySQLDocument(String key, JsonDocument doc) {
		if (doc == null || doc.isEmpty())
			return null;
//		String display = doc.getString("display");
//		Map<Integer, Duration> durations = new Gson().fromJson(doc.getString("durations"),
//				new SimpleTypeToken<Map<Integer, Duration>>().getType());
//		return new Reason(key, display, durations);
		Reason r = new Gson().fromJson(doc.toJson(), Reason.class);
		r.key = key;
		return r;
	}

	public static Reason deserialize(String json) {
		return new Gson().fromJson(json, Reason.class);
	}

	@Override
	public boolean equals(Object obj) {
		String key = null;
		if (obj instanceof Reason)
			key = ((Reason) obj).key;
		else if (obj instanceof String)
			key = obj.toString();
		else
			return false;
		return key.equals(this.key);
	}
}
