package eu.darkcube.system.citybuild.util;

import eu.darkcube.system.citybuild.Citybuild;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private final Citybuild plugin;
    private final LevelXPManager levelManager;
    private Map<UUID, Objective> playerObjectives = new HashMap<>();
    private final CorManager corManager;

    public ScoreboardManager(Citybuild plugin, LevelXPManager levelManager, CorManager corManager) {
        this.plugin = plugin;
        this.levelManager = levelManager;
        this.corManager = corManager;
    }

    public void createScoreboard(Player player) {
        Scoreboard board = player.getScoreboard();
        if (board == Bukkit.getScoreboardManager().getMainScoreboard()) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(board);
        }
        Objective obj = board.getObjective("playerStats");
        if (obj == null) {
            obj = board.registerNewObjective("playerStats", "dummy", ChatColor.GRAY + "« " + ChatColor.DARK_GREEN + "Dark" + ChatColor.GREEN + "Cube" + ChatColor.GRAY + ".eu »");
        }
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        setupDefaultScores(obj, player);

        playerObjectives.put(player.getUniqueId(), obj);
    }

    public void updatePlayerLevel(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective obj = scoreboard.getObjective(DisplaySlot.SIDEBAR);

        if (obj == null || !obj.getName().equals("playerStats")) {
            return;
        }

        String levelPrefix = ChatColor.WHITE + "Ḥ" + ChatColor.GRAY + " ┃ ";

        // Entfernen Sie alle Einträge, die mit dem Level-Prefix beginnen und nur eine Zahl dahinter haben
        for (String entry : scoreboard.getEntries()) {
            if (entry.startsWith(levelPrefix) && isNumeric(entry.substring(levelPrefix.length()))) {
                scoreboard.resetScores(entry);
            }
        }

        int playerLevel = levelManager.getLevel(player);
        Score lvl = obj.getScore(levelPrefix + playerLevel);
        lvl.setScore(2);
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void updatePlayerCor(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective obj = scoreboard.getObjective(DisplaySlot.SIDEBAR);

        if (obj == null || !obj.getName().equals("playerStats")) {
            return;
        }

        String corPrefix = ChatColor.WHITE + "ḡ" + ChatColor.GRAY + " ┃ ";

        // Remove any entries starting with the Cor prefix and followed by just a number
        for (String entry : scoreboard.getEntries()) {
            if (entry.startsWith(corPrefix) && isNumeric(entry.substring(corPrefix.length()))) {
                scoreboard.resetScores(entry);
            }
        }

        int playerCor = corManager.getCor(player);
        Score corScore = obj.getScore(corPrefix + playerCor);
        corScore.setScore(5);
    }

    private void setupDefaultScores(Objective obj, Player player) {
        // Header
        Score headerSpacer = obj.getScore(" ");
        headerSpacer.setScore(10);

        // Gilde
        Score gilde = obj.getScore(ChatColor.GRAY + "» " + ChatColor.GREEN + "Gilde:");
        gilde.setScore(9);
        Score gildenname = obj.getScore(ChatColor.WHITE + "ḥ" + ChatColor.GRAY + " ┃ " + getGildenNameForPlayer(player));
        gildenname.setScore(8);

        // Lücke zwischen Gilde und Cor
        Score gildeToCorSpacer = obj.getScore("  ");
        gildeToCorSpacer.setScore(7);

        // Cor
        Score cor = obj.getScore(ChatColor.GRAY + "» " + ChatColor.GREEN + "Cor:");
        cor.setScore(6);
        Score betrag = obj.getScore(ChatColor.WHITE + "ḡ" + ChatColor.GRAY + " ┃ " + corManager.getCor(player));
        betrag.setScore(5);

        // Lücke zwischen Cor und Level
        Score corToLevelSpacer = obj.getScore("   ");
        corToLevelSpacer.setScore(4);

        // Level
        Score level = obj.getScore(ChatColor.GRAY + "» " + ChatColor.GREEN + "Level:");
        level.setScore(3);
        int playerLevel = levelManager.getLevel(player);
        Score lvl = obj.getScore(ChatColor.WHITE + "Ḥ" + ChatColor.GRAY + " ┃ " + playerLevel);
        lvl.setScore(2);
    }

    private String getGildenNameForPlayer(Player player) {
        return "[Gildenname]";
    }

    private String getBetragForPlayer(Player player) {
        return "[Betrag]";
    }
}
