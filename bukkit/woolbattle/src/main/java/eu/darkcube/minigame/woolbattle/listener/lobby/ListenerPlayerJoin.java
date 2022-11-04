package eu.darkcube.minigame.woolbattle.listener.lobby;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;

import eu.darkcube.minigame.woolbattle.Config;
import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ObjectiveTeam;
import eu.darkcube.minigame.woolbattle.util.ScoreboardObjective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Objective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Team;

public class ListenerPlayerJoin extends Listener<PlayerJoinEvent> {

	@Override
	@EventHandler
	public void handle(PlayerJoinEvent e) {
		Lobby lobby = Main.getInstance().getLobby();
		Player p = e.getPlayer();
		User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
//		if (Main.getInstance().getLobby().isEnabled()) {
		Main.getInstance().getTeamManager().setTeam(user, Main.getInstance().getTeamManager().getSpectator());
		Scoreboard sb = new Scoreboard();
		Main.initScoreboard(sb, user);
		lobby.getScoreboardByUser().put(user, sb);
		lobby.loadScoreboard(user);
		lobby.reloadTab(user);
		p.setScoreboard(sb.getScoreboard());

		Objective obj = sb.getObjective(ScoreboardObjective.LOBBY.getKey());
		int minplayercount = Main.getInstance().getConfig("config").getInt(Config.MIN_PLAYER_COUNT);
		Team needed = sb.getTeam(ObjectiveTeam.NEEDED.getKey());
		for (int i = 0; i < ObjectiveTeam.values().length; i++) {
			ObjectiveTeam ot = ObjectiveTeam.values()[i];
			sb.getTeam(ot.getKey()).addPlayer(ot.getInvisTag());
			obj.setScore(ot.getInvisTag(), i + 1);
		}

		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}

		needed.setSuffix(Integer.toString(minplayercount));
		Main.getInstance().setMap(user);
		Main.getInstance().setOnline(user);
		Main.getInstance().setEpGlitch(user);

		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		Main.getInstance().getUserWrapper().getUsers().forEach(t -> {
			if (e.getJoinMessage() != null)
				lobby.reloadUsers(t);
			Main.getInstance().setOnline(t);
		});

		if (e.getJoinMessage() != null) {
			p.teleport(lobby.getSpawn());
		}

		p.setGameMode(GameMode.SURVIVAL);
		p.resetMaxHealth();
		p.resetPlayerTime();
		p.resetPlayerWeather();
		p.setAllowFlight(false);
		p.setExhaustion(0);
		p.setExp(0);
		p.setLevel(0);
		p.setFoodLevel(20);
		p.setHealth(20);
		p.setItemOnCursor(null);
		p.setSaturation(0);
		PlayerInventory inv = p.getInventory();
		inv.clear();
		inv.setArmorContents(new ItemStack[4]);
		lobby.setParticlesItem(user, p);
		inv.setItem(0, Item.LOBBY_PERKS.getItem(user));
		inv.setItem(1, Item.LOBBY_TEAMS.getItem(user));
//		inv.setItem(7, Item.LOBBY_GADGET.getItem(user));
		inv.setItem(7, Item.SETTINGS.getItem(user));
		inv.setItem(8, Item.LOBBY_VOTING.getItem(user));

		if (e.getJoinMessage() != null) {
			lobby.setTimer(lobby.getTimer() < 300 ? 300 : lobby.getTimer());

			for (User t : Main.getInstance().getUserWrapper().getUsers()) {
				t.getBukkitEntity().sendMessage(Message.PLAYER_JOINED.getMessage(t, user.getTeamPlayerName()));
			}
			Main.getInstance().sendConsole(Message.PLAYER_JOINED.getServerMessage(user.getTeamPlayerName()));

			e.setJoinMessage(null);
		} else {
			lobby.setTimer(lobby.getTimer());
//			}
		}

//		for (String n : ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel.pipeline().names()) {
//			ChannelHandler h = ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel.pipeline().get(n);
//			System.out.println(
//					"n: " + (h == null ? "null" : h.getClass().getName()));
//		}
//		((CraftPlayer) p).getHandle().playerConnection.networkManager.channel.pipeline()
//				.addBefore("packet_handler", "tempHandler", new ChannelDuplexHandler() {
//					@Override
//					public void write(ChannelHandlerContext channelHandlerContext, Object object,
//							ChannelPromise channelPromise) throws Exception {
//						if (object instanceof PacketPlayOutKeepAlive || object instanceof PacketPlayOutRelEntityMove
//								|| object instanceof PacketPlayOutUpdateTime || object instanceof PacketPlayOutChat) {
//
//						} else {
//							System.out.println(object.toString());
//						}
//						super.write(channelHandlerContext, object, channelPromise);
//					}
//				});
	}
}