package org.akazukin.graph.window;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.akazukin.graph.vec.Vec2d;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Panel extends JPanel {
    private static final int GRID_SIZE = 400;
    private static final int BASE_OFFSET = -10;
    final Set<GraphWrapper> graphWrappers = new HashSet<>();
    final Random rdm = new Random();
    final Map<GraphWrapper, Color[]> colors = new HashMap<>();
    @Setter
    private double graphBaseX;
    @Setter
    private double graphBaseY;
    @Setter
    private double graphUnitSize = 1;
    @Setter
    private double graphPointUnit = 0.01;
    @Setter
    private double graphMargin = 0.5;

    public Panel() {
        this.setBackground(Color.WHITE);
    }

    public void addGraph(final GraphWrapper graph) {
        this.graphWrappers.add(graph);
        this.colors.put(graph, new Color[]{
                new Color(this.rdm.nextInt(255), this.rdm.nextInt(255), this.rdm.nextInt(255)),
                new Color(this.rdm.nextInt(255), this.rdm.nextInt(255), this.rdm.nextInt(255))
        });
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        this.drawGrids(g);

        g.setColor(Color.BLUE);


        final double width = this.graphUnitSize + (this.graphMargin * 2);
        final int widthPoints = (int) (width / this.graphPointUnit);
        for (final GraphWrapper graph : this.graphWrappers) {
            g.setColor(this.colors.get(graph)[0]);
            for (int i = 0; i < widthPoints; i++) {
                g.drawRect(this.getGraphXPos(this.graphBaseX - this.graphMargin + (i * this.graphPointUnit)),
                        this.getGraphYPos(graph.getValue(this.graphBaseX - this.graphMargin + (i * this.graphPointUnit))),
                        1, 1);
            }

            g.setColor(this.colors.get(graph)[1]);
            for (final Vec2d p : graph.getControlPoints()) {
                g.drawRect(this.getGraphXPos(p.getX()) - 2, this.getGraphYPos(p.getY()) - 2, 4, 4);
            }
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
            g.drawLine(this.getGraphXPos(this.graphBaseY + (this.graphUnitSize / 2)), 0,
                    this.getGraphXPos(this.graphBaseY + (this.graphUnitSize / 2)), this.getSize().height);
            // Draw horizontal lines
            g.drawLine(0, this.getGraphYPos(this.graphBaseY + (this.graphUnitSize / 2)),
                    this.getSize().width, this.getGraphYPos(this.graphBaseY + (this.graphUnitSize / 2)));
        }
        {
            g.setColor(new Color(255, 0, 0));

            // Draw vertical lines (Base)
            g.drawLine(this.getGraphXPos(this.graphBaseX), 0
                    , this.getGraphXPos(this.graphBaseX), this.getSize().height);
            // Draw horizontal lines (Base)
            g.drawLine(0, this.getGraphYPos(this.graphBaseY),
                    this.getSize().width, this.getGraphYPos(this.graphBaseY));


            g.setColor(new Color(255, 125, 75));

            // Draw vertical lines (Offset)
            g.drawLine(this.getGraphXPos(this.graphBaseX + this.graphUnitSize),
                    0, this.getGraphXPos(this.graphBaseX + this.graphUnitSize), this.getSize().height);
            // Draw horizontal lines (Offset)
            g.drawLine(0, this.getGraphYPos(this.graphBaseY + this.graphUnitSize),
                    this.getSize().width, this.getGraphYPos(this.graphBaseY + this.graphUnitSize));
        }
    }

    private void drawGrid(final Graphics g, final int amount) {
        final double size = this.graphUnitSize / amount;
        for (int i = 0; ; i++) {
            boolean drewVerticalPlus = false;
            boolean drewVerticalMinus = false;
            boolean drewHorizontalPlus = false;
            boolean drewHorizontalMinus = false;

            {
                final double yCenter = this.graphBaseY + (this.graphMargin / 2);
                final int minusY = this.getGraphYPos(yCenter - (size * i));
                if (0 <= minusY && minusY <= this.getSize().height) {
                    drewHorizontalMinus = true;
                    // Draw horizontal lines (-)
                    g.drawLine(0, minusY, this.getSize().width, minusY);
                }
                final int plusY = this.getGraphYPos(yCenter + (size * i));
                if (0 <= plusY && plusY <= this.getSize().height) {
                    drewHorizontalPlus = true;
                    // Draw horizontal lines (+)
                    g.drawLine(0, plusY, this.getSize().width, plusY);
                }
            }
            {
                final double xCenter = this.graphBaseX + (this.graphMargin / 2);
                final int minusX = this.getGraphXPos(xCenter - (size * i));
                if (0 <= minusX && minusX <= this.getSize().width) {
                    drewVerticalMinus = true;
                    // Draw vertical lines (-)
                    g.drawLine(minusX, 0, minusX, this.getSize().height);
                }
                final int plusX = this.getGraphXPos(xCenter + (size * i));
                if (0 <= plusX && plusX <= this.getSize().width) {
                    drewVerticalPlus = true;
                    // Draw vertical lines (+)
                    g.drawLine(plusX, 0, plusX, this.getSize().height);
                }
            }
            if (!drewVerticalPlus && !drewVerticalMinus
                    && !drewHorizontalPlus && !drewHorizontalMinus) {
                break;
            }
        }
    }

    public int getGraphYPos(final double value) {
        return BASE_OFFSET + (int) (((value + (this.graphMargin * 1.25)) / this.graphUnitSize) * GRID_SIZE);
    }

    public int getGraphXPos(final double value) {
        return BASE_OFFSET + (int) (((value + (this.graphMargin * 1.25)) / this.graphUnitSize) * GRID_SIZE);
    }
}
