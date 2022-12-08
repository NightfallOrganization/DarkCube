/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.state;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.listener.EndgameListener;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.I18n;
import de.pixel.bedwars.util.Message;
import de.pixel.bedwars.util.ScoreboardTeam;

public class Endgame extends GameState {

	private static final ScoreboardTeam[] ENDGAME_SCOREBOARD_TEAMS = new ScoreboardTeam[] {
			ScoreboardTeam.ENDGAME_TIMER
	};
	public static final int TICKS_COUNTDOWN = 10 * 20;

	public Endgame() {
		super(new EndgameListener());
	}

	@Override
	protected void onEnable() {
		System.out.println("Enabling endgame");
		Optional<Team> winnerO = Team.getTeams().stream().filter(t -> t.getPlayers().size() != 0).findFirst();
		if (winnerO.isPresent()) {
			Team winner = winnerO.get();
			Main.sendMessage(Message.TEAM_WON, p -> winner.toString(p));
		} else {
			Main.getInstance().sendMessage("§cKein Team hat gewonnen!");
		}
		setup(null);

		new BukkitRunnable() {
			private int ticks = TICKS_COUNTDOWN;

			@Override
			public void run() {
				setTicks(ticks - 1);
			}

			private void setTicks(int ticks) {
				this.ticks = ticks;
				if (ticks == 0) {
					cancel();
					Main.getInstance().getLobby().activate();
				} else {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.setLevel((int) Math.ceil(ticks / 20F));
						if (ticks > TICKS_COUNTDOWN) {
							p.setExp(0.999999F);
						} else {
							p.setExp((float) ticks / (float) TICKS_COUNTDOWN);
						}
						Scoreboard sb = p.getScoreboard();
						org.bukkit.scoreboard.Team team = sb.getTeam(ScoreboardTeam.ENDGAME_TIMER.getTag());
						team.setSuffix(Integer.toString((int) Math.ceil(ticks / 20F)));
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}

	public void setupScoreboard(Player p) {
		Scoreboard sb = p.getScoreboard();
		Objective obj = sb.registerNewObjective("endgame", "dummy");
		obj.setDisplayName(Message.LOBBY_OBJECTIVE_TITLE.getMessage(p));
		int id = Endgame.ENDGAME_SCOREBOARD_TEAMS.length;
		for (ScoreboardTeam team : Endgame.ENDGAME_SCOREBOARD_TEAMS) {
			org.bukkit.scoreboard.Team t = sb.registerNewTeam(team.getTag());
			t.setPrefix(I18n.translate(I18n.getPlayerLanguage(p), team.getPrefixMessage()));
			t.addEntry(team.getEntry());
			obj.getScore(team.getEntry()).setScore(id--);
		}
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	@Override
	protected void onDisable() {

	}

	public void setup(Player p) {
		if (p == null) {
			Bukkit.getOnlinePlayers().forEach(this::setup);
			return;
		}
		p.closeInventory();
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
		p.setNoDamageTicks(0);
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setExhaustion(0);
		p.setSaturation(0);
		p.spigot().setCollidesWithEntities(true);
		Bukkit.getOnlinePlayers().stream().forEach(s -> {
			s.showPlayer(p);
		});
		p.teleport(Main.getInstance().getLobby().getSpawn());
		Main.getInstance().setupScoreboard(p);
	}
}
