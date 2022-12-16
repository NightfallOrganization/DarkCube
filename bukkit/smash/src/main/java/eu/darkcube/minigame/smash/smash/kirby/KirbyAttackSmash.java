/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.smash.kirby;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.smash.smash.AttackSmash;
import eu.darkcube.minigame.smash.api.user.User;
import eu.darkcube.minigame.smash.user.UserWrapper;
import eu.darkcube.minigame.smash.util.MathUtil;

public class KirbyAttackSmash extends AttackSmash {

	public KirbyAttackSmash() {
		super(5);
	}

	@Override
	protected void execute0(User user) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (MathUtil.canAttack(user.getPlayer(), p, 2.5, 1, 50)) {
				User target = UserWrapper.getUser(p);
				target.damage(1);
				p.setVelocity(user.getPlayer().getLocation().getDirection().setY(0).normalize().multiply(.2).setY(.1));
			}
		}
	}
}
