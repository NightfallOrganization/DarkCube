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
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.EventDamageBlock;
import eu.darkcube.minigame.woolbattle.event.EventDestroyBlock;
import eu.darkcube.minigame.woolbattle.event.EventPlayerDeath;
import eu.darkcube.minigame.woolbattle.event.EventPlayerKill;
import eu.darkcube.minigame.woolbattle.listener.ingame.*;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerBlink;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerBooster;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerCapsule;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerGhost;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerGrabber;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerGrandpasClock;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerGrapplingHook;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerLineBuilder;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerMinigun;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerRonjasToiletSplash;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerRope;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerSafetyPlatform;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerSlimePlatform;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerSwitcher;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerWallGenerator;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerWoolBomb;
import eu.darkcube.minigame.woolbattle.listener.ingame.standard.ListenerDoubleJump;
import eu.darkcube.minigame.woolbattle.listener.ingame.standard.ListenerEnderpearlLaunchable;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.PlayerPerks;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Characters;
import eu.darkcube.minigame.woolbattle.util.CloudNetLink;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemBuilder;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;
import eu.darkcube.minigame.woolbattle.util.ScoreboardObjective;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scheduler.SchedulerHeightDisplay;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Objective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.minigame.woolbattle.util.tab.Footer;
import eu.darkcube.minigame.woolbattle.util.tab.Header;
import eu.darkcube.minigame.woolbattle.util.tab.TabManager;

public class Ingame extends GamePhase {

	public static int MAX_BLOCK_ARROW_HITS =
			WoolBattle.getInstance().getConfig("config").getInt("maxblockarrowhits");

	public static int SPAWNPROTECTION_TICKS =
			WoolBattle.getInstance().getConfig("config").getInt("spawnprotectionticks");

	public static int SPAWNPROTECTION_TICKS_GLOBAL =
			WoolBattle.getInstance().getConfig("config").getInt("spawnprotectionticksglobal");

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

	public final ListenerCapsule listenerCapsule;

	public final ListenerSwitcher listenerSwitcher;

	public final ListenerInteract listenerInteract;

	public final ListenerGameModeChange listenerGameModeChange;

	public final ListenerPlayerMove listenerPlayerMove;

	public final ListenerEntitySpawn listenerEntitySpawn;

	public final ListenerWoolBomb listenerWoolBomb;

	public final ListenerTNTEntityDamageByEntity listenerTNTEntityDamageByEntity;

	public final ListenerExplode listenerExplode;

	public final ListenerLineBuilder listenerLineBuilder;

	public final ListenerRonjasToiletSplash listenerRonjasToiletSplash;

	public final ListenerEnderpearlLaunchable listenerEnderpearlLaunchable;

	public final ListenerSafetyPlatform listenerSafetyPlatform;

	public final ListenerWallGenerator listenerWallGenerator;

	public final ListenerBlink listenerBlink;

	public final ListenerGrandpasClock listenerGrandpasClock;

	public final ListenerGhost listenerGhost;

	public final ListenerMinigun listenerMinigun;

	public final ListenerDeathMove listenerDeathMove;

	public final ListenerGrabber listenerGrabber;

	public final ListenerBooster listenerBooster;

	public final ListenerGrapplingHook listenerGrapplingHook;

	public final ListenerRope listenerRope;

	public final ListenerSlimePlatform listenerSlimePlatform;

	public final Scheduler schedulerResetWool;

	public final Scheduler schedulerResetSpawnProtetion;

	public final Scheduler schedulerParticles;

	public final Scheduler schedulerGlobalSpawnProtection;

	public final Scheduler schedulerTick;

	public final Scheduler schedulerGhostItemFix;

	public final SchedulerHeightDisplay schedulerHeightDisplay;

	public final Map<User, Team> lastTeam = new HashMap<>();

	// public final Scheduler schedulerDeathTimer;
	public boolean isGlobalSpawnProtection = false;

