/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util;

import java.util.*;
import java.util.function.*;
import java.util.regex.*;

import de.dytanic.cloudnet.common.document.gson.*;
import de.dytanic.cloudnet.driver.channel.*;
import eu.darkcube.system.cloudban.util.ban.*;
import eu.darkcube.system.cloudban.util.communication.*;

public final class Punisher {

	public static final String PERMISSION_IGNORE_BAN = "darkcube.bansystem.ignore.ban";
	public static final String PERMISSION_IGNORE_MUTE = "darkcube.bansystem.ignore.mute";

	public static final PunishmentData punish(UUID uuid, String playerUUIDName, Reason reason, UUID bannedBy,
			String playerBannedByName, Server server) {
		int countNew = (int) BanUtil.getUserHistory(uuid).getBans().stream().map(Ban::getReason)
				.filter(r -> r.equals(reason)).count() + 1;
		BanDuration duration = getDuration(reason, countNew);
		Ban ban = new Ban(uuid, DateTime.current(), new Duration(duration.getDurationInSeconds()), server,
				duration.getType(), reason, bannedBy);
		BanType type = ban.getBanType();
		Util.log("Spieler " + playerUUIDName + " wurde von " + playerBannedByName + " für " + reason.getKey() + " "
				+ ban.getDuration().endingIn(ban.getTimeBanned()) + (type == BanType.MUTE ? "gemuted" : "gebannt")
				+ "!");
		boolean hist = addToHistory(uuid, ban);
		boolean info = addToInformation(uuid, ban);
		updateUser(ban.getUUID());
		return new PunishmentData(ban, countNew, hist, info);
	}

	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('&') + "[0-9A-FK-OR]");

	public static String stripColor(String input) {
		if (input == null) {
			return null;
		}
		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

	public static final PunishmentData pardon(Ban ban) {
		int countNew = (int) BanUtil.getUserHistory(ban.getUUID()).getBans().stream().map(Ban::getReason)
				.filter(r -> r.equals(ban.getReason())).count() - 1;
//		Util.log("Spieler " + UUIDManager.getPlayerName(ban.getUUID()) + " wurde von " + ban.getPlayerNameBannedBy()
//				+ " entbannt!");
		BanType type = ban.getBanType();
		String duration = stripColor(ban.getDuration().toText());
		Util.log("Spieler " + UUIDManager.getPlayerName(ban.getUUID()) + " wurde von " + ban.getPlayerNameBannedBy()
				+ " für " + ban.getReason().getKey() + "(" + duration + ") "
				+ (type == BanType.MUTE ? "entmuted" : "entbannt") + "!");
		boolean hist = removeFromHistory(ban.getUUID(), ban);
		boolean info = removeFromInformation(ban.getUUID(), ban);
		updateUser(ban.getUUID());
		return new PunishmentData(ban, countNew, hist, info);
	}

	public static final boolean isAllowedToChat(UUID uuid, Server server, Consumer<Ban> consumer) {
		BanInformation information = BanUtil.getUserInformation(uuid);
		List<Ban> activeBans = new ArrayList<>();
		for (Ban ban : information.getBans()) {
			if (!ban.isActive()) {
				BanUtil.removeBanFromUserInformation(uuid, ban);
				continue;
			}
			if (ban.getBanType() == BanType.MUTE && server.equals(ban.getServer())) {
				activeBans.add(ban);
			}
		}
		if (activeBans.size() != 0) {
			if (consumer != null) {
				Collections.sort(activeBans, new Comparator<Ban>() {
					@Override
					public int compare(Ban o1, Ban o2) {
						return o1.getDuration().compareTo(o2.getDuration());
					}
				});
				consumer.accept(activeBans.get(activeBans.size() - 1));
			}
			return false;
		}
		return true;
	}

	public static final boolean isAllowedToBeOnline(UUID uuid, Server server, Consumer<Ban> consumer) {
		BanInformation information = BanUtil.getUserInformation(uuid);
		List<Ban> activeBans = new ArrayList<>();
		for (Ban ban : information.getBans()) {
			if (!ban.isActive()) {
				BanUtil.removeBanFromUserInformation(uuid, ban);
				continue;
			}
			if (ban.getBanType() == BanType.BAN && server.equals(ban.getServer())) {
				activeBans.add(ban);
			}
		}

		if (activeBans.size() != 0) {
			if (consumer != null) {
				Collections.sort(activeBans, new BanComparator());
				consumer.accept(activeBans.get(activeBans.size() - 1));
			}
			return false;
		}
		return true;
	}

	public static final boolean isAllowedToBeOnline(UUID uuid, Server server) {
		return isAllowedToBeOnline(uuid, server, null);
	}

	public static final void updateUser(UUID uuid) {
//		Messenger.freeId(Messenger
//				.sendMessage(ChannelMessage.UPDATE_USER, new JsonDocument().append("uuid", uuid.toString())).getId());
		ChannelMessage.builder().targetServices().channel(Messenger.CHANNEL)
				.json(new JsonDocument().append("uuid", uuid.toString()))
				.message(EnumChannelMessage.UPDATE_USER.getMessage()).build().send();
	}

	public static final boolean addToInformation(UUID uuid, Ban ban) {
		return BanUtil.addBanToUserInformation(uuid, ban);
	}

	public static final boolean removeFromInformation(UUID uuid, Ban ban) {
		return BanUtil.removeBanFromUserInformation(uuid, ban);
	}

	public static final boolean addToHistory(UUID uuid, Ban ban) {
		return BanUtil.addBanToUserHistory(uuid, ban);
	}

	public static final boolean removeFromHistory(UUID uuid, Ban ban) {
		return BanUtil.removeBanFromUserHistory(uuid, ban);
	}

	private static final BanDuration getDuration(Reason reason, int countBanned) {
		BanDuration duration = BanDuration.WARNING_BAN;
		if (reason.getDurations().size() != 0) {
			int i = countBanned;
			int min = reason.getDurations().keySet().stream().mapToInt(j -> j).min().orElse(0);
			BanDuration o = null;
			while (o == null && i >= min) {
				o = reason.getDurations().get(i);
				i--;
			}
			if (o != null) {
				duration = o;
			}
		}
		return duration;
	}

	public static final class PunishmentData {

		private Ban ban;
		private int timesBannedNew;
		private boolean historySuccess;
		private boolean informationSuccess;

		public PunishmentData(Ban ban, int timesBannedNew, boolean historySuccess, boolean informationSuccess) {
			this.ban = ban;
			this.timesBannedNew = timesBannedNew;
			this.historySuccess = historySuccess;
			this.informationSuccess = informationSuccess;
		}

		public boolean isHistorySuccess() {
			return historySuccess;
		}

		public boolean isInformationSuccess() {
			return informationSuccess;
		}

		public Ban getBan() {
			return ban;
		}

		public int getTimesBannedNew() {
			return timesBannedNew;
		}
	}
}
