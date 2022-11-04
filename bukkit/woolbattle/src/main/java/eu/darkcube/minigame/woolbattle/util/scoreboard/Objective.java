package eu.darkcube.minigame.woolbattle.util.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;

public class Objective {

	private org.bukkit.scoreboard.Objective obj;
	
	public Objective(org.bukkit.scoreboard.Objective obj) {
		this.obj = obj;
	}
	
	public void setDisplayName(String name) {
		obj.setDisplayName(name);
	}
	
	public String getDisplayName() {
		return obj.getDisplayName();
	}
	
	public org.bukkit.scoreboard.Objective getObjective() {
		return obj;
	}
	
	public void setDisplaySlot(DisplaySlot slot) {
		obj.setDisplaySlot(slot);
	}
	
	public void unsetDisplaySlot() {
		setDisplaySlot(null);
	}
	
	public void setScore(String entry, int score) {
		obj.getScore(entry).setScore(score);
	}
	
	public int getScore(String entry) {
		return obj.getScore(entry).getScore();
	}
}