/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.user;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.ExtraWoolPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerks;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.WoolSubtractDirection;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.ItemStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class DefaultWBUser implements WBUser {

	private static final Key KEY_WOOL_SUBTRACT_DIRECTION =
			new Key(WoolBattle.getInstance(), "woolSubtractDirection");
	private static final PersistentDataType<WoolSubtractDirection> TYPE_WOOL_SUBTRACT_DIRECTION =
			PersistentDataTypes.enumType(WoolSubtractDirection.class);
	private static final Key KEY_HEIGHT_DISPLAY =
			new Key(WoolBattle.getInstance(), "heightDisplay");
	private static final PersistentDataType<HeightDisplay> TYPE_HEIGHT_DISPLAY =
			new PersistentDataType<HeightDisplay>() {
				@Override
				public HeightDisplay deserialize(JsonDocument doc, String key) {
					doc = doc.getDocument(key);
					return new HeightDisplay(doc.getBoolean("enabled"), doc.getInt("maxDistance"),
							doc.getChar("color"));
				}

				@Override
				public void serialize(JsonDocument doc, String key, HeightDisplay data) {
					doc.append(key, JsonDocument.newDocument().append("enabled", data.enabled)
							.append("maxDistance", data.maxDistance).append("color", data.color));
				}
			};
	private static final Key KEY_PARTICLES = new Key(WoolBattle.getInstance(), "particles");
	private static final PersistentDataType<Boolean> TYPE_PARTICLES = PersistentDataTypes.BOOLEAN;
	private static final Key KEY_PERKS = new Key(WoolBattle.getInstance(), "perks");
	private static final PersistentDataType<PerkName> TYPE_PERK_NAME =
			PersistentDataTypes.map(PersistentDataTypes.STRING, PerkName::getName, PerkName::new);
	private static final PersistentDataType<List<PerkName>> TYPE_LIST_PERK_NAME =
			PersistentDataTypes.list(TYPE_PERK_NAME);
	private static final PersistentDataType<PlayerPerks> TYPE_PERKS =
			new PersistentDataType<PlayerPerks>() {
				@Override
				public PlayerPerks deserialize(JsonDocument doc, String key) {
					doc = doc.getDocument(key);
					JsonDocument docPerks = doc.getDocument("perks");
					JsonDocument docPerkSlots = doc.getDocument("perkSlots");
					Map<ActivationType, PerkName[]> perks = new HashMap<>();
					Map<ActivationType, int[]> perkSlots = new HashMap<>();
					for (String documentKey : docPerks.keys()) {
						ActivationType type = ActivationType.valueOf(documentKey);
						PerkName[] array = TYPE_LIST_PERK_NAME.deserialize(docPerks, documentKey)
								.toArray(new PerkName[0]);
						perks.put(type, array);
					}
					for (String documentKey : docPerkSlots.keys()) {
						ActivationType type = ActivationType.valueOf(documentKey);
						int[] array = TYPE_INT_ARRAY.deserialize(docPerkSlots, documentKey);
						perkSlots.put(type, array);
					}
					return new DefaultPlayerPerks(perks, perkSlots);
				}

				@Override
				public void serialize(JsonDocument doc, String key, PlayerPerks data) {
					JsonDocument d = JsonDocument.newDocument();
					JsonDocument docPerks = new JsonDocument();
					JsonDocument docPerkSlots = new JsonDocument();
					for (ActivationType type : ActivationType.values()) {
						TYPE_LIST_PERK_NAME.serialize(docPerks, type.name(),
								Arrays.asList(data.perks(type)));
						TYPE_INT_ARRAY.serialize(docPerkSlots, type.name(),
								data.perkInvSlots(type));
					}
					d.append("perks", docPerks).append("perkSlots", docPerkSlots);
					doc.append(key, d);
				}
			};
	private static final PersistentDataType<int[]> TYPE_INT_ARRAY =
			new PersistentDataType<int[]>() {
				@Override
				public int[] deserialize(JsonDocument doc, String key) {
					byte[] bytes = doc.getBinary(key);
					IntBuffer buf = ByteBuffer.wrap(bytes).asIntBuffer();
					int len = buf.get();
					int[] ar = new int[len];
					for (int i = 0; buf.remaining() > 0; i++) {
						ar[i] = buf.get();
					}
					return ar;
				}

				@Override
				public void serialize(JsonDocument doc, String key, int[] data) {
					ByteBuffer buf = ByteBuffer.wrap(new byte[data.length * Integer.BYTES]);
					IntBuffer ib = buf.asIntBuffer();
					ib.put(data.length);
					for (int i : data)
						ib.put(i);
					doc.append(key, buf.array());
				}
			};
	private final User user;
	private boolean trollmode;
	private int spawnProtectionTicks;
	private IInventory openInventory;
	private int kills;
	private int deaths;
	private WBUser lastHit;
	private int ticksAfterLastHit;
	private UserPerks perks;

	public DefaultWBUser(User user) {
		this.user = user;
		user.getPersistentDataStorage().setIfNotPresent(KEY_HEIGHT_DISPLAY, TYPE_HEIGHT_DISPLAY,
				HeightDisplay.getDefault());
		user.getPersistentDataStorage().setIfNotPresent(KEY_PARTICLES, TYPE_PARTICLES, true);
		user.getPersistentDataStorage()
				.setIfNotPresent(KEY_PERKS, TYPE_PERKS, new DefaultPlayerPerks());
		user.getPersistentDataStorage()
				.setIfNotPresent(KEY_WOOL_SUBTRACT_DIRECTION, TYPE_WOOL_SUBTRACT_DIRECTION,
						WoolSubtractDirection.getDefault());
		spawnProtectionTicks = 0;
		trollmode = false;
		kills = 0;
		deaths = 0;
		perks = new UserPerks(this);
	}

	@Override
	public User user() {
		return user;
	}

	@Override
	public UUID getUniqueId() {
		return user.getUniqueId();
	}

	@Override
	public String getPlayerName() {
		return user.getName();
	}

	@Override
	public Component getTeamPlayerName() {
		return getTeam().getName(user);
	}

	@Override
	public Language getLanguage() {
		return user.getLanguage();
	}

	@Override
	public void setLanguage(Language language) {
		user.setLanguage(language);
	}

	@Override
	public Team getTeam() {
		return WoolBattle.getInstance().getTeamManager().getTeam(this);
	}

	@Override
	public void setTeam(Team team) {
		WoolBattle.getInstance().getTeamManager().setTeam(this, team);
	}

	@Override
	public UserPerks perks() {
		return perks;
	}

	@Override
	public PlayerPerks perksStorage() {
		return user.getPersistentDataStorage().get(KEY_PERKS, TYPE_PERKS).clone();
	}

	@Override
	public void perksStorage(PlayerPerks perks) {
		user.getPersistentDataStorage().set(KEY_PERKS, TYPE_PERKS, perks.clone());
	}

	@Override
	public boolean particles() {
		return user.getPersistentDataStorage().get(KEY_PARTICLES, TYPE_PARTICLES, () -> true);
	}

	@Override
	public void particles(boolean particles) {
		user.getPersistentDataStorage().set(KEY_PARTICLES, TYPE_PARTICLES, particles);
	}

	@Override
	public HeightDisplay heightDisplay() {
		return user.getPersistentDataStorage()
				.get(KEY_HEIGHT_DISPLAY, TYPE_HEIGHT_DISPLAY, HeightDisplay::getDefault).clone();
	}

	@Override
	public void heightDisplay(HeightDisplay heightDisplay) {
		user.getPersistentDataStorage()
				.set(KEY_HEIGHT_DISPLAY, TYPE_HEIGHT_DISPLAY, heightDisplay.clone());
	}

	@Override
	public WoolSubtractDirection woolSubtractDirection() {
		return user.getPersistentDataStorage()
				.get(KEY_WOOL_SUBTRACT_DIRECTION, TYPE_WOOL_SUBTRACT_DIRECTION,
						WoolSubtractDirection::getDefault);
	}

	@Override
	public void woolSubtractDirection(WoolSubtractDirection woolSubtractDirection) {
		user.getPersistentDataStorage()
				.set(KEY_WOOL_SUBTRACT_DIRECTION, TYPE_WOOL_SUBTRACT_DIRECTION,
						woolSubtractDirection);
	}

	@Override
	public int getMaxWoolSize() {
		return 64 * 3 * (1 + perkCount(ExtraWoolPerk.EXTRA_WOOL));
	}

	@Override
	public int getWoolBreakAmount() {
		return 2 * (1 + perkCount(ExtraWoolPerk.EXTRA_WOOL));
	}

	@Override
	public int getSpawnProtectionTicks() {
		return spawnProtectionTicks;
	}

	@Override
	public void setSpawnProtectionTicks(int ticks) {
		this.spawnProtectionTicks = ticks;
		getBukkitEntity().setExp(
				(float) getSpawnProtectionTicks() / (float) Ingame.SPAWNPROTECTION_TICKS);
	}

	@Override
	public boolean hasSpawnProtection() {
		return getSpawnProtectionTicks() > 0;
	}

	@Override
	public boolean isTrollMode() {
		return trollmode;
	}

	@Override
	public void setTrollMode(boolean trollmode) {
		this.trollmode = trollmode;
		WoolBattle.getInstance()
				.sendMessage("§aTrollmode " + (trollmode ? "§aAn" : "§cAus"), getBukkitEntity());
	}

	@Override
	public CraftPlayer getBukkitEntity() {
		return (CraftPlayer) user.asPlayer();
	}

	@Override
	public IInventory getOpenInventory() {
		return openInventory;
	}

	@Override
	public void setOpenInventory(IInventory id) {
		this.openInventory = id;
	}

	@Override
	public ItemStack getSingleWoolItem() {
		return new ItemStack(Material.WOOL, 1, getTeam().getType().getWoolColorByte());
	}

	@Override
	public WBUser getLastHit() {
		return lastHit;
	}

	@Override
	public void setLastHit(WBUser user) {
		this.lastHit = user;
	}

	@Override
	public int getTicksAfterLastHit() {
		return ticksAfterLastHit;
	}

	@Override
	public void setTicksAfterLastHit(int ticks) {
		ticksAfterLastHit = ticks;
	}

	@Override
	public int getKills() {
		return kills;
	}

	@Override
	public void setKills(int kills) {
		this.kills = kills;
		if (getBukkitEntity() != null) {
			getBukkitEntity().setLevel(kills);
		}
	}

	@Override
	public int getDeaths() {
		return deaths;
	}

	@Override
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	@Override
	public double getKD() {
		return getDeaths() != 0 ? (double) getKills() / (double) getDeaths() : getKills();
	}

	private int perkCount(@SuppressWarnings("SameParameterValue") PerkName perk) {
		int c = 0;
		for (UserPerk p : perks.perks())
			if (p.perk().perkName().equals(perk))
				c++;
		return c;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof WBUser && getUniqueId().equals(((WBUser) obj).getUniqueId());
	}

	@Override
	public String toString() {
		return getPlayerName();
	}
}
