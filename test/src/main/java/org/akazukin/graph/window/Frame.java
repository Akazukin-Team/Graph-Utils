package org.akazukin.graph.window;

import lombok.Getter;

import javax.swing.JFrame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class Frame extends JFrame {
    @Getter
    Panel panel;

    public Frame() {
        this.setSize(1200, 900);
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
}
