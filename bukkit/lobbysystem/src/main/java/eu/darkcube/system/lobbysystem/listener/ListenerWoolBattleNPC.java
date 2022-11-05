package eu.darkcube.system.lobbysystem.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.modifier.LabyModModifier.LabyModAction;

import eu.darkcube.system.labymod.emotes.Emotes;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.InventoryWoolBattle;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;

public class ListenerWoolBattleNPC extends BaseListener {

	@EventHandler
	public void handle(PlayerNPCInteractEvent e) {
		NPC npc = e.getNPC();
		if (npc.equals(Lobby.getInstance().getWoolBattleNPC())) {
			if (e.getUseAction() == PlayerNPCInteractEvent.EntityUseAction.ATTACK) {
				List<Emotes> emotes = new ArrayList<>(Arrays.asList(Emotes.values()));
				emotes.remove(Emotes.INFINITY_SIT);
				e.send(npc.labymod()
						.queue(LabyModAction.EMOTE, emotes.get(new Random().nextInt(emotes.size())).getId()));
//				npc.sendEmote(emotes.get(new Random().nextInt(emotes.size())));
			} else {
				Player p = e.getPlayer();
				User user = UserWrapper.getUser(p.getUniqueId());
				user.setOpenInventory(new InventoryWoolBattle(user));
			}
		}
	}

}
