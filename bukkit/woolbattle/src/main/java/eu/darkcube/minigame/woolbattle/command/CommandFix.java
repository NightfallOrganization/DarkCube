/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.user.WBUser;

public class CommandFix extends WBCommandExecutor {
	public CommandFix() {
		super("fix", b -> b.requires(s -> WoolBattle.getInstance().getIngame().enabled())
				.executes(ctx -> {
					WBUser user = WBUser.getUser(ctx.getSource().asPlayer());
					WoolBattle.getInstance().getIngame().setPlayerItems(user);
					return 0;
				}));
	}
	//	public CommandFix() {
	//		super(WoolBattle.getInstance(), "fix", new Command[0], "Fix");
	//	}
	//
	//	@Override
	//	public boolean execute(CommandSender sender, String[] args) {
	//		if (sender instanceof Player && WoolBattle.getInstance().getIngame().isEnabled()) {
	//			new Scheduler() {
	//				@Override
	//				public void run() {
	//					Player p = (Player) sender;
	//					WBUser user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
	//					WoolBattle.getInstance().getIngame().setPlayerItems(user);
	//				}
	//			}.runTaskLater(1);
	//		}
	//		return true;
	//	}
}
