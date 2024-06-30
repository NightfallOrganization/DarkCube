/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.aetheria.util.NPCManagement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpawnUpgraderCommand implements CommandExecutor {

    private final Aetheria aetheria;

    public SpawnUpgraderCommand(Aetheria aetheria) {
        this.aetheria = aetheria;

    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        UUID uuid = UUID.randomUUID();
        String name = uuid.toString().replace("-", "").substring(0, 16);
        NPCManagement.Skin skin = new NPCManagement.Skin("eyJ0aW1lc3RhbXAiOjE1ODM5MDUwMzU1MTgsInByb2ZpbGVJZCI6Ijc1MTQ0NDgxOTFlNjQ1NDY4Yzk3MzlhNmUzOTU3YmViIiwicHJvZmlsZU5hbWUiOiJUaGFua3NNb2phbmciLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzgxN2M2ODAxNjdhNDI1MTZhMWQ4ZDhlZTQ3N2JjMTdkM2MzNTEwM2E2ZjNhOWJmOGE3ZjBmNzZmM2EwZTFkMTIifX19", "hiDSu2Rt3nB1QfVsiYMzaAxZctn0peybg01TzkdrMmBjIYmxXMG/gztvQCbAe+ZEIVRn768eXu8aLZACbLCp5pLrtZ9t/6qIjKCDIMF3ysrNX0AuIeiv9qhKV+i1LFR4UEivHpVKwLnrI7CnLsht8TjVV2PO9QFfbOFpMuQSRCR+iBQe4QrIQsAWODd/j8xwuHQ07e94aA/GcTMFkWohEVbmPBHxyiTEJIyxgP9oGQtyo1hFwxPbgOhrIZvQaWrVnHEa7V2QWEnsSvpxfTjwLf4PUWhbh5RrJMRDDZZmzOFkrYijgNBqe0aiL5c1hVLLsYRAo3emkif8BeomQnWxuIfxkbIR3odiHChs+4FgOIUosUgiS6bmJCJjrIvph19pED9/4kBBj56EAm45H6RxRQfrNKUG8EoT4NoWJkFuXYv5ijbQAQEY0Vf4Wtl4u6NpGGeIz81Ma5rjmVkANKrvrpx4puVdLr4gP2vUZC/CMcqrSy9au5RaLbFxJrD0hVUIhbtZgsjRBUoIaU4A1p2AQyCZeMd3AL8A2TIbCs4OgaaguHC7c0A43qIQvUKvQPv/I0ZdL0Rtx+ifPNYCMtV8E+0yeNZ1NtuZ1+nIu1GZpeoLRtLrCOPCTa9WLH6aXz3Evib7hYH0CguHHYo/fLnXCt5bpipBE0WOsSfYjxpkSSE=");

        aetheria.npcManagement().newNpc(player.getLocation(), skin, name, uuid);

        return false;
    }
}
