package eu.darkcube.minigame.woolbattle.util;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityTracker;
import net.minecraft.server.v1_8_R3.WorldServer;

public class CustomEntityTracker extends EntityTracker {

	public CustomEntityTracker(WorldServer worldserver) {
		super(worldserver);
	}
	
	@Override
	public void track(Entity entity) {
//		if(entity instanceof EntityEnderSignal) {
//			EntityEnderSignal signal = (EntityEnderSignal)entity;
//			System.out.println("THROWN ENDER SIGNAL!!!");
//		}
		super.track(entity);
	}
}
