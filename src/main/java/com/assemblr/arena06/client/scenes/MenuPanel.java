/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assemblr.arena06.client.scenes;

import com.assemblr.arena06.client.menu.Button;
import com.assemblr.arena06.client.menu.ButtonAction;
import com.assemblr.arena06.client.menu.MenuConstants;
import com.assemblr.arena06.client.menu.MenuObject;
import com.assemblr.arena06.client.menu.TextField;
import com.assemblr.arena06.client.menu.TextFieldAndLabel;
import com.assemblr.arena06.client.navigation.NavigationControler;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author Henry
 */
public class MenuPanel extends Panel implements MouseListener, KeyEventDispatcher, KeyListener {

    private final List<MenuObject> menuObjects;
    
    private NavigationControler navigationControler;
    private TextFieldAndLabel ipTextField, playerNameFeild;
    public MenuPanel(NavigationControler navigationControler1) {
        this.navigationControler = navigationControler1;
        this.menuObjects = new ArrayList<MenuObject>();
        ipTextField = new TextFieldAndLabel("IP>",200, 30, 10);
        addMenuObject(ipTextField);
        playerNameFeild = new TextFieldAndLabel("Player Name>", 200, 30, 10);
        addMenuObject(playerNameFeild);
        addMenuObject(new Button("Connect", new ButtonAction() {
            public void buttonPressed(MouseEvent me) {
                String ip = ipTextField.getText();
                int port = 30155;
                if (ip.contains(":")) {
                    port = Integer.parseInt(ip.split(":")[1]);
                    ip = ip.split(":")[0];
                }
               navigationControler.pushPanel(new GamePanel(ip, port, playerNameFeild.getText(), navigationControler));
            }
        }));
        this.addMouseListener(this);
        this.addKeyListener(this);
    }

