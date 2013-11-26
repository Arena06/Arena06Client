/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assemblr.arena06.client.scenes;

import com.assemblr.arena06.client.ShotMain;
import com.assemblr.arena06.client.menu.Button;
import com.assemblr.arena06.client.menu.ButtonAction;
import com.assemblr.arena06.client.menu.MenuConstants;
import com.assemblr.arena06.client.navigation.NavigationControler;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import static java.awt.image.ImageObserver.WIDTH;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author Henry
 */
public class MenuPanel extends Panel implements MouseListener {

    private final List<Button> buttons;
    
    private NavigationControler navigationControler;
    public MenuPanel(NavigationControler navigationControler1) {
        this.navigationControler = navigationControler1;
        this.buttons = new ArrayList<Button>();
        buttons.add(new Button("Go to game", new ButtonAction() {

            public void buttonPressed(MouseEvent me) {
               navigationControler.pushPanel(new GamePanel("localhost", 30155, "bob", navigationControler));
            }
        }));
        this.addMouseListener(this);
    }

    public boolean dispatchKeyEvent(KeyEvent ke) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(this, ke);
        return true;
    }

    private boolean firstPaint = true;
    private Point buttonPaneLocation;
    @Override
    protected void paintComponent(Graphics gr) {
        if (firstPaint) {
            layoutButtons(gr);
            firstPaint = false;
        }
        ((Graphics2D)gr).setBackground(Color.black);
        gr.clearRect(0, 0, this.getWidth(), this.getHeight());
        gr.drawImage(buttonPane, buttonPaneLocation.x, buttonPaneLocation.y, null);
        buttonPaneLocation = new Point((this.getWidth() - buttonPane.getWidth()) / 2, (this.getHeight() - buttonPane.getHeight()) / 2);
    }
    
    private BufferedImage buttonPane;
    private void layoutButtons(Graphics gr) {
        //Init buttons and button pane
        int standardHeight = 0, maxWidth = 0;
        for (Button button : buttons) {
            int stringWidth = gr.getFontMetrics(MenuConstants.BUTTON_TEXT_FONT).stringWidth(button.text);
            int stringHeight = (int) gr.getFontMetrics(MenuConstants.BUTTON_TEXT_FONT).getAscent();
            int buttonWidth = 2 * MenuConstants.BUTTON_BOARDER_SIDE + stringWidth;
            if (buttonWidth > maxWidth) {
                maxWidth = buttonWidth;
            }
                
            button.setWidth(buttonWidth);
            int buttonHeight = (2 * MenuConstants.BUTTON_BOARDER_TOP) + stringHeight;
            if (standardHeight == 0)
                standardHeight = buttonHeight;
            button.setHeight(buttonHeight);
        }
        int paneHeight = (buttons.size() * standardHeight) + (buttons.size() - 1) * MenuConstants.BUTTON_SPACE;
        int paneWidth = maxWidth;
        buttonPane = new BufferedImage(paneWidth, paneHeight, BufferedImage.BITMASK);
        
        //Draw buttons to pane
        refreshButtonPane();
        buttonPaneLocation = new Point((this.getWidth() - buttonPane.getWidth()) / 2, (this.getHeight() - buttonPane.getHeight()) / 2);

    }
    
    public void refreshButtonPane() {
        Graphics graphics = buttonPane.createGraphics();
        graphics.clearRect(0, 0, buttonPane.getWidth(), buttonPane.getHeight());
        graphics.setFont(MenuConstants.BUTTON_TEXT_FONT);
        int buttonStart = 0;
        for (Button b : buttons) {
            int buttonx = (buttonPane.getWidth() - b.getWidth()) / 2;
            b.setX(buttonx);
            b.setY(buttonStart);
            graphics.setColor(Color.green);
            graphics.fillRect(buttonx, buttonStart, b.getWidth(), b.getHeight());
            graphics.setColor(Color.red);
            graphics.drawString(b.text, buttonx + MenuConstants.BUTTON_BOARDER_SIDE, buttonStart - MenuConstants.BUTTON_BOARDER_TOP + b.getHeight());
            buttonStart += b.getHeight() + MenuConstants.BUTTON_SPACE;
        }
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
        for (Button b : buttons) {
            if (b.getDimensions().contains(new Point(e.getX() - buttonPaneLocation.x, e.getY() - buttonPaneLocation.y))) {
                b.getAction().buttonPressed(e);
            }
        }
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
    
    private Thread runner;
    boolean running;
    @Override
    public void enteringView() {
        
        runner = new Thread(new Runnable() {
            public void run() {
                long lastUpdate = System.currentTimeMillis();
                while (running) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                //paintImmediately(0, 0, getWidth(), getHeight());
                                repaint();
                            }
                        });
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                    
                    long elapsed = System.currentTimeMillis() - lastUpdate;
                    if (elapsed < 16) { // 60 FPS
                        try {
                            Thread.sleep(16 - elapsed);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    
                    long now = System.currentTimeMillis();
                    lastUpdate = now;
                }
            }
        });
        running = true;
        runner.start();
    }

    @Override
    public void leavingView() {
        running = false;
    }

}
