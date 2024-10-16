package building.oneblock.npc;

import building.oneblock.manager.NPCManager;
import com.github.juliarn.npclib.api.Npc;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class NPC {
    private final NPCManager npcManager;
    private final Npc<World, Player, ItemStack, Plugin> npc;

    public NPC(NPCManager npcManager, Npc<World, Player, ItemStack, Plugin> npc) {
        this.npcManager = npcManager;
        this.npc = npc;
    }

    public void destroy() {
        npc.unlink();
    }

}
