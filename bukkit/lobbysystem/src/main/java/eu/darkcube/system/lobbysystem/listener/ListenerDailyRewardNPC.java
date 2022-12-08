/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent.Hand;
import com.github.juliarn.npc.modifier.LabyModModifier.LabyModAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import eu.darkcube.system.labymod.emotes.Emotes;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.InventoryDailyReward;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;

public class ListenerDailyRewardNPC extends BaseListener {

	@EventHandler
	public void handle(PlayerNPCInteractEvent e) {
		if (e.getHand() != Hand.MAIN_HAND) {
			return;
		}
		NPC npc = e.getNPC();
		if (npc.equals(Lobby.getInstance().getDailyRewardNpc())) {
			if (e.getUseAction() == PlayerNPCInteractEvent.EntityUseAction.ATTACK) {
				List<Emotes> emotes = new ArrayList<>(Arrays.asList(Emotes.values()));
				emotes.remove(Emotes.INFINITY_SIT);
				e.send(npc.labymod().queue(LabyModAction.EMOTE,
						emotes.get(new Random().nextInt(emotes.size())).getId()));
			} else {
				Player p = e.getPlayer();
				LobbyUser user = UserWrapper.fromUser(UserAPI.getInstance().getUser(p));
				user.setOpenInventory(new InventoryDailyReward(user.getUser()));
			}
		}
	}

}