    public boolean dispatchKeyEvent(KeyEvent ke) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(this, ke);
        return true;
    }
    
    private Rectangle buttonPaneLocation;
    @Override
    protected void paintComponent(Graphics gr) {
        ((Graphics2D)gr).setBackground(Color.black);
        gr.clearRect(0, 0, this.getWidth(), this.getHeight());
        refreshButtonPane(gr);
        
        buttonPaneLocation.x = (this.getWidth() - buttonPaneLocation.width) / 2; 
        buttonPaneLocation.y = (this.getHeight() - buttonPaneLocation.height) / 2;
    }
    private void calculateButtonDimensions(Graphics gr) {
        int totalHeight = 0, maxWidth = 0;
        for (MenuObject menuObject : menuObjects) {
            if (menuObject instanceof Button) {
                Button button = (Button) menuObject;
                int stringWidth = gr.getFontMetrics(MenuConstants.BUTTON_TEXT_FONT).stringWidth(button.text);
                int stringHeight = (int) gr.getFontMetrics(MenuConstants.BUTTON_TEXT_FONT).getAscent();
                int buttonWidth = 2 * MenuConstants.BUTTON_BOARDER_SIDE + stringWidth;
                button.setWidth(buttonWidth);
                button.setHeight((2 * MenuConstants.BUTTON_BOARDER_TOP) + stringHeight);
            }
            if (menuObject instanceof TextField) {
                //TextField textField = (TextField) menuObject;
            }
            if (menuObject instanceof TextFieldAndLabel) {
                TextFieldAndLabel tfl = (TextFieldAndLabel) menuObject;
                int stringWidth = gr.getFontMetrics(MenuConstants.LABEL_TEXT_FONT).stringWidth(tfl.getLabelText());
                tfl.setWidth(stringWidth + MenuConstants.LABEL_SPACE + tfl.getTextFieldWidth());
            }
            if (menuObject.getWidth() > maxWidth) {
                    maxWidth = menuObject.getWidth();
            }
            totalHeight += menuObject.getHeight();
            totalHeight += MenuConstants.BUTTON_SPACE;
        }
        if (menuObjects.size() > 0) {
            totalHeight -= MenuConstants.BUTTON_SPACE;
        }
        buttonPaneLocation = new Rectangle(maxWidth, totalHeight);
    }
    private boolean fistPaint = true;
    private void layoutButtons(Graphics gr) {
        //Init buttons and button pane
        if (fistPaint) {
            calculateButtonDimensions(gr);
            fistPaint = false;
        }
        buttonPaneLocation.x = (this.getWidth() - buttonPaneLocation.width - 20); 
        buttonPaneLocation.y = (this.getHeight() - buttonPaneLocation.height) / 2;
        int buttonStart = 0;
        for (MenuObject menuObject : menuObjects) {
            menuObject.setX(buttonPaneLocation.x + (buttonPaneLocation.width - menuObject.getWidth()));
            menuObject.setY(buttonPaneLocation.y + buttonStart);
            buttonStart += menuObject.getHeight() + MenuConstants.BUTTON_SPACE;
        }

    }
    
    public void refreshButtonPane(Graphics graphics) {
        layoutButtons(graphics);
        
        for (MenuObject m : menuObjects) {
            if (m instanceof Button) {
                Button b= (Button) m;
                graphics.setColor(new Color(20,20,20));
                graphics.fillRect(m.getX(), m.getY(), m.getWidth(), m.getHeight());
                graphics.setColor(Color.green);
                graphics.setFont(MenuConstants.BUTTON_TEXT_FONT);
                graphics.drawString(b.text, b.getX() + MenuConstants.BUTTON_BOARDER_SIDE, b.getY() - MenuConstants.BUTTON_BOARDER_TOP + b.getHeight());
            }
            if (m instanceof TextField) {
                TextField tf = (TextField) m;
                graphics.setColor(Color.green);
                Graphics2D g2 = (Graphics2D) graphics;
                float thickness = 1;
                if (tf.isInFocus()) {
                    thickness = 3;
                }
                Stroke oldStroke = g2.getStroke();
                g2.setStroke(new BasicStroke(thickness));
                graphics.drawRect(m.getX(), m.getY(), m.getWidth() - 1, m.getHeight() - 1);
                g2.setStroke(oldStroke);
                graphics.setFont(MenuConstants.TEXTFIELD_TEXT_FONT);
                graphics.drawString(tf.getText(), tf.getX() + 5, tf.getY() - 5 + tf.getHeight());

            }
            if (m instanceof TextFieldAndLabel) {
                TextFieldAndLabel tfl = (TextFieldAndLabel) m;
                graphics.setColor(new Color(0,255,0));
                Graphics2D g2 = (Graphics2D) graphics;
                float thickness = 1;
                if (tfl.isInFocus()) {
                    thickness = 3;
                }
                Stroke oldStroke = g2.getStroke();
                g2.setStroke(new BasicStroke(thickness));
                graphics.drawRect(tfl.getTextFieldX(), tfl.getTextFieldY(), tfl.getTextFieldWidth() - 1, tfl.getTextFieldHeight() - 1);
                g2.setStroke(oldStroke);
                graphics.setFont(MenuConstants.TEXTFIELD_TEXT_FONT);
                graphics.drawString(tfl.getText(), tfl.getTextFieldX() + 5, tfl.getTextFieldY() - 5 + tfl.getTextFieldHeight());
                graphics.setFont(MenuConstants.LABEL_TEXT_FONT);
                graphics.drawString(tfl.getLabelText(), tfl.getX(), tfl.getY() - 5 + tfl.getHeight());
            }
        }
    }

    public void keyTyped(KeyEvent e) {

    }
    
    private void addMenuObject(MenuObject mo) {
        menuObjects.add(mo);
        if (mo.needsKeyInput()) {
            this.addKeyListener(mo);
        }
        if (mo.needsMouseInput()) {
            this.addMouseListener(mo);
        }
    }
    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
        for (MenuObject m : menuObjects) {
            if (m.getDimensions().contains(e.getPoint())) {
                m.clicked(e);
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
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
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
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
    }

}
