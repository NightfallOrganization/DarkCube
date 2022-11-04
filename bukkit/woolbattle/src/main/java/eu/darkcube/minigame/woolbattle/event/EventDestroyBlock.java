package eu.darkcube.minigame.woolbattle.event;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

public class EventDestroyBlock extends BlockEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	
	public EventDestroyBlock(Block theBlock) {
		super(theBlock);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