	private boolean startingIngame = false;

	public Team winner;

	public Ingame() {
		this.listenerItemPickup = new ListenerItemPickup();
		this.listenerItemDrop = new ListenerItemDrop();
		this.listenerBlockBreak = new ListenerBlockBreak();
		this.listenerBlockPlace = new ListenerBlockPlace();
		this.listenerBlockCanBuild = new ListenerBlockCanBuild();
		this.listenerPlayerJoin = new ListenerPlayerJoin();
		this.listenerPlayerQuit = new ListenerPlayerQuit();
		this.listenerPlayerLogin = new ListenerPlayerLogin();
		this.listenerEntityDamageByEntity = new ListenerEntityDamageByEntity();
		this.listenerDoubleJump = new ListenerDoubleJump();
		this.listenerEntityDamage = new ListenerEntityDamage();
		this.listenerChangeBlock = new ListenerChangeBlock();
		this.listenerProjectileHit = new ListenerProjectileHit();
		this.listenerProjectileLaunch = new ListenerProjectileLaunch();
		this.listenerInventoryClick = new ListenerInventoryClick();
		this.listenerInventoryDrag = new ListenerInventoryDrag();
		this.listenerCapsule = new ListenerCapsule();
		this.listenerSwitcher = new ListenerSwitcher();
		this.listenerInteract = new ListenerInteract();
		this.listenerGameModeChange = new ListenerGameModeChange();
		this.listenerPlayerMove = new ListenerPlayerMove();
		this.listenerEntitySpawn = new ListenerEntitySpawn();
		this.listenerTNTEntityDamageByEntity = new ListenerTNTEntityDamageByEntity();
		this.listenerWoolBomb = new ListenerWoolBomb();
		this.listenerExplode = new ListenerExplode();
		this.listenerLineBuilder = new ListenerLineBuilder();
		this.listenerRonjasToiletSplash = new ListenerRonjasToiletSplash();
		this.listenerEnderpearlLaunchable = new ListenerEnderpearlLaunchable();
		this.listenerSafetyPlatform = new ListenerSafetyPlatform();
		this.listenerBlink = new ListenerBlink();
		this.listenerWallGenerator = new ListenerWallGenerator();
		this.listenerGrandpasClock = new ListenerGrandpasClock();
		this.listenerGhost = new ListenerGhost();
		this.listenerMinigun = new ListenerMinigun();
		this.listenerDeathMove = new ListenerDeathMove();
		this.listenerGrabber = new ListenerGrabber();
		this.listenerBooster = new ListenerBooster();
		this.listenerGrapplingHook = new ListenerGrapplingHook();
		this.listenerRope = new ListenerRope();
		this.listenerSlimePlatform = new ListenerSlimePlatform();

		this.schedulerParticles = new Scheduler() {

			@Override
			public void run() {
				for (Arrow arrow : Ingame.this.arrows.keySet()) {
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
						if (arrow.isDead() || arrow.isOnGround() || !arrow.isValid()
								|| !((Player) arrow.getShooter()).isOnline()
								|| !arrow.getLocation().getChunk().isLoaded()) {
							arrow.remove();
							Ingame.this.arrows.remove(arrow);
							break;
						}
						User user = WoolBattle.getInstance().getUserWrapper()
								.getUser(((Player) arrow.getShooter()).getUniqueId());
						ParticleEffect.BLOCK_CRACK.display(
								new ParticleEffect.BlockData(Material.WOOL,
										user.getTeam().getType().getWoolColorByte()),
								0, 0, 0, 1, 8, arrow.getLocation(), Ingame.this.particlePlayers);
					}
				}
			}

		};
		this.schedulerGlobalSpawnProtection = new Scheduler() {

			public int protectionTicks = Ingame.SPAWNPROTECTION_TICKS_GLOBAL;

			@Override
			public void run() {
				if (this.protectionTicks - 1 <= 0 || Ingame.SPAWNPROTECTION_TICKS_GLOBAL == 0) {
					this.protectionTicks = Ingame.SPAWNPROTECTION_TICKS_GLOBAL;
					this.cancel();
					return;
				}
				Ingame.this.isGlobalSpawnProtection = true;
				this.protectionTicks--;
				for (User user : WoolBattle.getInstance().getUserWrapper().getUsers()) {
					user.getBukkitEntity().setExp((float) this.protectionTicks
							/ (float) Ingame.SPAWNPROTECTION_TICKS_GLOBAL);
				}
			}

			@Override
			public void cancel() {
				if (!this.isCancelled()) {
					Ingame.this.isGlobalSpawnProtection = false;
					Ingame.this.schedulerResetSpawnProtetion.runTaskTimer(1);
					super.cancel();
				}
			}

		};
		this.schedulerTick = new Scheduler() {

			@Override
			public void run() {
				for (User user : WoolBattle.getInstance().getUserWrapper().getUsers()) {
					if (user.getTeam().getType() != TeamType.SPECTATOR) {
						if (user.getTicksAfterLastHit() < 1200)
							user.setTicksAfterLastHit(user.getTicksAfterLastHit() + 1);
					}
				}
			}

		};
		this.schedulerResetSpawnProtetion = new Scheduler() {

			@Override
			public void run() {
				for (User user : WoolBattle.getInstance().getUserWrapper().getUsers()) {
					if (Ingame.SPAWNPROTECTION_TICKS != 0) {
						if (user.getSpawnProtectionTicks() > 0) {
							user.setSpawnProtectionTicks(user.getSpawnProtectionTicks() - 1);
						}
					}
				}
			}

		};
		this.schedulerResetWool = new Scheduler() {

			@SuppressWarnings("deprecation")
			@Override
			public synchronized void run() {
				for (Block b : Ingame.this.breakedWool.keySet()) {
					b.setType(Material.WOOL);
					b.setData(Ingame.this.breakedWool.get(b));
				}
				Ingame.this.breakedWool.clear();
			}

		};
		this.schedulerHeightDisplay = new SchedulerHeightDisplay();
		this.schedulerGhostItemFix = new Scheduler() {

			@Override
			public void run() {
				for (User user : WoolBattle.getInstance().getUserWrapper().getUsers()) {
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
		return this.lastTeam.containsKey(user) ? this.lastTeam.get(user) : user.getTeam();
	}

	@Override
	public void onEnable() {
		this.startingIngame = true;
		CloudNetLink.update();
		this.splitPlayersToTeams();
		// Main.getInstance().getSchedulers().clear();
		int lifes = -1;
		if (!WoolBattle.getInstance().getLobby().VOTES_LIFES.isEmpty()) {
			Collection<Integer> list = WoolBattle.getInstance().getLobby().VOTES_LIFES.values();
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
		if (WoolBattle.getInstance().baseLifes != null)
			lifes = WoolBattle.getInstance().baseLifes;
		for (Team team : WoolBattle.getInstance().getTeamManager().getTeams()) {
			team.setLifes(lifes);
		}

		this.listenerDoubleJump.cooldown.clear();
		this.listenerPlayerMove.ghostBlockFixCount.clear();

		if (this.placedBlocks != null)
			for (Block b : this.placedBlocks) {
				b.setType(Material.AIR);
				Ingame.resetBlockDamage(b);
			}

		WoolBattle.registerListeners(this.listenerBlockBreak, this.listenerBlockPlace,
				this.listenerItemDrop, this.listenerItemPickup, this.listenerBlockCanBuild,
				this.listenerPlayerJoin, this.listenerPlayerQuit, this.listenerPlayerLogin,
				this.listenerEntityDamageByEntity, this.listenerDoubleJump,
				this.listenerEntityDamage, this.listenerChangeBlock, this.listenerProjectileHit,
				this.listenerProjectileLaunch, this.listenerInventoryClick,
				this.listenerInventoryDrag, this.listenerCapsule, this.listenerSwitcher,
				this.listenerInteract, this.listenerGameModeChange, this.listenerPlayerMove,
				this.listenerEntitySpawn, this.listenerEnderpearlLaunchable,
				this.listenerSafetyPlatform, this.listenerTNTEntityDamageByEntity,
				this.listenerWoolBomb, this.listenerExplode, this.listenerLineBuilder,
				this.listenerRonjasToiletSplash, this.listenerBlink, this.listenerWallGenerator,
				this.listenerGrandpasClock, this.listenerGhost, this.listenerMinigun,
				this.listenerDeathMove, this.listenerGrabber, this.listenerBooster,
				this.listenerGrapplingHook, this.listenerRope, this.listenerSlimePlatform);

		WoolBattle.getInstance().getUserWrapper().getUsers().forEach(u -> {
			this.loadScoreboardObjective(u);
			u.loadPerks();
			u.getBukkitEntity().closeInventory();
			this.setPlayerItems(u);
			u.setTicksAfterLastHit(1200);
			u.getBukkitEntity().teleport(u.getTeam().getSpawn());
		});

		this.isGlobalSpawnProtection = true;

		this.schedulerResetWool.runTaskTimer(16);
		this.schedulerParticles.runTaskTimer(1);
		// This one also starts the normal spawnprotection scheduler
		this.schedulerGlobalSpawnProtection.runTaskTimer(1);
		this.schedulerTick.runTaskTimer(1);
		this.schedulerHeightDisplay.start();

		for (Player p : Bukkit.getOnlinePlayers()) {
			for (Player t : Bukkit.getOnlinePlayers()) {
				if (p != t) {
					if (!p.canSee(t)) {
						p.showPlayer(t);
					}
				}
			}
		}
		this.startingIngame = false;
	}

	@Override
	public void onDisable() {
		this.schedulerGlobalSpawnProtection.cancel();
		this.schedulerResetSpawnProtetion.cancel();
		this.schedulerParticles.cancel();
		this.schedulerResetWool.cancel();
		this.schedulerTick.cancel();
		this.schedulerHeightDisplay.stop();
		WoolBattle.unregisterListeners(this.listenerBlockBreak, this.listenerBlockPlace,
				this.listenerItemDrop, this.listenerItemPickup, this.listenerBlockCanBuild,
				this.listenerPlayerJoin, this.listenerPlayerQuit, this.listenerPlayerLogin,
				this.listenerEntityDamageByEntity, this.listenerDoubleJump,
				this.listenerEntityDamage, this.listenerChangeBlock, this.listenerProjectileHit,
				this.listenerProjectileLaunch, this.listenerInventoryClick,
				this.listenerInventoryDrag, this.listenerCapsule, this.listenerSwitcher,
				this.listenerInteract, this.listenerGameModeChange, this.listenerPlayerMove,
				this.listenerEntitySpawn, this.listenerEnderpearlLaunchable,
				this.listenerSafetyPlatform, this.listenerTNTEntityDamageByEntity,
				this.listenerWoolBomb, this.listenerExplode, this.listenerLineBuilder,
				this.listenerRonjasToiletSplash, this.listenerBlink, this.listenerWallGenerator,
				this.listenerGrandpasClock, this.listenerGhost, this.listenerMinigun,
				this.listenerDeathMove, this.listenerGrabber, this.listenerBooster,
				this.listenerGrapplingHook, this.listenerRope, this.listenerSlimePlatform);
		this.listenerEnderpearlLaunchable.disable();
		for (Block b : this.placedBlocks) {
			b.setType(Material.AIR);
			Ingame.resetBlockDamage(b);
		}
	}

	public int getKillstreak(User user) {
		return this.killstreak.getOrDefault(user, 0);
	}

	public void kill(User user) {
		this.kill(user, false);
	}

	public void kill(User user, boolean leaving) {
		final User killer = user.getLastHit();
		EventPlayerDeath pe1 = new EventPlayerDeath(user);
		Bukkit.getPluginManager().callEvent(pe1);
		final boolean countAsDeath = killer != null ? user.getTicksAfterLastHit() <= 200 : false;
		if (countAsDeath) {
			this.killstreak.remove(user);
			EventPlayerKill pe2 = new EventPlayerKill(user, killer);
			Bukkit.getPluginManager().callEvent(pe2);
			if (!pe2.isCancelled()) {
				killer.setKills(killer.getKills() + 1);
				user.setDeaths(user.getDeaths() + 1);
				this.killstreak.put(killer, this.getKillstreak(killer) + 1);
				int killstreak = this.getKillstreak(killer);
				if (killstreak > 0 && killstreak % this.killsFor1Life == 0) {
					new Scheduler(() -> WoolBattle.getInstance().sendMessage(Message.KILLSTREAK,
							killer.getTeamPlayerName(), Integer.toString(killstreak))).runTask();
					killer.getTeam().setLifes(killer.getTeam().getLifes() + 1);
				}
				StatsLink.addKill(killer);
				StatsLink.addDeath(user);
				StatsLink.updateKillElo(killer, user);
			}
		}

		if (leaving) {
			this.killstreak.remove(user);
		}

		if (!countAsDeath && !leaving) {
			user.getBukkitEntity().teleport(user.getTeam().getSpawn());
			if (killer != null) {
				user.setLastHit(null);
				user.setSpawnProtectionTicks(Ingame.SPAWNPROTECTION_TICKS);
			}
			return;
		}
		if (killer != null) {
			if (user.getTicksAfterLastHit() < 201) {
				WoolBattle.getInstance().sendMessage(Message.PLAYER_WAS_KILLED_BY_PLAYER,
						user.getTeamPlayerName(), killer.getTeamPlayerName());
			} else if (user.getTicksAfterLastHit() <= 200) {
				WoolBattle.getInstance().sendMessage(Message.PLAYER_DIED, user.getTeamPlayerName());
			}
		}

		Team userTeam = user.getTeam();
		if (userTeam.getLifes() == 0 || leaving) {
			WoolBattle.getInstance().getTeamManager().setTeam(user,
					WoolBattle.getInstance().getTeamManager().getSpectator());
		}

		if (userTeam.getUsers().size() == 0) {
			for (Entry<User, Team> e : this.lastTeam.entrySet()) {
				if (e.getValue() == userTeam) {
					StatsLink.addLoss(e.getKey());
				}
			}

			WoolBattle.getInstance().sendMessage(Message.TEAM_WAS_ELIMINATED,
					u -> new String[] {userTeam.getName(u)});
			if (leaving) {
				user.getBukkitEntity().kickPlayer("Disconnected");
			}
			this.checkGameEnd();
		}
		if (countAsDeath) {
			if (user.getTeam().getLifes() > 0) {
				user.getTeam().setLifes(user.getTeam().getLifes() - 1);
			}
		}
		user.getBukkitEntity().teleport(user.getTeam().getSpawn());
		user.setTicksAfterLastHit((int) TimeUnit.SECOND.toTicks(60));
		user.setSpawnProtectionTicks(Ingame.SPAWNPROTECTION_TICKS);
		CloudNetLink.update();
	}

	public void checkGameEnd() {
		if (this.startingIngame)
			return;
		List<Team> teams = WoolBattle.getInstance().getTeamManager().getTeams().stream()
				.filter(t -> t.getUsers().size() >= 1).collect(Collectors.toList());
		if (teams.size() == 1) {
			this.winner = teams.get(0);
			for (User i : this.winner.getUsers()) {
				StatsLink.addWin(i);
			}
			this.disable();
			WoolBattle.getInstance().getEndgame().enable();
		}
	}

	public void loadScoreboardObjective(User user) {
		Scoreboard sb = new Scoreboard(user);
		Objective obj = sb.getObjective(ScoreboardObjective.INGAME.getKey());
		int i = 0;
		for (Team team : WoolBattle.getInstance().getTeamManager().getTeams()) {
			eu.darkcube.minigame.woolbattle.util.scoreboard.Team t =
					sb.getTeam(team.getType().getIngameScoreboardTag());
			t.addPlayer(team.getType().getInvisibleTag());
			t.setSuffix(
					ChatColor.GOLD.toString() + Characters.SHIFT_SHIFT_LEFT + team.getName(user));
			obj.setScore(team.getType().getInvisibleTag(), i++);
			this.reloadScoreboardLifes(sb, team);
		}
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public boolean revive(User target) {
		if (!this.lastTeam.containsKey(target)) {
			return false;
		}
		WoolBattle.getInstance().getTeamManager().setTeam(target, this.lastTeam.remove(target));
		return true;
	}

	public synchronized void splitPlayersToTeams() {
		Collection<? extends Team> teams = WoolBattle.getInstance().getTeamManager().getTeams();
		int chosenCount = 0;
		for (Team team : teams) {
			chosenCount += team.getUsers().size();
		}
		if (chosenCount == WoolBattle.getInstance().getUserWrapper().getUsers().size()) {
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
							WoolBattle.getInstance()
									.sendConsole("§cUser of team was somehow not found");
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
					for (User user : WoolBattle.getInstance().getUserWrapper().getUsers()) {
						if (user.getTeam().getType() == TeamType.SPECTATOR) {
							user.setTeam(team);
							break;
						}
					}
				}
			}
		}
	}

	public void setArmor(User user) {
		Player p = user.getBukkitEntity();
		Color color = user.getTeam().getType().getWoolColor().getColor();
		if (ListenerGhost.isGhost(user)) {
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
		p.getInventory().setArmorContents(new ItemStack[] {boots, leggings, chest, helmet});
	}

	public void setPlayerItems(User user) {
		if (!this.isEnabled()) {
			return;
		}
		if (user.getTeam().getType() == TeamType.SPECTATOR) {
			this.fixSpectator(user);
			return;
		}

		CraftPlayer p = user.getBukkitEntity();
		InventoryView v = p.getOpenInventory();
		int woolcount = ItemManager.countItems(Material.WOOL, p.getInventory());
		v.getBottomInventory().clear();
		v.getTopInventory().clear();
		v.setCursor(new ItemStack(Material.AIR));

		this.setArmor(user);

		PlayerPerks perks = user.getData().getPerks();
		Set<Integer> slots = new HashSet<>();
		slots.add(perks.getSlotActivePerk1());
		slots.add(perks.getSlotActivePerk2());
		slots.add(perks.getSlotPassivePerk());
		slots.add(perks.getSlotShears());
		slots.add(perks.getSlotBow());
		slots.add(perks.getSlotArrow());
		if (slots.size() < 6) {
			PerkName a1 = perks.getActivePerk1();
			PerkName a2 = perks.getActivePerk2();
			PerkName p1 = perks.getPassivePerk();
			perks.reset();
			perks.setActivePerk1(a1);
			perks.setActivePerk2(a2);
			perks.setPassivePerk(p1);
		}

		this.setPlayerItem(v, perks.getSlotBow(), Item.DEFAULT_BOW.getItem(user));
		this.setPlayerItem(v, perks.getSlotArrow(), Item.DEFAULT_ARROW.getItem(user));
		this.setPlayerItem(v, perks.getSlotShears(), Item.DEFAULT_SHEARS.getItem(user));
		user.getEnderPearl().setItem();

		user.getActivePerk1().setItem();
		user.getActivePerk2().setItem();
		user.getPassivePerk().setItem();

		for (int i = 0; i < woolcount; i++) {
			p.getInventory().addItem(new ItemBuilder(Material.WOOL)
					.setDurability(user.getTeam().getType().getWoolColorByte()).build());
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
			this.lastTeam.put(user, user.getTeam());
		Player p = user.getBukkitEntity();
		// Main.getInstance().getTeamManager().setTeam(user,
		// Main.getInstance().getTeamManager().getSpectator());
		Scoreboard sb = new Scoreboard();
		p.setScoreboard(sb.getScoreboard());
		WoolBattle.initScoreboard(sb, user);
		WoolBattle.getInstance().getUserWrapper().getUsers().forEach(u -> {
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
		this.loadScoreboardObjective(user);
		this.reloadTab(user);
		p.spigot().setCollidesWithEntities(false);
		p.setAllowFlight(true);
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
		p.getInventory().setItem(0, Item.TELEPORT_COMPASS.getItem(user));
	}

	// public void setSpectator(User user) {
	// Player p = user.getBukkitEntity();
	// if (user.getTeam() != null)
	// lastTeam.put(user, user.getTeam());
	// Main.getInstance().getTeamManager().setTeam(user,
	// Main.getInstance().getTeamManager().getSpectator());
	// Scoreboard sb = new Scoreboard();
	// p.setScoreboard(sb.getScoreboard());
	// Main.initScoreboard(sb, user);
	// Main.getInstance().getUserWrapper().getUsers().forEach(u -> {
	// Scoreboard s = new Scoreboard(u);
	// s.getTeam(user.getTeam().getType().getScoreboardTag()).addPlayer(user.getPlayerName());
	// if (u.getTeam().getType() != TeamType.SPECTATOR) {
	// u.getBukkitEntity().hidePlayer(p);
	// } else {
	// p.showPlayer(u.getBukkitEntity());
	// }
	// if (u != user) {
	// sb.getTeam(u.getTeam().getType().getScoreboardTag()).addPlayer(u.getPlayerName());
	// }
	// });
	// loadScoreboardObjective(user);
	// reloadTab(user);
	// p.spigot().setCollidesWithEntities(false);
	// p.setAllowFlight(true);
	// p.teleport(Main.getInstance().getMap().getSpawn(TeamType.SPECTATOR.getDisplayNameKey()));
	// p.getInventory().clear();
	// p.getInventory().setArmorContents(new ItemStack[4]);
	// p.getInventory().setItem(0, Item.TELEPORT_COMPASS.getItem(user));
	// }

	public void reloadScoreboardLifes(User user) {
		Scoreboard sb = new Scoreboard(user);
		for (Team team : WoolBattle.getInstance().getTeamManager().getTeams()) {
			this.reloadScoreboardLifes(sb, team);
		}
	}

	public void reloadScoreboardLifes(Scoreboard sb, Team team) {
		String llifes = Integer.toString(team.getLifes());
		if (llifes.length() > 3) {
			llifes = llifes.substring(0, 3);
		}
		sb.getTeam(team.getType().getIngameScoreboardTag())
				.setPrefix(ChatColor.translateAlternateColorCodes('&',
						"&6" + Characters.SHIFT_SHIFT_RIGHT + " &4" + Characters.HEART + "&r"
								+ llifes + "&4" + Characters.HEART + " "));
	}

	public void reloadTab(User user) {
		Header header = new Header(
				WoolBattle.getInstance().getConfig("config").getString(Config.TAB_HEADER));
		Footer footer = new Footer(
				WoolBattle.getInstance().getConfig("config").getString(Config.TAB_FOOTER));
		header.setMessage(header.getMessage().replace("%map%",
				WoolBattle.getInstance().getMap() == null ? "Unknown Map"
						: WoolBattle.getInstance().getMap().getName()));
		footer.setMessage(footer.getMessage().replace("%map%",
				WoolBattle.getInstance().getMap() == null ? "Unknown Map"
						: WoolBattle.getInstance().getMap().getName()));
		header.setMessage(header.getMessage().replace("%name%", WoolBattle.getInstance().name));
		footer.setMessage(footer.getMessage().replace("%name%", WoolBattle.getInstance().name));
		header.setMessage(header.getMessage().replace("%prefix%",
				"&8" + Characters.SHIFT_SHIFT_RIGHT + " &6" + WoolBattle.getInstance().tabprefix
						+ " &8" + Characters.SHIFT_SHIFT_LEFT));
		footer.setMessage(footer.getMessage().replace("%prefix%",
				"&8" + Characters.SHIFT_SHIFT_RIGHT + " &6" + WoolBattle.getInstance().tabprefix
						+ " &8" + Characters.SHIFT_SHIFT_LEFT));
		header.setMessage(header.getMessage().replace("%server%", Bukkit.getServerName()));
		footer.setMessage(footer.getMessage().replace("%server%", Bukkit.getServerName()));
		header.setMessage(ChatColor.translateAlternateColorCodes('&', header.getMessage())
				.replace("\\n", "\n"));
		footer.setMessage(ChatColor.translateAlternateColorCodes('&', footer.getMessage())
				.replace("\\n", "\n"));
		TabManager.setHeaderFooter(user, header, footer);
	}

	public boolean attack(User user, User target, boolean ignoreGhost) {
		if (user.isTrollMode() && user.getTeam().getType() == TeamType.SPECTATOR) {
			return true;
		}
		if ((!this.isGlobalSpawnProtection && !target.hasSpawnProtection()
				&& target.getTeam() != user.getTeam()
				&& (!ListenerGhost.isGhost(user) || ignoreGhost)) || user.isTrollMode()) {
			target.setLastHit(user);
			target.setTicksAfterLastHit(0);
			return true;
		}
		return false;
	}

	public boolean attack(User user, User target) {
		return this.attack(user, target, false);
	}

	public static int getBlockDamage(Block block) {
		MetadataValue v;
		return ((v = Ingame.getMetaData(block, "arrowDamage")) == null) ? 0 : v.asInt();
	}

	public static MetadataValue getMetaData(Block block, String key) {
		if (block.getMetadata(key).size() > 0) {
			return block.getMetadata(key).get(0);
		}
		return null;
	}

	public static void resetBlockDamage(Block block) {
		block.removeMetadata("arrowDamage", WoolBattle.getInstance());
	}

	public static void playSoundNotEnoughWool(User user) {
		user.getBukkitEntity().playSound(user.getBukkitEntity().getLocation(), Sound.VILLAGER_NO, 1,
				1);
	}

	@SuppressWarnings("deprecation")
	public static void setBlockDamage(Block block, int damage) {
		EventDamageBlock damageBlock =
				new EventDamageBlock(block, Ingame.getBlockDamage(block), damage);
		if (damageBlock.isCancelled()) {
			return;
		}
		if (damage >= Ingame.MAX_BLOCK_ARROW_HITS) {
			EventDestroyBlock destroyBlock = new EventDestroyBlock(block);
			if (!destroyBlock.isCancelled()) {
				Ingame ingame = WoolBattle.getInstance().getIngame();
				if (ingame.placedBlocks.contains(block)) {
					ingame.placedBlocks.remove(block);
				} else {
					ingame.breakedWool.put(block, block.getData());
				}
				block.setType(Material.AIR);
				Ingame.resetBlockDamage(block);
				return;
			}
		}
		// block.setMetadata("arrowDamage", new FixedMetadataValue(Main.getInstance(), damage));
		Ingame.setMetaData(block, "arrowDamage", damage);
	}

	public static void setMetaData(Block block, String key, Object value) {
		block.setMetadata(key, new FixedMetadataValue(WoolBattle.getInstance(), value));
	}

}
