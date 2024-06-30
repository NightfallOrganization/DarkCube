/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.manager.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {

    public void setScoreboard(Player player) {
        if(player.getScoreboard()== Bukkit.getScoreboardManager().getMainScoreboard()) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.registerNewObjective("test", "dummy", " §7- §eOneBlock V1 §7- ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score11 = objective.getScore("     ");
        score11.setScore(11);
        Score score10 = objective.getScore("§fLevel:");
        score10.setScore(10);
        Score score9 = objective.getScore("§7» §e1");
        score9.setScore(9);
        Score score8 = objective.getScore("    ");
        score8.setScore(8);
        Score score7 = objective.getScore("§fCor:");
        score7.setScore(7);
        Score score6 = objective.getScore("§7» §e0");
        score6.setScore(6);
        Score score5 = objective.getScore("   ");
        score5.setScore(5);
        Score score4 = objective.getScore("§fSpielzeit:");
        score4.setScore(4);
        Score score3 = objective.getScore("§7» §e1d 17h");
        score3.setScore(3);
        Score score2 = objective.getScore("  ");
        score2.setScore(2);
        Score score1 = objective.getScore("§fWelt:");
        score1.setScore(1);
        Score score0 = objective.getScore("§7» §eSpawn");
        score0.setScore(0);

        player.setScoreboard(scoreboard);
    }

}
