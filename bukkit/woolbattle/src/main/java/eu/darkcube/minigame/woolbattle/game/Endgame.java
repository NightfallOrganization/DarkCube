package eu.darkcube.minigame.woolbattle.game;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.endgame.ListenerBlockBreak;
import eu.darkcube.minigame.woolbattle.listener.endgame.ListenerEntityDamage;
import eu.darkcube.minigame.woolbattle.listener.endgame.ListenerPlayerJoin;
import eu.darkcube.minigame.woolbattle.listener.endgame.ListenerPlayerQuit;
import eu.darkcube.minigame.woolbattle.mysql.MySQL;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.CloudNetLink;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class Endgame extends GamePhase {

	public final ListenerPlayerJoin listenerPlayerJoin;
	public final ListenerPlayerQuit listenerPlayerQuit;
	public final ListenerBlockBreak listenerBlockBreak;
	public final ListenerEntityDamage listenerEntityDamage;

	public Endgame() {
		this.listenerPlayerJoin = new ListenerPlayerJoin();
		this.listenerPlayerQuit = new ListenerPlayerQuit();
		this.listenerBlockBreak = new ListenerBlockBreak();
		this.listenerEntityDamage = new ListenerEntityDamage();
	}

	@Override
	public void onEnable() {
		CloudNetLink.update();
		final Main main = Main.getInstance();
//		main.getSchedulers().clear();

		Main.registerListeners(this.listenerPlayerJoin);
		Main.registerListeners(this.listenerPlayerQuit);
		Main.registerListeners(this.listenerBlockBreak);
		Main.registerListeners(this.listenerEntityDamage);

		Team winner = main.getIngame().winner;
		main.getUserWrapper().getUsers().forEach(u -> {
			u.getBukkitEntity().teleport(main.getLobby().getSpawn());
			u.getBukkitEntity().setAllowFlight(false);
			this.setPlayerItems(u);
			MySQL.saveUserData(u);
		});
		main.sendMessage(Message.TEAM_HAS_WON, user -> {
			return new String[] { winner.getName(user) };
		});
		User mostKills = this.getPlayerWithMostKills();
		main.sendMessage(Message.PLAYER_HAS_MOST_KILLS, user -> {
			return new String[] { mostKills.getTeamPlayerName(), Integer.toString(mostKills.getKills()) };
		});
		for (Player p : Bukkit.getOnlinePlayers()) {
			for (Player t : Bukkit.getOnlinePlayers()) {
				if (!p.canSee(t))
					p.showPlayer(t);
			}
		}

		List<User> users = main.getUserWrapper().getUsers().stream().filter(u -> u.getKills() != 0)
				.collect(Collectors.toList());

		Collections.sort(users, new Comparator<User>() {
			@Override
			public int compare(User o1, User o2) {
				if (o1.getKD() > o2.getKD()) {
					return -1;
				}
				if (o1.getKD() < o2.getKD()) {
					return 1;
				}
				return 0;
			}
		});

		int players = users.size();
		if (players >= 1) {
			main.sendMessage(Message.STATS_PLACE_1, "1", this.val(users.get(0).getKD()), users.get(0).getTeamPlayerName());
			if (players >= 2) {
				main.sendMessage(Message.STATS_PLACE_2, "2", this.val(users.get(1).getKD()),
						users.get(1).getTeamPlayerName());
				if (players >= 3) {
					main.sendMessage(Message.STATS_PLACE_3, "3", this.val(users.get(2).getKD()),
							users.get(2).getTeamPlayerName());
					if (players >= 4) {
						for (int i = 3; i < 4; i++) {
							main.sendMessage(Message.STATS_PLACE_TH, Integer.toString(i + 1), this.val(users.get(i).getKD()),
									users.get(i).getTeamPlayerName());
						}
					}
				}
			}
		}

		new Scheduler() {
			private int countdownTicks = 400;

			@Override
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (this.countdownTicks % 20 == 0)
						all.setLevel(this.countdownTicks / 20);
					all.setExp(this.countdownTicks / 400F);
				}
				this.countdownTicks--;
				if (this.countdownTicks <= 0) {
					for (Player all : Bukkit.getOnlinePlayers()) {
						all.kickPlayer(Bukkit.getShutdownMessage());
					}
					Endgame.this.disable();
					this.cancel();
					Bukkit.shutdown();
//					Main.getInstance().getLobby().enable();
				}
			}
		}.runTaskTimer(1);
	}

	private String val(double d) {
		return String.format("%1.2f", d);
	}

	public User getPlayerWithMostKills() {
		User result = null;
		int kills = -1;
		for (User player : Main.getInstance().getUserWrapper().getUsers()) {
			if (player.getKills() > kills) {
				kills = player.getKills();
				result = player;
			}
		}
		return result;
	}

	public User getPlayerWithBestKD() {

		User result = null;
		double kd = -1;
		for (User player : Main.getInstance().getUserWrapper().getUsers()) {
			if (player.getKD() > kd) {
				kd = player.getKD();
				result = player;
			}
		}
		return result;
	}

	public void setPlayerItems(User user) {
		user.getBukkitEntity().getInventory().clear();
		user.getBukkitEntity().getInventory().setArmorContents(new ItemStack[4]);
	}

	@Override
	public void onDisable() {
		Main.unregisterListeners(this.listenerPlayerJoin);
		Main.unregisterListeners(this.listenerPlayerQuit);
		Main.unregisterListeners(this.listenerBlockBreak);
		Main.unregisterListeners(this.listenerEntityDamage);
	}

}
