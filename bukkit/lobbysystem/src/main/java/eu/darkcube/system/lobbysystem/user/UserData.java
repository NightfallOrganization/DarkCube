package eu.darkcube.system.lobbysystem.user;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.reflect.TypeToken;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.lobbysystem.gadget.Gadget;
import eu.darkcube.system.lobbysystem.util.GsonSerializer.DontSerialize;
import eu.darkcube.system.lobbysystem.util.Serializable;

public class UserData implements Serializable {

	@DontSerialize
	private Language language;
	private Gadget gadget;
	private boolean sounds = true;
	private boolean animations = true;
	private long lastDailyReward = 0;
	private Set<Integer> rewardSlotsUsed = new HashSet<>();

	public UserData() {
		this(null, null);
	}

	public UserData(UUID owner, JsonDocument document) {
		if (document == null) {
			language = Language.DEFAULT;
			gadget = Gadget.HOOK_ARROW;
			return;
		}
		if (document.contains("language")) {
			String lstring = document.getString("language");
			Language language = Language.fromString(lstring);
			System.out.println("Converting language... from " + lstring + " to "
							+ language.toString());
			this.language = language;
			Language.setLanguage(owner, language);
		} else {
			this.language = Language.getLanguage(owner);
		}
		gadget = Gadget.fromString(document.getString("gadget"));
		sounds = document.getBoolean("sounds");
		animations = document.getBoolean("animations");
		lastDailyReward = document.getLong("lastDailyReward");
		rewardSlotsUsed = document.get("rewardSlotsUsed", new TypeToken<Set<Integer>>() {
			private static final long serialVersionUID = -2586151616370404958L;
		}.getType(), rewardSlotsUsed);
	}

	public UserData(Language language, Gadget gadget, boolean sounds,
					boolean animations, long lastDailyReward,
					Set<Integer> rewardSlotsUsed) {
		this.rewardSlotsUsed = rewardSlotsUsed;
		this.lastDailyReward = lastDailyReward;
		this.language = language;
		this.gadget = gadget;
		this.sounds = sounds;
		this.animations = animations;
	}

	public Language getLanguage() {
		return language;
	}

	public boolean isSounds() {
		return sounds;
	}

	public Set<Integer> getRewardSlotsUsed() {
		return rewardSlotsUsed;
	}

	public boolean isAnimations() {
		return animations;
	}

	public long getLastDailyReward() {
		return lastDailyReward;
	}

	public void setLastDailyReward(long lastDailyReward) {
		this.lastDailyReward = lastDailyReward;
	}

	public void setSounds(boolean sounds) {
		this.sounds = sounds;
	}

	public void setAnimations(boolean animations) {
		this.animations = animations;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Gadget getGadget() {
		return gadget;
	}

	public void setGadget(Gadget gadget) {
		this.gadget = gadget;
	}
}
