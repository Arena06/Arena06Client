package com.assemblr.arena06.client.scene;

import com.assemblr.arena06.client.gui.Action;
import com.assemblr.arena06.client.gui.Button;
import com.assemblr.arena06.client.gui.FormView;
import com.assemblr.arena06.client.gui.Label;
import com.assemblr.arena06.client.gui.PaddedView;
import com.assemblr.arena06.client.gui.TextField;
import com.assemblr.arena06.client.utils.DeltaRunnable;
import com.assemblr.arena06.client.utils.DeltaRunner;
import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;


public class MenuScene extends ViewScene {
    
    private DeltaRunner runner;
    
    private PaddedView paddedView;
    private TextField ipAddressField;
    private TextField usernameField;
    
    public MenuScene() {
        getView().setBackground(new Color(0x222222));
        
        ipAddressField = new TextField();
        usernameField = new TextField();
        
        FormView form = new FormView(130, 400);
        form.addEntry(new Label("IP Address:"), ipAddressField);
        form.addEntry(new Label("Username:"), usernameField);
        form.addButton(new Button("Submit", new Action() {
            public void act() {
                String[] ip = ipAddressField.getText().split(":");
                String address = ip[0];
                int port = ip.length >= 2 ? Integer.parseInt(ip[1]) : 30155;
                getNavigationController().pushScene(new GameScene(address, port, usernameField.getText()));
            }
        }));
        
        paddedView = new PaddedView(form);
        getView().addChild(paddedView);
        
        ipAddressField.requestFocus();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        paddedView.setWidth(getWidth());
        paddedView.setHeight(getHeight());
        super.paintComponent(g);
    }
    
    @Override
    public void sceneWillAppear() {
        super.sceneWillAppear();
        runner = new DeltaRunner(30, new Runnable() {
            public void run() {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            paintImmediately(0, 0, getWidth(), getHeight());
                        }
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
        }, DeltaRunnable.NULL);
        runner.start();
    }
    
    @Override
    public void sceneWillDisappear() {
        super.sceneWillDisappear();
        runner.requestStop();
    }
    
}
