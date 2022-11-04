package eu.darkcube.minigame.woolbattle.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scoreboard.DisplaySlot;

import eu.darkcube.minigame.woolbattle.Config;
import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.event.EventDamageBlock;
import eu.darkcube.minigame.woolbattle.event.EventDestroyBlock;
import eu.darkcube.minigame.woolbattle.event.EventPlayerDeath;
import eu.darkcube.minigame.woolbattle.event.EventPlayerKill;
import eu.darkcube.minigame.woolbattle.listener.ingame.*;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.*;
import eu.darkcube.minigame.woolbattle.listener.ingame.standard.ListenerDoubleJump;
import eu.darkcube.minigame.woolbattle.listener.ingame.standard.ListenerEnderpearlLaunchable;
import eu.darkcube.minigame.woolbattle.perk.PlayerPerks;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Characters;
import eu.darkcube.minigame.woolbattle.util.CloudNetLink;
import eu.darkcube.minigame.woolbattle.util.Enableable;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemBuilder;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;
import eu.darkcube.minigame.woolbattle.util.ScoreboardObjective;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scheduler.SchedulerHeightDisplay;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Objective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.minigame.woolbattle.util.tab.Footer;
import eu.darkcube.minigame.woolbattle.util.tab.Header;
import eu.darkcube.minigame.woolbattle.util.tab.TabManager;

public class Ingame implements Enableable {

	public static int MAX_BLOCK_ARROW_HITS = Main.getInstance().getConfig("config").getInt("maxblockarrowhits");
	public static int SPAWNPROTECTION_TICKS = Main.getInstance().getConfig("config").getInt("spawnprotectionticks");
	public static int SPAWNPROTECTION_TICKS_GLOBAL = Main.getInstance().getConfig("config").getInt("spawnprotectionticksglobal");

	public Set<Block> placedBlocks = new HashSet<>();
	public Map<Arrow, User> arrows = new HashMap<>();
	public Collection<Player> particlePlayers = new HashSet<>();
	public Map<Block, Byte> breakedWool = new HashMap<>();
	public Map<User, Integer> killstreak = new HashMap<>();

	public int killsFor1Life = 5;

	public final ListenerBlockBreak listenerBlockBreak;
	public final ListenerBlockPlace listenerBlockPlace;
	public final ListenerItemDrop listenerItemDrop;
	public final ListenerItemPickup listenerItemPickup;
	public final ListenerBlockCanBuild listenerBlockCanBuild;
	public final ListenerPlayerJoin listenerPlayerJoin;
	public final ListenerPlayerQuit listenerPlayerQuit;
	public final ListenerPlayerLogin listenerPlayerLogin;
	public final ListenerEntityDamageByEntity listenerEntityDamageByEntity;
	public final ListenerDoubleJump listenerDoubleJump;
	public final ListenerEntityDamage listenerEntityDamage;
	public final ListenerChangeBlock listenerChangeBlock;
	public final ListenerProjectileHit listenerProjectileHit;
	public final ListenerProjectileLaunch listenerProjectileLaunch;
	public final ListenerInventoryClick listenerInventoryClick;
	public final ListenerInventoryDrag listenerInventoryDrag;
	public final ListenerCapsuleInteract listenerCapsule;
	public final ListenerSwitcherLaunchable listenerSwitcherLaunch;
	public final ListenerSwitcherSwitch listenerSwitcherSwitch;
	public final ListenerInteract listenerInteract;
	public final ListenerGameModeChange listenerGameModeChange;
	public final ListenerPlayerMove listenerPlayerMove;
	public final ListenerEntitySpawn listenerEntitySpawn;
	public final ListenerWoolBombInteract listenerWoolBombInteract;
	public final ListenerWoolBombLaunchable listenerWoolBombLaunch;
	public final ListenerTNTEntityDamageByEntity listenerTNTEntityDamageByEntity;
	public final ListenerWoolBombHit listenerWoolBombHit;
	public final ListenerExplode listenerWoolBombExplode;
	public final ListenerLineBuilderInteract listenerLineBuilderInteract;
	public final ListenerRonjasToiletInteract listenerRonjasToiletInteract;
	public final ListenerRonjasToiletLaunch listenerRonjasToiletLaunch;
	public final ListenerRonjasToiletHit listenerRonjasToiletHit;
	public final ListenerRonjasToiletEntityDamageByEntity listenerRonjasToiletEntityDamageByEntity;
	public final ListenerEnderpearlLaunchable listenerEnderpearlLaunchable;
	public final ListenerSafetyPlatformInteract listenerSafetyPlatformInteract;
	public final ListenerWallGeneratorInteract listenerWallGeneratorInteract;
	public final ListenerBlinkLaunchable listenerBlinkInteract;
	public final ListenerGrandpasClockInteract listenerGrandpasClockInteract;
	public final ListenerGhostInteract listenerGhostInteract;
	public final ListenerGhostEntityDamage listenerGhostEntityDamageByEntity;
	public final ListenerMinigunInteract listenerMinigunInteract;
	public final ListenerDeathMove listenerDeathMove;
	public final ListenerGrabberInteract listenerGrabberInteract;
	public final ListenerBoosterInteract listenerBoosterInteract;
	public final ListenerGrapplingHookFishing listenerGrapplingHookFishing;
	public final ListenerRopeInteract listenerRopeInteract;
	public final Scheduler schedulerResetWool;
	public final Scheduler schedulerResetSpawnProtetion;
	public final Scheduler schedulerParticles;
	public final Scheduler schedulerGlobalSpawnProtection;
	public final Scheduler schedulerTick;
	public final Scheduler schedulerGhostItemFix;
	public final SchedulerHeightDisplay schedulerHeightDisplay;
	public final Map<User, Team> lastTeam = new HashMap<>();
//	public final Scheduler schedulerDeathTimer;
	public boolean isGlobalSpawnProtection = false;
	private boolean startingIngame = false;
	public Team winner;

