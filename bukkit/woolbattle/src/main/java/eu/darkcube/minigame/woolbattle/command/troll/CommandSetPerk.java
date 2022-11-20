package eu.darkcube.minigame.woolbattle.command.troll;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkEnderPearl;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.PerkNumber;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.system.commandapi.Command;

public class CommandSetPerk extends Command {

	public CommandSetPerk() {
		super(WoolBattle.getInstance(), "setPerk", new Command[0], "Setzt die Perks eines Spielers",
				CommandArgument.PLAYER_OPTIONAL, CommandArgument.PERK_SLOT, CommandArgument.PERK);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(WoolBattle.getInstance()
					.getUserWrapper()
					.getUsers()
					.stream()
					.filter(u -> u.getTeam().getType() != TeamType.SPECTATOR)
					.map(User::getBukkitEntity)
					.map(Player::getName)
					.collect(Collectors.toSet()), args[0]);
		} else if (args.length == 2) {
			return Arrays.toSortedStringList(Arrays.asList(PerkNumber.values())
					.stream()
					.filter(s -> s != PerkNumber.DISPLAY)
					.map(Enum::toString)
					.collect(Collectors.toSet()), args[1]);
		} else if (args.length == 3) {
			PerkNumber n = number(args[1]);
			PerkType[] ps = getPerks(n);
			return Arrays.toSortedStringList(
					(ps != null ? Arrays.asList(ps).stream().map(PerkType::getPerkName).map(PerkName::getName)
							: Arrays.asList(new String[] {
									"ENDERPEARL"
							}).stream()).collect(Collectors.toList()), args[2]);
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		User target = null;
		int i = 0;
		if (args.length == 2) {
			if (sender instanceof Player) {
				target = WoolBattle.getInstance().getUserWrapper().getUser(((Player) sender).getUniqueId());
			}
		} else if (args.length == 3) {
			i = 1;
			Player p = Bukkit.getPlayer(args[0]);
			if (p != null) {
				target = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
			}
		}
		if (target == null) {
			return false;
		}
		PerkNumber number = number(args[i]);
		if (number == null) {
			WoolBattle.getInstance().sendMessage("§cInvalid PerkSlot", sender);
			return true;
		}
		PerkType perk = null;
		PerkType[] a = getPerks(number);
		if (a != null) {
			for (PerkType t : a) {
				if (t.getPerkName().getName().equals(args[i + 1])) {
					perk = t;
					break;
				}
			}
			if (perk == null) {
				WoolBattle.getInstance().sendMessage("§cInvalid Perk", sender);
				return true;
			}
		}
		Perk rperk = perk != null ? perk.newPerkTypePerk(target, number) : new PerkEnderPearl(target);
		switch (number) {
		case ACTIVE_1:
			target.setActivePerk1(rperk);
			break;
		case ACTIVE_2:
			target.setActivePerk2(rperk);
			break;
		case PASSIVE:
			target.setPassivePerk(rperk);
			break;
		case ENDER_PEARL:
			target.setEnderPearl(rperk);
			break;
		default:
			WoolBattle.getInstance().sendMessage("§cError: " + number, sender);
			return true;
		}
		WoolBattle.getInstance().getIngame().setPlayerItems(target);
		WoolBattle.getInstance().sendMessage("§aPerk set: " + number + " | " + rperk.getPerkName().getName(), sender);
		return true;
	}

	private PerkType[] getPerks(PerkNumber number) {
		switch (number) {
		case ACTIVE_1:
		case ACTIVE_2:
			return Arrays.asList(PerkType.values())
					.stream()
					.filter(p -> !p.isPassive())
					.collect(Collectors.toList())
					.toArray(new PerkType[0]);
		case PASSIVE:
			return Arrays.asList(PerkType.values())
					.stream()
					.filter(p -> p.isPassive())
					.collect(Collectors.toList())
					.toArray(new PerkType[0]);
		case ENDER_PEARL:
			return null;
		default:
			break;
		}
		return new PerkType[0];
	}

	private PerkNumber number(String s) {
		for (PerkNumber number : PerkNumber.values()) {
			if (number != PerkNumber.DISPLAY) {
				if (s.equalsIgnoreCase(number.toString())) {
					return number;
				}
			}
		}
		return null;
	}
}
