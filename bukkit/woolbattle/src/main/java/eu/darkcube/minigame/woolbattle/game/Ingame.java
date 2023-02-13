/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.game;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.EventDamageBlock;
import eu.darkcube.minigame.woolbattle.event.EventDestroyBlock;
import eu.darkcube.minigame.woolbattle.event.EventPlayerDeath;
import eu.darkcube.minigame.woolbattle.event.EventPlayerKill;
import eu.darkcube.minigame.woolbattle.listener.ingame.*;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.active.*;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.misc.ListenerEnderpearlLaunchable;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.passive.ListenerDoubleJump;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.PlayerPerks;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.*;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scheduler.SchedulerHeightDisplay;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Objective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.*;
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

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Ingame extends GamePhase {

	public static int MAX_BLOCK_ARROW_HITS =
			WoolBattle.getInstance().getConfig("config").getInt("maxblockarrowhits");

	public static int SPAWNPROTECTION_TICKS =
			WoolBattle.getInstance().getConfig("config").getInt("spawnprotectionticks");

	public static int SPAWNPROTECTION_TICKS_GLOBAL =
			WoolBattle.getInstance().getConfig("config").getInt("spawnprotectionticksglobal");
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
	public final SchedulerHeightDisplay schedulerHeightDisplay;
	public final Map<WBUser, Team> lastTeam = new HashMap<>();
	public Set<Block> placedBlocks = new HashSet<>();
	public Map<Arrow, WBUser> arrows = new HashMap<>();
	public Map<Block, Byte> breakedWool = new HashMap<>();
	public Map<WBUser, Integer> killstreak = new HashMap<>();
	public int killsFor1Life = 5;
	// public final Scheduler schedulerDeathTimer;
	public boolean isGlobalSpawnProtection = false;
	public Team winner;
	private boolean startingIngame = false;

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
								|| !((Player) arrow.getShooter()).isOnline() || !arrow.getLocation()
								.getChunk().isLoaded()) {
							arrow.remove();
							Ingame.this.arrows.remove(arrow);
							break;
						}
						WBUser user = WBUser.getUser(((Player) arrow.getShooter()));
						ParticleEffect.BLOCK_CRACK.display(
								new ParticleEffect.BlockData(Material.WOOL,
										user.getTeam().getType().getWoolColorByte()), 0, 0, 0, 1, 8,
								arrow.getLocation(), Bukkit.getOnlinePlayers().stream()
										.map(UserAPI.getInstance()::getUser).map(User::asPlayer)
										.collect(Collectors.toList()));
					}
				}
			}

		};
		this.schedulerGlobalSpawnProtection = new Scheduler() {

			public int protectionTicks = Ingame.SPAWNPROTECTION_TICKS_GLOBAL;

			@Override
			public void cancel() {
				if (!this.isCancelled()) {
					Ingame.this.isGlobalSpawnProtection = false;
					Ingame.this.schedulerResetSpawnProtetion.runTaskTimer(1);
					super.cancel();
				}
			}

			@Override
			public void run() {
				if (this.protectionTicks - 1 <= 0 || Ingame.SPAWNPROTECTION_TICKS_GLOBAL == 0) {
					this.protectionTicks = Ingame.SPAWNPROTECTION_TICKS_GLOBAL;
					this.cancel();
					return;
				}
				Ingame.this.isGlobalSpawnProtection = true;
				this.protectionTicks--;
				for (WBUser user : WBUser.onlineUsers()) {
					user.getBukkitEntity().setExp((float) this.protectionTicks
							/ (float) Ingame.SPAWNPROTECTION_TICKS_GLOBAL);
				}
			}

		};
		this.schedulerTick = new Scheduler() {

			@Override
			public void run() {
				for (WBUser user : WBUser.onlineUsers()) {
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
				for (WBUser user : WBUser.onlineUsers()) {
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

	public static void playSoundNotEnoughWool(WBUser user) {
		user.getBukkitEntity()
				.playSound(user.getBukkitEntity().getLocation(), Sound.VILLAGER_NO, 1, 1);
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
		if (value == null) {
			block.removeMetadata(key, WoolBattle.getInstance());
		} else {
			block.setMetadata(key, new FixedMetadataValue(WoolBattle.getInstance(), value));
		}

	}

	public Team getLastTeam(WBUser user) {
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
				xtra = 0;
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

		WBUser.onlineUsers().forEach(u -> {
			this.loadScoreboardObjective(u);
			u.perks().reloadFromStorage();
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
		for (Block b : this.placedBlocks) {
			b.setType(Material.AIR);
			Ingame.resetBlockDamage(b);
		}
	}

	public int getKillstreak(WBUser user) {
		return this.killstreak.getOrDefault(user, 0);
	}

	public void kill(WBUser user) {
		this.kill(user, false);
	}

	public void kill(WBUser user, boolean leaving) {
		final WBUser killer = user.getLastHit();
		EventPlayerDeath pe1 = new EventPlayerDeath(user);
		Bukkit.getPluginManager().callEvent(pe1);
		final boolean countAsDeath = killer != null && user.getTicksAfterLastHit() <= 200;
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
					new Scheduler(() -> WoolBattle.getInstance()
							.sendMessage(Message.KILLSTREAK, killer.getTeamPlayerName(),
									Integer.toString(killstreak))).runTask();
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
				WoolBattle.getInstance()
						.sendMessage(Message.PLAYER_WAS_KILLED_BY_PLAYER, user.getTeamPlayerName(),
								killer.getTeamPlayerName());
			} else if (user.getTicksAfterLastHit() <= 200) {
				WoolBattle.getInstance().sendMessage(Message.PLAYER_DIED, user.getTeamPlayerName());
			}
		}

		Team userTeam = user.getTeam();
		if (userTeam.getLifes() == 0 || leaving) {
			WoolBattle.getInstance().getTeamManager()
					.setTeam(user, WoolBattle.getInstance().getTeamManager().getSpectator());
		}

		if (userTeam.getUsers().size() == 0) {
			for (Entry<WBUser, Team> e : this.lastTeam.entrySet()) {
				if (e.getValue() == userTeam) {
					StatsLink.addLoss(e.getKey());
				}
			}

			WoolBattle.getInstance().sendMessage(Message.TEAM_WAS_ELIMINATED,
					u -> new Object[] {userTeam.getName(u)});
			if (leaving) {
				user.getBukkitEntity().kickPlayer("Disconnected");
			}
			this.checkGameEnd();
			CloudNetLink.update();
			return;
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
			for (WBUser i : this.winner.getUsers()) {
				StatsLink.addWin(i);
			}
			this.disable();
			WoolBattle.getInstance().getEndgame().enable();
		}
	}

	public void loadScoreboardObjective(WBUser user) {
		Scoreboard sb = new Scoreboard(user);
		Objective obj = sb.getObjective(ScoreboardObjective.INGAME.getKey());
		int i = 0;
		for (Team team : WoolBattle.getInstance().getTeamManager().getTeams()) {
			eu.darkcube.minigame.woolbattle.util.scoreboard.Team t =
					sb.getTeam(team.getType().getIngameScoreboardTag());
			t.addPlayer(team.getType().getInvisibleTag());
			t.setSuffix(Component.text(Characters.SHIFT_SHIFT_LEFT.toString())
					.color(NamedTextColor.GOLD).append(team.getName(user.user())));
			obj.setScore(team.getType().getInvisibleTag(), i++);
			this.reloadScoreboardLifes(sb, team);
		}
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
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

	public boolean revive(WBUser target) {
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
		if (chosenCount == WBUser.onlineUsers().size()) {
			if (chosenCount == 0) {
				throw new Error("Starting game without players!");
			}
			Set<WBUser> users1 = new HashSet<>();
			for (Team team : teams) {
				if (team.getUsers().size() == chosenCount) {
					int half = team.getUsers().size() / 2;
					while (users1.size() < half) {
						Optional<? extends WBUser> o = team.getUsers().stream().findAny();
						if (o.isPresent()) {
							WBUser user = o.get();
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
						for (WBUser user : users1) {
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
					for (WBUser user : WBUser.onlineUsers()) {
						if (user.getTeam().getType() == TeamType.SPECTATOR) {
							user.setTeam(team);
							break;
						}
					}
				}
			}
		}
	}

	public void setArmor(WBUser user) {
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

	public void setPlayerItems(WBUser user) {
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

		PlayerPerks perks = user.perksStorage();
		Set<Integer> slots = new HashSet<>();
		int required = 0;
		for (ActivationType type : ActivationType.values()) {
			for (int slot : perks.perkInvSlots(type)) {
				required++;
				slots.add(slot);
			}
		}
		if (slots.size() < required) {
			Map<ActivationType, PerkName[]> perkMap = new HashMap<>(perks.perks());
			perks.reset();
			perks.perks(perkMap);
			user.perksStorage(perks);
			user.perks().reloadFromStorage();
		}

		for (ActivationType type : ActivationType.values()) {
			for (UserPerk perk : user.perks().perks()) {
				perk.currentPerkItem().setItem();
			}
		}

		for (int i = 0; i < woolcount; i++) {
			p.getInventory().addItem(ItemBuilder.item(Material.WOOL)
					.damage(user.getTeam().getType().getWoolColorByte()).build());
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

	public void fixSpectator(WBUser user) {
		if (user.getTeam() != null)
			this.lastTeam.put(user, user.getTeam());
		Player p = user.getBukkitEntity();
		Scoreboard sb = new Scoreboard();
		p.setScoreboard(sb.getScoreboard());
		WoolBattle.initScoreboard(sb, user);
		WBUser.onlineUsers().forEach(u -> {
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
		p.spigot().setCollidesWithEntities(false);
		p.setAllowFlight(true);
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
		p.getInventory().setItem(0, Item.TELEPORT_COMPASS.getItem(user));
	}

	public void reloadScoreboardLifes(WBUser user) {
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
		sb.getTeam(team.getType().getIngameScoreboardTag()).setPrefix(
				LegacyComponentSerializer.legacySection().deserialize(
						ChatColor.translateAlternateColorCodes('&',
								"&6" + Characters.SHIFT_SHIFT_RIGHT + " &4" + Characters.HEART
										+ "&r" + llifes + "&4" + Characters.HEART + " ")));
	}

	public boolean attack(WBUser user, WBUser target, boolean ignoreGhost) {
		if (user.isTrollMode() && user.getTeam().getType() == TeamType.SPECTATOR) {
			return true;
		}
		if ((!this.isGlobalSpawnProtection && !target.hasSpawnProtection()
				&& target.getTeam() != user.getTeam() && (!ListenerGhost.isGhost(user)
				|| ignoreGhost)) || user.isTrollMode()) {
			target.setLastHit(user);
			target.setTicksAfterLastHit(0);
			return true;
		}
		return false;
	}

	public boolean attack(WBUser user, WBUser target) {
		return this.attack(user, target, false);
	}

}
