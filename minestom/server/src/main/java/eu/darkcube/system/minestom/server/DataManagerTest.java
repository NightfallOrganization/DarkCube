package eu.darkcube.system.minestom.server;

import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.server.chunk.ChunkManager;
import eu.darkcube.system.minestom.server.chunk.ChunkViewer;
import eu.darkcube.system.minestom.server.player.PlayerTicketManager;
import eu.darkcube.system.minestom.server.util.BinaryOperations;
import eu.darkcube.system.minestom.server.util.PriorityCalculator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class DataManagerTest {
    public static void main(String[] args) {
        test1();
    }

    public static void test1() {
        var playerTicketManagers = new PlayerTicketManager[3];
        var viewers = new ChunkViewer[playerTicketManagers.length];

        PriorityCalculator priorityCalculator = (centerX, centerY, x, y) -> {
            int distX = centerX - x;
            int distY = centerY - y;
            int distXSq = distX * distX;
            int distYSq = distY * distY;
            return Math.max(0, Math.min(31, (int) Math.sqrt(distXSq + distYSq)));
        };
        var dataManager = new ChunkManager<>(new ChunkManager.Callbacks<>() {
            @Override public void loaded(int chunkX, int chunkY) {

            }

            @Override public void unloaded(int chunkX, int chunkY) {
                for (var viewer : viewers) {
                    viewer.unloadChunk(chunkX, chunkY);
                }
            }

            @Override public void generated(int chunkX, int chunkY, Color data) {
                for (var viewer : viewers) {
                    viewer.loadChunk(chunkX, chunkY, data);
                }
            }
        }, 32, task -> {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(ThreadLocalRandom.current().nextInt(50)));
            return randomColor();
        });

        var combinedData = viewer(null);

        for (int i = 0; i < playerTicketManagers.length; i++) {
            var data = viewer(combinedData.viewer());
            viewers[i] = data.viewer();
            playerTicketManagers[i] = new PlayerTicketManager<>(dataManager, priorityCalculator, 30, data.viewer());
            addListeners(data.label(), playerTicketManagers[i], data.image());
        }

    }

    private static Color randomColor() {
        return Color.getHSBColor((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    private static void addListeners(JLabel label, PlayerTicketManager<Color> ticketManager, BufferedImage image) {
        MouseAdapter a = new MouseAdapter() {
            IntList down = new IntArrayList();

            @Override public void mousePressed(MouseEvent e) {
                int x = e.getX() - (label.getWidth() - image.getWidth() * 4) / 2;
                int y = e.getY() - (label.getHeight() - image.getHeight() * 4) / 2;
                down.add(e.getButton());
                if (e.getButton() == MouseEvent.BUTTON1) {
                    ticketManager.move(x / 4, y / 4);
                } else if (e.getButton() == MouseEvent.BUTTON2) {
                    ticketManager.unload();
                }
            }

            @Override public void mouseReleased(MouseEvent e) {
                down.rem(e.getButton());
            }

            @Override public void mouseDragged(MouseEvent e) {
                int x = e.getX() - (label.getWidth() - image.getWidth() * 4) / 2;
                int y = e.getY() - (label.getHeight() - image.getHeight() * 4) / 2;
                for (int btn : down) {
                    if (btn == MouseEvent.BUTTON1) {
                        ticketManager.move(x / 4, y / 4);
                    }
                }
            }
        };
        label.addMouseListener(a);
        label.addMouseMotionListener(a);
    }

    private static Data viewer(@Nullable ChunkViewer<Color> combined) {
        LongSet l = LongSets.synchronize(new LongOpenHashSet());
        var frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocation(400, 250);
        var image = new BufferedImage(200, 200, BufferedImage.OPAQUE);
        int rgb = 0xFFFFFF;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, rgb);
            }
        }
        var icon = new ImageIcon(image.getScaledInstance(800, 800, BufferedImage.SCALE_FAST));
        var label = new JLabel(icon);
        frame.add(label);

        Runnable redraw = new Runnable() {
            private volatile boolean redrawing = false;
            private volatile boolean again = false;

            @Override public void run() {
                if (redrawing) {
                    again = true;
                    return;
                }
                redrawing = true;
                work();
            }

            private void work() {
                SwingUtilities.invokeLater(this::work0);
            }

            private void work0() {
                synchronized (image) {
                    icon.setImage(image.getScaledInstance(800, 800, BufferedImage.SCALE_FAST));
                }
                frame.repaint();
                if (again) {
                    again = false;
                    work0();

                    return;
                }
                redrawing = false;
            }
        };

        var viewer = new ChunkViewer<Color>() {
            @Override public void loadChunk(int x, int y, Color chunk) {
                if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) return;
                long lo = BinaryOperations.combine(x, y);
                if (!l.add(lo)) System.out.println("Duplicate");
                if (combined != null) combined.loadChunk(x, y, chunk);
                synchronized (image) {
                    image.setRGB(x, y, chunk.getRGB());
                }
                redraw.run();
            }

            @Override public void unloadChunk(int x, int y) {
                if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) return;
                if (!l.remove(BinaryOperations.combine(x, y))) System.out.println("INvalid remove");
                if (combined != null) combined.unloadChunk(x, y);
                synchronized (image) {
                    image.setRGB(x, y, 0xFFFFFF);
                }

                redraw.run();
            }
        };

        frame.setVisible(true);

        return new Data(viewer, frame, label, image);
    }

    private record Data(ChunkViewer<Color> viewer, JFrame frame, JLabel label, BufferedImage image) {
    }
}
