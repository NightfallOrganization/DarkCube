package eu.darkcube.system.minestom.server.player;

import eu.darkcube.system.minestom.server.chunk.ChunkCache;
import eu.darkcube.system.minestom.server.chunk.ChunkManager;
import eu.darkcube.system.minestom.server.chunk.ChunkViewer;
import eu.darkcube.system.minestom.server.util.BinaryOperations;
import eu.darkcube.system.minestom.server.util.MathHelper;
import eu.darkcube.system.minestom.server.util.PriorityCalculator;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PlayerTicketManager<T> {

    private final ChunkManager<T> chunkManager;
    private final PriorityCalculator priorityCalculator;
    private final ChunkViewer<T> viewer;
    private ChunkCache.Ticket[] tickets;
    private ChunkCache.Ticket[] tempTickets;
    private int[][] ticketsIndexes;
    private ChunkCache.Ticket[] oldTickets;
    private Consumer<T>[] listeners;
    private int[] offsets;
    private int radius = -1;
    private boolean unloaded = true;
    private int x = 0;
    private int y = 0;

    public PlayerTicketManager(ChunkManager<T> chunkManager, PriorityCalculator priorityCalculator, int radius, ChunkViewer<T> viewer) {
        this.chunkManager = chunkManager;
        this.priorityCalculator = priorityCalculator;
        this.viewer = viewer;
        resize(radius);
    }

    public ChunkManager<?> chunkManager() {
        return chunkManager;
    }

    public void resize(int radius) {
        synchronized (this) {
            if (this.radius == radius) return;
            var tickets0 = tickets;
            var offsets0 = offsets;
            this.radius = radius;
            offsets = MathHelper.bresenheimOffsets(radius);
            tickets = new ChunkCache.Ticket[offsets.length];
            tempTickets = new ChunkCache.Ticket[offsets.length];
            oldTickets = new ChunkCache.Ticket[offsets.length];
            listeners = new Consumer[offsets.length];
            ticketsIndexes = new int[radius * 2 + 1][radius * 2 + 1];
            for (var a : ticketsIndexes) Arrays.fill(a, -1);
            for (var i = 0; i < offsets.length; i++) {
                var offset = offsets[i];
                var relativeX = (int) BinaryOperations.x(offset);
                var relativeY = (int) BinaryOperations.y(offset);
                ticketsIndexes[relativeY + radius][relativeX + radius] = i;
            }
            for (int[] ticketsIndex : ticketsIndexes) {
                System.out.println(Arrays.toString(ticketsIndex));
            }

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
            if ((oldX == newX && oldY == newY) && !unloaded) return;

            int cnt = 0;
            int skipped = 0;
            System.out.println("1: " + -(oldX - newX) + " " + (newY - oldY));

            for (var i = 0; i < offsets.length; i++) {
                var offset = offsets[i];
                var relativeX = (int) BinaryOperations.x(offset);
                var relativeY = (int) BinaryOperations.y(offset);
                var priority = priorityCalculator.priority(newX, newY, relativeX, relativeY);

                var realX = newX + relativeX;
                var realY = newY + relativeY;

                // Load the coordinate
                var result = chunkManager.require(realX, realY, priority);
                var now = result.future().getNow();
                if (now != null) {
                    var existingTickets = chunkManager.tickets(realX, realY);
                    var oldIndex = ticketsIndex(relativeX + radius - newX + oldX, relativeY + radius - newY + oldY);
                    var newChunk = oldIndex == -1;
                    if (!newChunk) {
                        System.out.println("2: " + -(BinaryOperations.x(offsets[oldIndex]) - relativeX) * -(BinaryOperations.y(offsets[oldIndex]) - relativeY) + "  " + i + "  " + oldIndex);
                        var oldTicket = tempTickets[oldIndex];
                        if (oldTicket == null) {
                            if (cnt == 0) System.out.println(existingTickets);
                            newChunk = true;
                        }
                    }
                    if (newChunk) {
                        // Make sure not to send every chunk on every border crossing
                        viewer.loadChunk(realX, realY, now);
                        cnt++;
                    } else {
                        skipped++;
                    }
                } else {
                    listeners[i] = (T chunk) -> {

                    };
                    result.future().then(listeners[i]);
                }
                oldTickets[i] = tickets[i];
                tickets[i] = result.ticket();
            }
            System.arraycopy(tickets, 0, tempTickets, 0, tickets.length);
            System.out.println("Sent " + cnt);
            System.out.println("Skipped: " + skipped);
            if (!unloaded) for (var i = 0; i < offsets.length; i++) {
                var offset = offsets[i];
                var relativeX = (int) BinaryOperations.x(offset);
                var relativeY = (int) BinaryOperations.y(offset);
                var realX = oldX + relativeX;
                var realY = oldY + relativeY;
                var result = chunkManager.release(realX, realY, oldTickets[i]);
                var existingTickets = chunkManager.tickets(realX, realY);
                var currentTicket = tickets[i];
                if (currentTicket == null || !existingTickets.contains(currentTicket)) {
//                    viewer.unloadChunk(realX, realY);
                }

                oldTickets[i] = null;
            }
            x = newX;
            y = newY;
            unloaded = false;
        }
    }

    private int ticketsIndex(int x, int y) {
        if (x < 0 || y < 0) return -1;
        if (y >= ticketsIndexes.length) return -1;
        var row = ticketsIndexes[y];
        if (x >= row.length) return -1;
        return row[x];
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
