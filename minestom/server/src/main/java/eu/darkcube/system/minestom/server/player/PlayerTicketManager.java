package eu.darkcube.system.minestom.server.player;

import eu.darkcube.system.minestom.server.chunk.ChunkCache;
import eu.darkcube.system.minestom.server.chunk.ChunkManager;
import eu.darkcube.system.minestom.server.util.BinaryOperations;
import eu.darkcube.system.minestom.server.util.MathHelper;
import eu.darkcube.system.minestom.server.util.PriorityCalculator;
import net.minestom.server.instance.Chunk;
import net.minestom.server.network.packet.server.play.UnloadChunkPacket;

import java.util.concurrent.CompletableFuture;

public class PlayerTicketManager {

    private final ChunkManager<?> chunkManager;
    private final PriorityCalculator priorityCalculator;
    private final DarkCubePlayer player;
    private ChunkCache.Ticket[] tickets;
    private ChunkCache.Ticket[] oldTickets;
    private CompletableFuture<Chunk>[] futures;
    private int[] offsets;
    private float radius = -1;
    private boolean unloaded = true;
    private int x = 0;
    private int y = 0;

    public PlayerTicketManager(ChunkManager<?> chunkManager, PriorityCalculator priorityCalculator, float radius, DarkCubePlayer player) {
        this.chunkManager = chunkManager;
        this.priorityCalculator = priorityCalculator;
        this.player = player;
        resize(radius);
    }

    public ChunkManager<?> chunkManager() {
        return chunkManager;
    }

    public void resize(float radius) {
        synchronized (this) {
            if (this.radius == radius) return;
            var tickets0 = tickets;
            var offsets0 = offsets;
            this.radius = radius;
            offsets = MathHelper.bresenheimOffsets(radius);
            tickets = new ChunkCache.Ticket[offsets.length];
            oldTickets = new ChunkCache.Ticket[offsets.length];
            futures = new CompletableFuture[offsets.length];
            if (!unloaded) {
                unloaded = true;
                move(x, y);
                unload(offsets0, tickets0);
            }
        }
    }

    public void unload() {
        synchronized (this) {
            if (unloaded) return;
            unloaded = true;
            unload(offsets, tickets);
        }
    }

    private void unload(int[] offsets, ChunkCache.Ticket[] tickets) {
        for (int i = 0; i < offsets.length; i++) {
            var offset = offsets[i];
            var relativeX = (int) BinaryOperations.x(offset);
            var relativeY = (int) BinaryOperations.y(offset);
            chunkManager.release(x + relativeX, y + relativeY, tickets[i]);
            tickets[i] = null;
        }
    }

    public void move(int newX, int newY) {
        synchronized (this) {
            var oldX = x;
            var oldY = y;

            int cnt = 0;

            for (int i = 0; i < offsets.length; i++) {
                var offset = offsets[i];
                var relativeX = (int) BinaryOperations.x(offset);
                var relativeY = (int) BinaryOperations.y(offset);
                var priority = priorityCalculator.priority(newX, newY, relativeX, relativeY);

                var realX = newX + relativeX;
                var realY = newY + relativeY;

                // Load the coordinate
                var result = chunkManager.require(realX, realY, priority);
                var now = (Chunk) result.future().getNow(null);
                if (now != null) {
                    var existingTickets = chunkManager.tickets(realX, realY);
                    var oldTicket = tickets[i];
                    if (oldTicket == null || !existingTickets.contains(oldTicket)) {
                        // Make sure not to send every chunk on every border crossing
                        now.sendChunk(player);
                        cnt++;
                    }
                }
                oldTickets[i] = tickets[i];
                tickets[i] = result.ticket();
            }
            System.out.println("Sent " + cnt);
            if (!unloaded) for (int i = 0; i < offsets.length; i++) {
                var offset = offsets[i];
                var relativeX = (int) BinaryOperations.x(offset);
                var relativeY = (int) BinaryOperations.y(offset);
                var realX = oldX + relativeX;
                var realY = oldY + relativeY;
                var result = chunkManager.release(realX, realY, oldTickets[i]);
                var existingTickets = chunkManager.tickets(realX, realY);
                var currentTicket = tickets[i];
                if (currentTicket == null || !existingTickets.contains(currentTicket)) {
                    player.sendPacket(new UnloadChunkPacket(realX, realY));
                }

                oldTickets[i] = null;
            }
            x = newX;
            y = newY;
            unloaded = false;
        }
    }

    public float radius() {
        synchronized (this) {
            return radius;
        }
    }

    public boolean unloaded() {
        synchronized (this) {
            return unloaded;
        }
    }

    public int x() {
        synchronized (this) {
            return x;
        }
    }

    public int y() {
        synchronized (this) {
            return y;
        }
    }
}
