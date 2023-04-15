/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;

public class CommandTimer extends WBCommandExecutor {

	public CommandTimer() {
		//		super(WoolBattle.getInstance(), "timer", new Command[0], "Setze den Timer",
		//						CommandArgument.TIMER);
		super("timer", b -> b.then(
				Commands.argument("time", IntegerArgumentType.integer(0)).executes(ctx -> {
					int time = IntegerArgumentType.getInteger(ctx, "time");
					WoolBattle.instance().getLobby().setOverrideTimer(time);
					ctx.getSource().sendMessage(Message.TIMER_CHANGED, time);
					return 0;
				})));
	}

	//	@Override
	//	public boolean execute(CommandSender sender, String[] args) {
	//		if (args.length == 1) {
	//			WBUser user = null;
	//			if (sender instanceof Player)
	//				user = WoolBattle.getInstance().getUserWrapper()
	//						.getUser(((Player) sender).getUniqueId());
	//			Integer timer = null;
	//			try {
	//				timer = Integer.valueOf(args[0]);
	//				if (timer < 0)
	//					timer = null;
	//			} catch (Exception ignored) {
	//			}
	//			if (timer == null) {
	//				if (user != null)
	//					sender.sendMessage(Message.ENTER_POSITIVE_NUMBER.getMessage(user));
	//				else
	//					sender.sendMessage(Message.ENTER_POSITIVE_NUMBER.getServerMessage());
	//				return true;
	//			}
	//			WoolBattle.getInstance().getLobby().setOverrideTimer(timer <= 1 ? 2 : timer * 20);
	//			if (user != null)
	//				sender.sendMessage(Message.TIMER_CHANGED.getMessage(user, timer.toString()));
	//			else
	//				sender.sendMessage(Message.TIMER_CHANGED.getServerMessage(timer.toString()));
	//			return true;
	//		}
	//		return false;
	//	}
}
