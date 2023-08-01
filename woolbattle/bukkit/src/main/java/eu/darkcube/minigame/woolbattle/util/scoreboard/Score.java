/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.util.scoreboard;

public class Score {

	private org.bukkit.scoreboard.Score score;
	private Scoreboard sb;
	private Objective obj;

	public Score(Objective obj, ScoreboardTeam team) {
		this(team.getScoreboard(), obj, team.getTeam().getName());
	}

	public Score(Scoreboard sb, Objective obj, String name) {
		this.score = obj.getObjective().getScore(name);
		this.obj = obj;
		this.sb = sb;
	}

	public int getScore() {
		return score.getScore();
	}

	public Score setScore(int score) {
		this.score.setScore(score);
		return this;
	}

	public Scoreboard getScoreboard() {
		return sb;
	}

	public Objective getObjective() {
		return obj;
	}
}
