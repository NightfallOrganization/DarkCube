/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.math.BigInteger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;
import eu.darkcube.system.darkessentials.util.Duration;
import eu.darkcube.system.darkessentials.util.DurationDeserializer;

public class CommandStop extends Command {

	private long stopAt = Long.MAX_VALUE;
	private static final String TITLE = Main.cValue() + " " + Main.cConfirm();
	private static final String SUBTITLE = Main.cConfirm() + "Der Server stoppt"
					+ Main.cValue() + " %s";
	private BukkitRunnable runnable;

	public CommandStop() {
		super(Main.getInstance(), "stop", new Command[0],
						"Stoppt den Server nach einer bestimmten Zeit",
						new Argument("Zeit",
										"Zeit, nach der der Server gestoppt wird",
										false));
		setAliases("d_stop");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if ((sender instanceof Player || sender instanceof ConsoleCommandSender)
						&& (args.length == 1 || (args.length == 0
										&& runnable != null))) {
			if (args.length == 0) {
				runnable.cancel();
				Main.getInstance().sendMessage(Main.cConfirm()
								+ "Der Stopvorgang wurde abgebrochen", sender);
				Main.sendTitle(TITLE, Main.cConfirm()
								+ "Der Stopvorgang wurde abgebrochen!", 20, 100, 20);
				return true;
			}
			Duration duration = DurationDeserializer.deserialize(args[0]);
			if (duration.getDurationInSeconds().equals(BigInteger.ZERO)
							|| duration.getYear().compareTo(BigInteger.ONE) != -1) {
				if (runnable != null) {
					runnable.cancel();
					runnable = null;
					Main.getInstance().sendMessage(Main.cConfirm()
									+ "Der Stopvorgang wurde abgebrochen", sender);
					Main.sendTitle(TITLE, Main.cConfirm()
									+ "Der Stopvorgang wurde abgebrochen!", 20, 100, 20);
				} else {
					Main.getInstance().sendMessage(Main.cFail()
									+ "Bitte gib eine gültige Zeitspanne an", sender);
				}
				stopAt = Long.MAX_VALUE;
				return true;
			}
			stopAt = System.currentTimeMillis() / 1000
							+ duration.getDurationInSeconds().longValueExact();
			if (runnable != null) {
				runnable.cancel();
				Main.getInstance().sendMessage(Main.cConfirm()
								+ "Der vorherige Stopvorgang wurde abgebrochen!", sender);
			}
			runnable = new BukkitRunnable() {

				@Override
				public void run() {
					if (stopAt != Long.MAX_VALUE) {
						long current = System.currentTimeMillis() / 1000;
						long remaining = stopAt - current;
						if (remaining <= 0) {
							Bukkit.shutdown();
							return;
						}
						if (remaining == 60) {
							System.out.println("Der Server stoppt in einer Minute.");
							Main.sendTitle(TITLE, String.format(SUBTITLE, "in einer Minute"), 10, 100, 10);
						} else if (remaining == 30) {
							System.out.println("Der Server stoppt in 30 Sekunden.");
							Main.sendTitle(TITLE, String.format(SUBTITLE, "in 30 Sekunden"), 10, 100, 10);
						} else if (remaining == 1) {
							System.out.println("Der Server stoppt jetzt.");
							Main.sendTitle(TITLE, String.format(SUBTITLE, "Jetzt"), 0, 100, 0);
						} else if (remaining <= 10) {
							System.out.println("Der Server stoppt in "
											+ remaining + " Sekunden.");
							Main.sendTitle(TITLE, String.format(SUBTITLE, "in "
											+ remaining
											+ " Sekunden"), 0, 20, 0);
						}
					}
				}
			};
			runnable.runTaskTimer(Main.getInstance(), 0, 20);
			return true;
		} else if ((sender instanceof Player
						|| sender instanceof ConsoleCommandSender)
						&& args.length == 0) {
			Bukkit.shutdown();
			return true;
		}
		return false;
	}

}
