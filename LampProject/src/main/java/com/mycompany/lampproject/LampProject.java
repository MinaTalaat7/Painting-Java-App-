package com.mycompany.lampproject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

public class LampProject {
    private static void createAndShowGUI() {
        javax.swing.JFrame frame = new javax.swing.JFrame("Lamp Project");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        Lamp lamp = new Lamp();
        frame.add(lamp);

        frame.setVisible(true);
    }

    public static class Lamp extends JPanel implements Runnable {

        private final Random random;

        private final Color staticColorFill = new Color(255, 255, 0);  // Yellow color for filling
        private final Color staticColorLine = Color.BLACK;  // Black color for lines

        public Lamp() {
            random = new Random();
            new Thread(this).start();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            // Set static color for lines
            g.setColor(staticColorLine);

            // Draw shapes with static color
            g.drawOval(50, 50, 100, 30);
            g.drawOval(40, 120, 25, 40);
            g.drawOval(75, 100, 45, 70);
            g.drawOval(130, 120, 25, 40);

            g.drawLine(50, 65, 20, 180);
            g.drawLine(150, 65, 170, 180);

            g.drawArc(20, 160, 150, 40, 0, -180);

            g.drawLine(95, 200, 85, 250);
            g.drawLine(105, 200, 115, 250);

            // Draw empty rectangle without color inside
            g.drawRect(50, 250, 100, 20);

            // Set dynamic color for oval shapes
            Color randomColorFill = getRandomColor();

            // Fill oval shapes with dynamic color
            g.setColor(randomColorFill);
            g.fillOval(50, 50, 100, 30);
            g.fillOval(40, 120, 25, 40);
            g.fillOval(75, 100, 45, 70);
            g.fillOval(130, 120, 25, 40);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    this.repaint();
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Lamp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        private Color getRandomColor() {
            return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
}
