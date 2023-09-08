/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server;

import eu.darkcube.system.minestom.server.instance.DataManager2D;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongLists;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minestom.server.utils.chunk.ChunkUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Test {
    public static void main(String[] args) {
        var frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocation(400, 250);

        var image = new BufferedImage(100, 100, BufferedImage.OPAQUE);
        int rgb = 0xFFFFFF;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, rgb);
            }
        }
        var icon = new ImageIcon(image.getScaledInstance(400, 400, BufferedImage.SCALE_FAST));
        var label = new JLabel(icon);
        frame.add(label);
        LongSet l = LongSets.synchronize(new LongOpenHashSet());

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
                    icon.setImage(image.getScaledInstance(400, 400, BufferedImage.SCALE_FAST));
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

        var dataManager = new DataManager2D<>(task -> {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(30));
//            return new Color(ThreadLocalRandom.current().nextInt(256), ThreadLocalRandom.current().nextInt(256), ThreadLocalRandom
//                    .current()
//                    .nextInt(256));
            return Color.BLACK;
        }, (centerX, centerY, x, y) -> {
            int distX = centerX - x;
            int distY = centerY - y;
            int distXSq = distX * distX;
            int distYSq = distY * distY;
            return Integer.MAX_VALUE - distXSq - distYSq;
        }, (x, y, data) -> {
            if (x < 0 || x >= 100 || y < 0 || y >= 100) return;
            long lo = ChunkUtils.getChunkIndex(x, y);
            if (!l.add(lo)) System.out.println("Duplicate");
            synchronized (image) {
                image.setRGB(x, y, data.getRGB());
            }
            redraw.run();
        }, (x, y, data) -> {
            if (x < 0 || x >= 100 || y < 0 || y >= 100) return;
            if (!l.remove(ChunkUtils.getChunkIndex(x, y))) System.out.println("INvalid remove");
            synchronized (image) {
                image.setRGB(x, y, 0xFFFFFF);
            }

            redraw.run();
        });

        frame.setVisible(true);

        MouseAdapter a = new MouseAdapter() {
            int oldX = 0;
            int oldY = 0;
            int oldX2 = 0;
            int oldY2 = 0;
            IntList down = new IntArrayList();

            @Override public void mousePressed(MouseEvent e) {
                int x = e.getX() - (label.getWidth() - image.getWidth() * 4) / 2;
                int y = e.getY() - (label.getHeight() - image.getHeight() * 4) / 2;
                down.add(e.getButton());
                if (e.getButton() == MouseEvent.BUTTON1) {
                    dataManager.move(oldX / 4, oldY / 4, x / 4, y / 4, 20, 20);
                    oldX = x;
                    oldY = y;
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    dataManager.move(oldX2 / 4, oldY2 / 4, x / 4, y / 4, 20, 20);
                    oldX2 = x;
                    oldY2 = y;
                } else if (e.getButton() == MouseEvent.BUTTON2) {
                    dataManager.unloadAround(x / 4, y / 4, 20);
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
                        dataManager.move(oldX / 4, oldY / 4, x / 4, y / 4, 20, 20);
                        oldX = x;
                        oldY = y;
                    } else if (btn == MouseEvent.BUTTON3) {
                        dataManager.move(oldX2 / 4, oldY2 / 4, x / 4, y / 4, 20, 20);
                        oldX2 = x;
                        oldY2 = y;
                    }
                }
            }
        };
        label.addMouseListener(a);
        label.addMouseMotionListener(a);
    }
}
