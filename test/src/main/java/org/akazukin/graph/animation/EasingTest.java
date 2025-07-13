package org.akazukin.graph.animation;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class EasingTest {
    public static class Frame extends JFrame {
        Panel panel;

        public Frame() {
            this.setSize(800, 600);
            this.panel = new Panel();
            this.add(this.panel);

            this.addComponentListener(new ComponentListener() {
                @Override
                public void componentResized(final ComponentEvent e) {
                    Frame.this.panel.setSize(e.getComponent().getSize());
                }

                @Override
                public void componentMoved(final ComponentEvent e) {
                }

                @Override
                public void componentShown(final ComponentEvent e) {
                }

                @Override
                public void componentHidden(final ComponentEvent e) {
                }
            });
        }

        public Panel getPanel() {
            return this.panel;
        }
    }

    public static class Panel extends JPanel {
        private static final int GRID_SIZE = 400;
        private static final int UNIT_SIZE = 1;
        private static final int BASE_OFFSET = -10;
        private static final int OFFSET_X = 100;
        private static final int OFFSET_Y = 100;

        IEasing easing = timePercent -> 0;

        public Panel() {
            this.setBackground(Color.WHITE);
        }

        public void setEasing(final IEasing easing) {
            this.easing = easing;
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            this.drawGrids(g);

            g.setColor(Color.BLUE);

            System.out.println(this.getGraphYPos(0));

            for (int i = 0, points = GRID_SIZE / 5;
                 i <= points; i++) {
                g.drawRect(this.getGraphXPos((double) i / points), this.getGraphYPos(this.easing.getProgress((double) i / points)), 1, 1);
            }
        }

        private void drawGrids(final Graphics g) {
            // Draw Grids
            {
                g.setColor(new Color(150, 150, 150, 255));
                this.drawGrid(g, 20);
            }
            {
                g.setColor(new Color(100, 100, 100, 255));
                this.drawGrid(g, 10);
            }
            {
                g.setColor(new Color(50, 50, 50, 255));

                // Draw vertical lines
                g.drawLine(this.getGraphXPos(0.5), 0, this.getGraphXPos(0.5), this.getSize().height);
                // Draw horizontal lines
                g.drawLine(0, this.getGraphYPos(0.5), this.getSize().width, this.getGraphYPos(0.5));
            }
            {
                g.setColor(new Color(255, 0, 0));

                // Draw vertical lines (Base)
                g.drawLine(this.getGraphXPos(0), 0, this.getGraphXPos(0), this.getSize().height);
                // Draw horizontal lines (Base)
                g.drawLine(0, this.getGraphYPos(0), this.getSize().width, this.getGraphYPos(0));


                g.setColor(new Color(255, 125, 75));

                // Draw vertical lines (Offset)
                g.drawLine(this.getGraphXPos(1), 0, this.getGraphXPos(1), this.getSize().height);
                // Draw horizontal lines (Offset)
                g.drawLine(0, this.getGraphYPos(1), this.getSize().width, this.getGraphYPos(1));
            }
        }

        private void drawGrid(final Graphics g, final int amount) {
            for (int i = 0; ; i++) {
                boolean drewVertical = false;
                boolean drawHorizontal = false;
                if (this.getSize().width >= (((double) GRID_SIZE / amount) * i) - OFFSET_X) {
                    drewVertical = true;
                    // Draw vertical lines
                    g.drawLine(this.getGraphXPos((double) i / amount) - OFFSET_X, 0, this.getGraphXPos((double) i / amount) - OFFSET_X, this.getSize().height);
                }
                if (this.getSize().height >= (((double) GRID_SIZE / amount) * i) - OFFSET_Y) {
                    drawHorizontal = true;
                    // Draw horizontal lines
                    g.drawLine(0, this.getGraphYPos((double) i / amount) - OFFSET_Y, this.getSize().width, this.getGraphYPos((double) i / amount) - OFFSET_Y);
                }
                if (!drewVertical && !drawHorizontal) {
                    break;
                }
            }
        }

        public int getGraphYPos(final double value) {
            return BASE_OFFSET + OFFSET_Y + (int) ((value * GRID_SIZE) / UNIT_SIZE);
        }

        public int getGraphXPos(final double value) {
            return BASE_OFFSET + OFFSET_X + (int) ((value * GRID_SIZE) / UNIT_SIZE);
        }
    }
}
