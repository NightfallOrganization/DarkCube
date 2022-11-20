package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockCanBuildEvent;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;

public class ListenerBlockCanBuild extends Listener<BlockCanBuildEvent> {
	@Override
	@EventHandler
	public void handle(BlockCanBuildEvent e) {
		if (e.getBlock().getType() != Material.AIR) {
			e.setBuildable(false);
			return;
		}
		if (e.getMaterial() == Material.WOOL) {
			e.setBuildable(true);
			Block block = CraftMagicNumbers.getBlock(e.getMaterial());
			AxisAlignedBB box = block.a(((CraftWorld) e.getBlock().getWorld()).getHandle(),
					new BlockPosition(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()),
					block.getBlockData());
			for (Entity ent : e.getBlock().getWorld().getEntities()) {
				AxisAlignedBB entityBox = ((CraftEntity) ent).getHandle().getBoundingBox();
				if (box.b(entityBox)) {
					if (!WoolBattle.getInstance().getTeamManager().getSpectator().contains(ent.getUniqueId())
							&& ent.getType() != EntityType.DROPPED_ITEM) {
						e.setBuildable(false);
						return;
					}
				}
			}
		}
	}
}