	public Ingame() {
		listenerItemPickup = new ListenerItemPickup();
		listenerItemDrop = new ListenerItemDrop();
		listenerBlockBreak = new ListenerBlockBreak();
		listenerBlockPlace = new ListenerBlockPlace();
		listenerBlockCanBuild = new ListenerBlockCanBuild();
		listenerPlayerJoin = new ListenerPlayerJoin();
		listenerPlayerQuit = new ListenerPlayerQuit();
		listenerPlayerLogin = new ListenerPlayerLogin();
		listenerEntityDamageByEntity = new ListenerEntityDamageByEntity();
		listenerDoubleJump = new ListenerDoubleJump();
		listenerEntityDamage = new ListenerEntityDamage();
		listenerChangeBlock = new ListenerChangeBlock();
		listenerProjectileHit = new ListenerProjectileHit();
		listenerProjectileLaunch = new ListenerProjectileLaunch();
		listenerInventoryClick = new ListenerInventoryClick();
		listenerInventoryDrag = new ListenerInventoryDrag();
		listenerCapsule = new ListenerCapsuleInteract();
		listenerSwitcherLaunch = new ListenerSwitcherLaunchable();
		listenerSwitcherSwitch = new ListenerSwitcherSwitch();
		listenerInteract = new ListenerInteract();
		listenerGameModeChange = new ListenerGameModeChange();
		listenerPlayerMove = new ListenerPlayerMove();
		listenerEntitySpawn = new ListenerEntitySpawn();
		listenerWoolBombInteract = new ListenerWoolBombInteract();
		listenerWoolBombLaunch = new ListenerWoolBombLaunchable();
		listenerTNTEntityDamageByEntity = new ListenerTNTEntityDamageByEntity();
		listenerWoolBombHit = new ListenerWoolBombHit();
		listenerWoolBombExplode = new ListenerExplode();
		listenerLineBuilderInteract = new ListenerLineBuilderInteract();
		listenerRonjasToiletInteract = new ListenerRonjasToiletInteract();
		listenerRonjasToiletLaunch = new ListenerRonjasToiletLaunch();
		listenerRonjasToiletHit = new ListenerRonjasToiletHit();
		listenerRonjasToiletEntityDamageByEntity = new ListenerRonjasToiletEntityDamageByEntity();
		listenerEnderpearlLaunchable = new ListenerEnderpearlLaunchable();
		listenerSafetyPlatformInteract = new ListenerSafetyPlatformInteract();
		listenerBlinkInteract = new ListenerBlinkLaunchable();
		listenerWallGeneratorInteract = new ListenerWallGeneratorInteract();
		listenerGrandpasClockInteract = new ListenerGrandpasClockInteract();
		listenerGhostInteract = new ListenerGhostInteract();
		listenerGhostEntityDamageByEntity = new ListenerGhostEntityDamage();
		listenerMinigunInteract = new ListenerMinigunInteract();
		listenerDeathMove = new ListenerDeathMove();
		listenerGrabberInteract = new ListenerGrabberInteract();
		listenerBoosterInteract = new ListenerBoosterInteract();
		listenerGrapplingHookFishing = new ListenerGrapplingHookFishing();
		listenerRopeInteract = new ListenerRopeInteract();

		schedulerParticles = new Scheduler() {
			@Override
			public void run() {
				for (Arrow arrow : arrows.keySet()) {
					Location loc = arrow.getLocation();
					loc.add(arrow.getVelocity().multiply(10));
					for (int x = -1; x < 2; x++) {
						for (int z = -1; z < 2; z++) {
							Location l = arrow.getLocation();
							l.add(x * 16, 0, z * 16);
							l.getChunk().load();
						}
					}
					if (arrow.getShooter() instanceof Player) {
						if (arrow.isDead() || arrow.isOnGround()
										|| !arrow.isValid()
										|| !((Player) arrow.getShooter()).isOnline()
										|| !arrow.getLocation().getChunk().isLoaded()) {
							arrow.remove();
							arrows.remove(arrow);
							break;
						}
						User user = Main.getInstance().getUserWrapper().getUser(((Player) arrow.getShooter()).getUniqueId());
						ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(
										Material.WOOL,
										user.getTeam().getType().getWoolColor()), 0, 0, 0, 1, 8, arrow.getLocation(), particlePlayers);
					}
				}
			}
		};
		schedulerGlobalSpawnProtection = new Scheduler() {
			public int protectionTicks = SPAWNPROTECTION_TICKS_GLOBAL;

			@Override
			public void run() {
				if (protectionTicks - 1 <= 0
								|| SPAWNPROTECTION_TICKS_GLOBAL == 0) {
					protectionTicks = SPAWNPROTECTION_TICKS_GLOBAL;
					this.cancel();
					return;
				}
				isGlobalSpawnProtection = true;
				protectionTicks--;
				for (User user : Main.getInstance().getUserWrapper().getUsers()) {
					user.getBukkitEntity().setExp((float) protectionTicks
									/ (float) SPAWNPROTECTION_TICKS_GLOBAL);
				}
			}

			@Override
			public void cancel() {
				if (!isCancelled()) {
					isGlobalSpawnProtection = false;
					schedulerResetSpawnProtetion.runTaskTimer(1);
					super.cancel();
				}
			}
		};
		schedulerTick = new Scheduler() {
			@Override
			public void run() {
				for (User user : Main.getInstance().getUserWrapper().getUsers()) {
					if (user.getTeam().getType() != TeamType.SPECTATOR) {
						if (user.getTicksAfterLastHit() < 1200)
							user.setTicksAfterLastHit(user.getTicksAfterLastHit()
											+ 1);
					}
				}
			}
		};
		schedulerResetSpawnProtetion = new Scheduler() {
			@Override
			public void run() {
				for (User user : Main.getInstance().getUserWrapper().getUsers()) {
					if (SPAWNPROTECTION_TICKS != 0) {
						if (user.getSpawnProtectionTicks() > 0) {
							user.setSpawnProtectionTicks(user.getSpawnProtectionTicks()
											- 1);
						}
					}
				}
			}
		};
		schedulerResetWool = new Scheduler() {
			@SuppressWarnings("deprecation")
			@Override
			public synchronized void run() {
				for (Block b : breakedWool.keySet()) {
					b.setType(Material.WOOL);
					b.setData(breakedWool.get(b));
				}
				breakedWool.clear();
			}
		};
		schedulerHeightDisplay = new SchedulerHeightDisplay();
		schedulerGhostItemFix = new Scheduler() {
			@Override
			public void run() {
				for (User user : Main.getInstance().getUserWrapper().getUsers()) {
					if (user.getTeam().getType() != TeamType.SPECTATOR) {
						user.getActivePerk1().updateInventory();
						user.getActivePerk2().updateInventory();
						user.getPassivePerk().updateInventory();
					}
				}
			}
		};
	}

	public Team getLastTeam(User user) {
		return lastTeam.containsKey(user) ? lastTeam.get(user) : user.getTeam();
	}

	@Override
	public void onEnable() {
		startingIngame = true;
		CloudNetLink.update();
		splitPlayersToTeams();
		Main.getInstance().getSchedulers().clear();
		int lifes = -1;
		if (!Main.getInstance().getLobby().VOTES_LIFES.isEmpty()) {
			Collection<Integer> list = Main.getInstance().getLobby().VOTES_LIFES.values();
			double d = 0;
			for (int i : list) {
				d += i;
			}
			d /= list.size();
			lifes = (int) Math.round(d);
		}
		if (lifes == -1) {
			int xtra = 0;
			int playersize = Bukkit.getOnlinePlayers().size();
			if (playersize < 3) {
				xtra = new Random().nextInt(playersize / 2);
			} else {
				playersize = 3;
				xtra = new Random().nextInt(4);
			}
			xtra += playersize;
			lifes = 10 + xtra;
		}
		if (Main.getInstance().baseLifes != null)
			lifes = Main.getInstance().baseLifes;
		for (Team team : Main.getInstance().getTeamManager().getTeams()) {
			team.setLifes(lifes);
		}

		listenerLineBuilderInteract.cooldownTasks.clear();
		listenerLineBuilderInteract.lastLine.clear();
		listenerLineBuilderInteract.lines.clear();
		listenerLineBuilderInteract.placeTasks.clear();
		listenerDoubleJump.cooldown.clear();
		listenerPlayerMove.ghostBlockFixCount.clear();
		listenerGhostInteract.ghosts.clear();
		listenerGhostEntityDamageByEntity.attacks.clear();

		if (placedBlocks != null)
			for (Block b : placedBlocks) {
				b.setType(Material.AIR);
				resetBlockDamage(b);
			}

		Main.registerListeners(listenerBlockBreak, listenerBlockPlace, listenerItemDrop, listenerItemPickup, listenerBlockCanBuild, listenerPlayerJoin, listenerPlayerQuit, listenerPlayerLogin, listenerEntityDamageByEntity, listenerDoubleJump, listenerEntityDamage, listenerChangeBlock, listenerProjectileHit, listenerProjectileLaunch, listenerInventoryClick, listenerInventoryDrag, listenerCapsule, listenerSwitcherLaunch, listenerSwitcherSwitch, listenerInteract, listenerGameModeChange, listenerPlayerMove, listenerEntitySpawn, listenerEnderpearlLaunchable, listenerSafetyPlatformInteract, listenerWoolBombInteract, listenerWoolBombLaunch, listenerTNTEntityDamageByEntity, listenerWoolBombHit, listenerWoolBombExplode, listenerLineBuilderInteract, listenerRonjasToiletInteract, listenerRonjasToiletHit, listenerRonjasToiletEntityDamageByEntity, listenerRonjasToiletLaunch, listenerBlinkInteract, listenerWallGeneratorInteract, listenerGrandpasClockInteract, listenerGhostInteract, listenerGhostEntityDamageByEntity, listenerMinigunInteract, listenerDeathMove, listenerGrabberInteract, listenerBoosterInteract, listenerGrapplingHookFishing, listenerRopeInteract);
//		Main.registerListeners(listenerEnderpearlLaunchable);

		Main.getInstance().getUserWrapper().getUsers().forEach(u -> {
			loadScoreboardObjective(u);
			u.loadPerks();
			u.getBukkitEntity().closeInventory();
			setPlayerItems(u);
			u.setTicksAfterLastHit(1200);
			u.getBukkitEntity().teleport(u.getTeam().getSpawn());
		});

		isGlobalSpawnProtection = true;

		schedulerResetWool.runTaskTimer(16);
		schedulerParticles.runTaskTimer(1);
		// This one also starts the normal spawnprotection scheduler
		schedulerGlobalSpawnProtection.runTaskTimer(1);
		schedulerTick.runTaskTimer(1);
		schedulerHeightDisplay.start();

		for (Player p : Bukkit.getOnlinePlayers()) {
			for (Player t : Bukkit.getOnlinePlayers()) {
				if (p != t) {
					if (!p.canSee(t)) {
						p.showPlayer(t);
					}
				}
			}
		}
		startingIngame = false;
	}

	@Override
	public void onDisable() {
		schedulerGlobalSpawnProtection.cancel();
		schedulerResetSpawnProtetion.cancel();
		schedulerParticles.cancel();
		schedulerResetWool.cancel();
		schedulerTick.cancel();
		schedulerHeightDisplay.stop();
		Main.unregisterListeners(listenerBlockBreak, listenerBlockPlace, listenerItemDrop, listenerItemPickup, listenerBlockCanBuild, listenerPlayerJoin, listenerPlayerQuit, listenerPlayerLogin, listenerEntityDamageByEntity, listenerDoubleJump, listenerEntityDamage, listenerChangeBlock, listenerProjectileHit, listenerProjectileLaunch, listenerInventoryClick, listenerInventoryDrag, listenerCapsule, listenerSwitcherLaunch, listenerSwitcherSwitch, listenerInteract, listenerGameModeChange, listenerPlayerMove, listenerEntitySpawn, listenerEnderpearlLaunchable, listenerSafetyPlatformInteract, listenerWoolBombInteract, listenerWoolBombLaunch, listenerTNTEntityDamageByEntity, listenerWoolBombHit, listenerWoolBombExplode, listenerLineBuilderInteract, listenerRonjasToiletInteract, listenerRonjasToiletHit, listenerRonjasToiletEntityDamageByEntity, listenerRonjasToiletLaunch, listenerBlinkInteract, listenerWallGeneratorInteract, listenerGrandpasClockInteract, listenerGhostInteract, listenerGhostEntityDamageByEntity, listenerMinigunInteract, listenerDeathMove, listenerGrabberInteract, listenerBoosterInteract, listenerGrapplingHookFishing, listenerRopeInteract);
		listenerEnderpearlLaunchable.disable();
		for (Block b : placedBlocks) {
			b.setType(Material.AIR);
			resetBlockDamage(b);
		}
	}

	public int getKillstreak(User user) {
		return killstreak.getOrDefault(user, 0);
	}

	public void kill(User user) {
		kill(user, false);
	}

	public void kill(User user, boolean leaving) {
		User killer = user.getLastHit();
		EventPlayerDeath pe1 = new EventPlayerDeath(user);
		Bukkit.getPluginManager().callEvent(pe1);
		boolean countAsDeath = killer != null
						? user.getTicksAfterLastHit() <= 200
						: false;
		if (countAsDeath) {
			killstreak.remove(user);
			EventPlayerKill pe2 = new EventPlayerKill(user, killer);
			Bukkit.getPluginManager().callEvent(pe2);
			if (!pe2.isCancelled()) {
				killer.setKills(killer.getKills() + 1);
				user.setDeaths(user.getDeaths() + 1);
				killstreak.put(killer, getKillstreak(killer) + 1);
				int killstreak = getKillstreak(killer);
				if (killstreak > 0 && killstreak % killsFor1Life == 0) {
					new Scheduler() {
						@Override
						public void run() {
							Main.getInstance().sendMessage(Message.KILLSTREAK, killer.getTeamPlayerName(), Integer.toString(killstreak));
						}
					}.runTask();
					killer.getTeam().setLifes(killer.getTeam().getLifes() + 1);
				}
				StatsLink.addKill(killer);
				StatsLink.addDeath(user);
				StatsLink.updateKillElo(killer, user);
			}
		}

		if (leaving) {
			killstreak.remove(user);
		}

		if (!countAsDeath && !leaving) {
			user.getBukkitEntity().teleport(user.getTeam().getSpawn());
			if (killer != null) {
				user.setLastHit(null);
				user.setSpawnProtectionTicks(SPAWNPROTECTION_TICKS);
			}
			return;
		}
//		Map<Message, Function<User, String[]>> msgs = new HashMap<>();
		if (killer != null) {
			if (user.getTicksAfterLastHit() < 201) {
//				msgs.put(Message.PLAYER_WAS_KILLED_BY_PLAYER,
//						u -> new String[] { user.getTeamPlayerName(), killer.getTeamPlayerName() });
				Main.getInstance().sendMessage(Message.PLAYER_WAS_KILLED_BY_PLAYER, user.getTeamPlayerName(), killer.getTeamPlayerName());
			} else if (user.getTicksAfterLastHit() <= 200) {
//				msgs.put(Message.PLAYER_DIED, u -> new String[] { user.getTeamPlayerName() });
				Main.getInstance().sendMessage(Message.PLAYER_DIED, user.getTeamPlayerName());
//			} else {
//				msgs.put(Message.PLAYER_DIED, u -> new String[] { "Player should not be able to die" });
			}
		}

		Team userTeam = user.getTeam();
		if (userTeam.getLifes() == 0 || leaving) {
			Main.getInstance().getTeamManager().setTeam(user, Main.getInstance().getTeamManager().getSpectator());
//			setSpectator(user);
		}

		if (userTeam.getUsers().size() == 0) {
//			for (Entry<Message, Function<User, String[]>> msg : msgs.entrySet()) {
//				Main.getInstance().sendMessage(msg.getKey(), msg.getValue());
//			}
//			msgs.clear();
			for (Entry<User, Team> e : lastTeam.entrySet()) {
				if (e.getValue() == userTeam) {
					StatsLink.addLoss(e.getKey());
				}
			}

			Main.getInstance().sendMessage(Message.TEAM_WAS_ELIMINATED, u -> new String[] {
							userTeam.getName(u)
			});
			if (leaving) {
				user.getBukkitEntity().kickPlayer("Disconnected");
			}
			checkGameEnd();
//			List<Team> teams = Main.getInstance().getTeamManager().getTeams().stream().filter(t -> t.getUsers().size() >= 1).collect(Collectors.toList());
//			if (teams.size() == 1) {
//				this.winner = teams.get(0);
//				for (User i : this.winner.getUsers()) {
//					StatsLink.addWin(i);
//				}
//				disable();
//				Main.getInstance().getEndgame().enable();
//				return;
//			}
		}
		if (countAsDeath) {
			if (user.getTeam().getLifes() > 0) {
				user.getTeam().setLifes(user.getTeam().getLifes() - 1);
			}
		}
		user.getBukkitEntity().teleport(user.getTeam().getSpawn());
		user.setSpawnProtectionTicks(SPAWNPROTECTION_TICKS);
	}

	public void checkGameEnd() {
		if (startingIngame)
			return;
		List<Team> teams = Main.getInstance().getTeamManager().getTeams().stream().filter(t -> t.getUsers().size() >= 1).collect(Collectors.toList());
		if (teams.size() == 1) {
			this.winner = teams.get(0);
			for (User i : this.winner.getUsers()) {
				StatsLink.addWin(i);
			}
			disable();
			Main.getInstance().getEndgame().enable();
		}
	}

	public void loadScoreboardObjective(User user) {
		Scoreboard sb = new Scoreboard(user);
		Objective obj = sb.getObjective(ScoreboardObjective.INGAME.getKey());
		int i = 0;
		for (Team team : Main.getInstance().getTeamManager().getTeams()) {
			eu.darkcube.minigame.woolbattle.util.scoreboard.Team t = sb.getTeam(team.getType().getIngameScoreboardTag());
			t.addPlayer(team.getType().getInvisibleTag());
			t.setSuffix(ChatColor.GOLD.toString() + Characters.SHIFT_SHIFT_LEFT
							+ team.getName(user));
			obj.setScore(team.getType().getInvisibleTag(), i++);
			reloadScoreboardLifes(sb, team);
		}
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public boolean revive(User target) {
		if (!lastTeam.containsKey(target)) {
			return false;
		}
		Main.getInstance().getTeamManager().setTeam(target, lastTeam.remove(target));
		return true;
	}

	public synchronized void splitPlayersToTeams() {
		Collection<? extends Team> teams = Main.getInstance().getTeamManager().getTeams();
		int chosenCount = 0;
		for (Team team : teams) {
			chosenCount += team.getUsers().size();
		}
		if (chosenCount == Main.getInstance().getUserWrapper().getUsers().size()) {
			if (chosenCount == 0) {
				throw new Error("Starting game without players!");
			}
			Set<User> users1 = new HashSet<>();
			for (Team team : teams) {
				if (team.getUsers().size() == chosenCount) {
					int half = team.getUsers().size() / 2;
					while (users1.size() < half) {
						Optional<? extends User> o = team.getUsers().stream().findAny();
						if (o.isPresent()) {
							User user = o.get();
							users1.add(user);
						} else {
							Main.getInstance().sendConsole("§cUser of team was somehow not found");
						}
					}
					break;
				}
			}
			if (users1.size() != 0) {
				for (Team team : teams) {
					if (team.getUsers().size() == 0) {
						for (User user : users1) {
							user.setTeam(team);
						}
						break;
					}
				}
			}
		}
		int max = teams.stream().findAny().get().getType().getMaxPlayers();
		for (int i = 0; i < max; i++) {
			for (Team team : teams) {
				if (team.getUsers().size() == i) {
					for (User user : Main.getInstance().getUserWrapper().getUsers()) {
						if (user.getTeam().getType() == TeamType.SPECTATOR) {
							user.setTeam(team);
							break;
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void setArmor(User user) {
		Player p = user.getBukkitEntity();
		Color color = DyeColor.getByData(user.getTeam().getType().getWoolColor()).getColor();
		if (listenerGhostInteract.ghosts.containsKey(user)) {
			color = Color.WHITE;
		}
		ItemStack boots = Item.ARMOR_LEATHER_BOOTS.getItem(user);
		LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
		meta.setColor(color);

		boots.setItemMeta(meta.clone());

		ItemStack leggings = Item.ARMOR_LEATHER_LEGGINGS.getItem(user);
		meta.setDisplayName(leggings.getItemMeta().getDisplayName());
		leggings.setItemMeta(meta.clone());

		ItemStack chest = Item.ARMOR_LEATHER_CHESTPLATE.getItem(user);
		meta.setDisplayName(chest.getItemMeta().getDisplayName());
		chest.setItemMeta(meta.clone());

		ItemStack helmet = Item.ARMOR_LEATHER_HELMET.getItem(user);
		meta.setDisplayName(helmet.getItemMeta().getDisplayName());
		helmet.setItemMeta(meta.clone());
		p.getInventory().setArmorContents(new ItemStack[] {
						boots, leggings, chest, helmet
		});
	}

	public void setPlayerItems(User user) {
		if (!isEnabled()) {
			return;
		}
		if (user.getTeam().getType() == TeamType.SPECTATOR) {
			fixSpectator(user);
			return;
		}

		CraftPlayer p = user.getBukkitEntity();
		InventoryView v = p.getOpenInventory();
		int woolcount = ItemManager.countItems(Material.WOOL, p.getInventory());
		v.getBottomInventory().clear();
		v.getTopInventory().clear();
		v.setCursor(new ItemStack(Material.AIR));

		setArmor(user);

		PlayerPerks perks = user.getData().getPerks();

		setPlayerItem(v, perks.getSlotBow(), Item.DEFAULT_BOW.getItem(user));
		setPlayerItem(v, perks.getSlotArrow(), Item.DEFAULT_ARROW.getItem(user));
//		v.setItem(perks.getSlotBow(), Item.DEFAULT_BOW.getItem(user));
//		v.setItem(perks.getSlotArrow(), Item.DEFAULT_ARROW.getItem(user));
		setPlayerItem(v, perks.getSlotShears(), Item.DEFAULT_SHEARS.getItem(user));
		user.getEnderPearl().setItem();
//		v.setItem(user.getData().getPerks().getSlotPearl(), Item.DEFAULT_PEARL.getItem(user));
//		v.setItem(perks.getSlotShears(), Item.DEFAULT_SHEARS.getItem(user));

		user.getActivePerk1().setItem();
		user.getActivePerk2().setItem();
		user.getPassivePerk().setItem();

//		v.setItem(user.getData().getPerks().getSlotActivePerk1(), user.getData().getPerks().getActivePerk1().toType()
//				.newPerkTypePerk(user, PerkNumber.ACTIVE_1).getItem().getItem(user));
//		v.setItem(user.getData().getPerks().getSlotActivePerk2(), user.getData().getPerks().getActivePerk2().toType()
//				.newPerkTypePerk(user, PerkNumber.ACTIVE_2).getItem().getItem(user));
//		v.setItem(user.getData().getPerks().getSlotPassivePerk(), user.getData().getPerks().getPassivePerk().toType()
//				.newPerkTypePerk(user, PerkNumber.PASSIVE).getItem().getItem(user));
		for (int i = 0; i < woolcount; i++) {
			p.getInventory().addItem(new ItemBuilder(
							Material.WOOL).setDurability(user.getTeam().getType().getWoolColor()).build());
		}
		p.getHandle().updateInventory(p.getHandle().activeContainer);
		p.getHandle().collidesWithEntities = true;
	}

	private void setPlayerItem(InventoryView v, int slot, ItemStack item) {
		if (slot == 100)
			v.setCursor(item);
		else
			v.setItem(slot, item);
	}

	public void fixSpectator(User user) {
		if (user.getTeam() != null)
			lastTeam.put(user, user.getTeam());
		Player p = user.getBukkitEntity();
//		Main.getInstance().getTeamManager().setTeam(user, Main.getInstance().getTeamManager().getSpectator());
		Scoreboard sb = new Scoreboard();
		p.setScoreboard(sb.getScoreboard());
		Main.initScoreboard(sb, user);
		Main.getInstance().getUserWrapper().getUsers().forEach(u -> {
			Scoreboard s = new Scoreboard(u);
			s.getTeam(user.getTeam().getType().getScoreboardTag()).addPlayer(user.getPlayerName());
			if (u.getTeam().getType() != TeamType.SPECTATOR) {
				u.getBukkitEntity().hidePlayer(p);
			} else {
				p.showPlayer(u.getBukkitEntity());
			}
			if (u != user) {
				sb.getTeam(u.getTeam().getType().getScoreboardTag()).addPlayer(u.getPlayerName());
			}
		});
		loadScoreboardObjective(user);
		reloadTab(user);
		p.spigot().setCollidesWithEntities(false);
		p.setAllowFlight(true);
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
		p.getInventory().setItem(0, Item.TELEPORT_COMPASS.getItem(user));
	}

//	public void setSpectator(User user) {
//		Player p = user.getBukkitEntity();
//		if (user.getTeam() != null)
//			lastTeam.put(user, user.getTeam());
//		Main.getInstance().getTeamManager().setTeam(user, Main.getInstance().getTeamManager().getSpectator());
//		Scoreboard sb = new Scoreboard();
//		p.setScoreboard(sb.getScoreboard());
//		Main.initScoreboard(sb, user);
//		Main.getInstance().getUserWrapper().getUsers().forEach(u -> {
//			Scoreboard s = new Scoreboard(u);
//			s.getTeam(user.getTeam().getType().getScoreboardTag()).addPlayer(user.getPlayerName());
//			if (u.getTeam().getType() != TeamType.SPECTATOR) {
//				u.getBukkitEntity().hidePlayer(p);
//			} else {
//				p.showPlayer(u.getBukkitEntity());
//			}
//			if (u != user) {
//				sb.getTeam(u.getTeam().getType().getScoreboardTag()).addPlayer(u.getPlayerName());
//			}
//		});
//		loadScoreboardObjective(user);
//		reloadTab(user);
//		p.spigot().setCollidesWithEntities(false);
//		p.setAllowFlight(true);
//		p.teleport(Main.getInstance().getMap().getSpawn(TeamType.SPECTATOR.getDisplayNameKey()));
//		p.getInventory().clear();
//		p.getInventory().setArmorContents(new ItemStack[4]);
//		p.getInventory().setItem(0, Item.TELEPORT_COMPASS.getItem(user));
//	}

	public void reloadScoreboardLifes(User user) {
		Scoreboard sb = new Scoreboard(user);
		for (Team team : Main.getInstance().getTeamManager().getTeams()) {
			reloadScoreboardLifes(sb, team);
		}
	}

	public void reloadScoreboardLifes(Scoreboard sb, Team team) {
		String llifes = Integer.toString(team.getLifes());
		if (llifes.length() > 3) {
			llifes = llifes.substring(0, 3);
		}
		sb.getTeam(team.getType().getIngameScoreboardTag()).setPrefix(ChatColor.translateAlternateColorCodes('&', "&6"
						+ Characters.SHIFT_SHIFT_RIGHT + " &4"
						+ Characters.HEART + "&r" + llifes + "&4"
						+ Characters.HEART + " "));
	}

	public void reloadTab(User user) {
		Header header = new Header(
						Main.getInstance().getConfig("config").getString(Config.TAB_HEADER));
		Footer footer = new Footer(
						Main.getInstance().getConfig("config").getString(Config.TAB_FOOTER));
		header.setMessage(header.getMessage().replace("%map%", Main.getInstance().getMap() == null
						? "Unknown Map"
						: Main.getInstance().getMap().getName()));
		footer.setMessage(footer.getMessage().replace("%map%", Main.getInstance().getMap() == null
						? "Unknown Map"
						: Main.getInstance().getMap().getName()));
		header.setMessage(header.getMessage().replace("%name%", Main.getInstance().name));
		footer.setMessage(footer.getMessage().replace("%name%", Main.getInstance().name));
		header.setMessage(header.getMessage().replace("%prefix%", "&8"
						+ Characters.SHIFT_SHIFT_RIGHT + " &6"
						+ Main.getInstance().tabprefix + " &8"
						+ Characters.SHIFT_SHIFT_LEFT));
		footer.setMessage(footer.getMessage().replace("%prefix%", "&8"
						+ Characters.SHIFT_SHIFT_RIGHT + " &6"
						+ Main.getInstance().tabprefix + " &8"
						+ Characters.SHIFT_SHIFT_LEFT));
		header.setMessage(header.getMessage().replace("%server%", Bukkit.getServerName()));
		footer.setMessage(footer.getMessage().replace("%server%", Bukkit.getServerName()));
		header.setMessage(ChatColor.translateAlternateColorCodes('&', header.getMessage()).replace("\\n", "\n"));
		footer.setMessage(ChatColor.translateAlternateColorCodes('&', footer.getMessage()).replace("\\n", "\n"));
		TabManager.setHeaderFooter(user, header, footer);
	}

	public boolean attack(User user, User target, boolean ignoreGhost) {
		if (user.isTrollMode()
						&& user.getTeam().getType() == TeamType.SPECTATOR) {
			return true;
		}
		if ((!isGlobalSpawnProtection && !target.hasSpawnProtection()
						&& target.getTeam() != user.getTeam()
						&& (!listenerGhostInteract.ghosts.containsKey(target)
										|| ignoreGhost))
						|| user.isTrollMode()) {
			target.setLastHit(user);
			target.setTicksAfterLastHit(0);
			return true;
		}
		return false;
	}

	public boolean attack(User user, User target) {
		return attack(user, target, false);
	}

	public static int getBlockDamage(Block block) {
//		if (block.getMetadata("arrowDamage").size() == 0)
//			return 0;
//		return block.getMetadata("arrowDamage").get(0).asInt();
		MetadataValue v;
		return ((v = getMetaData(block, "arrowDamage")) == null) ? 0
						: v.asInt();
	}

	public static MetadataValue getMetaData(Block block, String key) {
		if (block.getMetadata(key).size() > 0) {
			return block.getMetadata(key).get(0);
		}
		return null;
	}

	public static void resetBlockDamage(Block block) {
		block.removeMetadata("arrowDamage", Main.getInstance());
	}

	public static void playSoundNotEnoughWool(User user) {
		user.getBukkitEntity().playSound(user.getBukkitEntity().getLocation(), Sound.VILLAGER_NO, 1, 1);
	}

	@SuppressWarnings("deprecation")
	public static void setBlockDamage(Block block, int damage) {
		EventDamageBlock damageBlock = new EventDamageBlock(block,
						getBlockDamage(block), damage);
		if (damageBlock.isCancelled()) {
			return;
		}
		if (damage >= MAX_BLOCK_ARROW_HITS) {
			EventDestroyBlock destroyBlock = new EventDestroyBlock(block);
			if (!destroyBlock.isCancelled()) {
				Ingame ingame = Main.getInstance().getIngame();
				if (ingame.placedBlocks.contains(block)) {
					ingame.placedBlocks.remove(block);
				} else {
					ingame.breakedWool.put(block, block.getData());
				}
				block.setType(Material.AIR);
				resetBlockDamage(block);
				return;
			}
		}
//		block.setMetadata("arrowDamage", new FixedMetadataValue(Main.getInstance(), damage));
		setMetaData(block, "arrowDamage", damage);
	}

	public static void setMetaData(Block block, String key, Object value) {
		block.setMetadata(key, new FixedMetadataValue(Main.getInstance(),
						value));
	}
}