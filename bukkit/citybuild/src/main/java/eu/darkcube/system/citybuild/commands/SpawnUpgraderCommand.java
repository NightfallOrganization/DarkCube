/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import com.github.juliarn.npclib.api.Npc;
import com.github.juliarn.npclib.api.NpcActionController;
import com.github.juliarn.npclib.api.Platform;
import com.github.juliarn.npclib.api.Position;
import com.github.juliarn.npclib.api.event.AttackNpcEvent;
import com.github.juliarn.npclib.api.event.ShowNpcEvent;
import com.github.juliarn.npclib.api.profile.Profile;
import com.github.juliarn.npclib.api.profile.ProfileProperty;
import com.github.juliarn.npclib.api.protocol.enums.EntityAnimation;
import com.github.juliarn.npclib.api.protocol.meta.EntityMetadataFactory;
import com.github.juliarn.npclib.bukkit.BukkitPlatform;
import com.github.juliarn.npclib.bukkit.BukkitWorldAccessor;
import com.github.juliarn.npclib.bukkit.protocol.BukkitProtocolAdapter;
import com.github.juliarn.npclib.bukkit.util.BukkitPlatformUtil;
import eu.darkcube.system.citybuild.Citybuild;
import eu.darkcube.system.citybuild.util.NPCManagement;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpawnUpgraderCommand implements CommandExecutor {

    private final Citybuild citybuild;

    public SpawnUpgraderCommand(Citybuild citybuild) {
        this.citybuild = citybuild;

    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        UUID uuid = UUID.randomUUID();
        String name = uuid.toString().replace("-", "").substring(0, 16);
        NPCManagement.Skin skin = new NPCManagement.Skin("eyJ0aW1lc3RhbXAiOjE1ODM5MDUwMzU1MTgsInByb2ZpbGVJZCI6Ijc1MTQ0NDgxOTFlNjQ1NDY4Yzk3MzlhNmUzOTU3YmViIiwicHJvZmlsZU5hbWUiOiJUaGFua3NNb2phbmciLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzgxN2M2ODAxNjdhNDI1MTZhMWQ4ZDhlZTQ3N2JjMTdkM2MzNTEwM2E2ZjNhOWJmOGE3ZjBmNzZmM2EwZTFkMTIifX19", "hiDSu2Rt3nB1QfVsiYMzaAxZctn0peybg01TzkdrMmBjIYmxXMG/gztvQCbAe+ZEIVRn768eXu8aLZACbLCp5pLrtZ9t/6qIjKCDIMF3ysrNX0AuIeiv9qhKV+i1LFR4UEivHpVKwLnrI7CnLsht8TjVV2PO9QFfbOFpMuQSRCR+iBQe4QrIQsAWODd/j8xwuHQ07e94aA/GcTMFkWohEVbmPBHxyiTEJIyxgP9oGQtyo1hFwxPbgOhrIZvQaWrVnHEa7V2QWEnsSvpxfTjwLf4PUWhbh5RrJMRDDZZmzOFkrYijgNBqe0aiL5c1hVLLsYRAo3emkif8BeomQnWxuIfxkbIR3odiHChs+4FgOIUosUgiS6bmJCJjrIvph19pED9/4kBBj56EAm45H6RxRQfrNKUG8EoT4NoWJkFuXYv5ijbQAQEY0Vf4Wtl4u6NpGGeIz81Ma5rjmVkANKrvrpx4puVdLr4gP2vUZC/CMcqrSy9au5RaLbFxJrD0hVUIhbtZgsjRBUoIaU4A1p2AQyCZeMd3AL8A2TIbCs4OgaaguHC7c0A43qIQvUKvQPv/I0ZdL0Rtx+ifPNYCMtV8E+0yeNZ1NtuZ1+nIu1GZpeoLRtLrCOPCTa9WLH6aXz3Evib7hYH0CguHHYo/fLnXCt5bpipBE0WOsSfYjxpkSSE=");

        citybuild.npcManagement().newNpc(player.getLocation(), skin, name, uuid);

        return false;
    }
}
