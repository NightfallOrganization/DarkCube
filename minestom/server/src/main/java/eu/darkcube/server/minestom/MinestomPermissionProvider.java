package eu.darkcube.server.minestom;

import eu.darkcube.system.minestom.command.PermissionProvider;
import net.luckperms.api.LuckPermsProvider;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.entity.Player;

public class MinestomPermissionProvider implements PermissionProvider {
    @Override
    public boolean hasPermission(CommandSender sender, String permission) {
        if (sender instanceof ConsoleSender) return true;
        if (sender instanceof Player player) {
            return LuckPermsProvider.get().getPlayerAdapter(Player.class).getPermissionData(player).checkPermission(permission).asBoolean();
        }
        return false;
    }
}
