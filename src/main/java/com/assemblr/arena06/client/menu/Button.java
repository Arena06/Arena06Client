/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assemblr.arena06.client.menu;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class Button extends MenuObject{
    private boolean initialized = false;
    public final String text;
    private final ButtonAction action;
    public Button(String text, ButtonAction action) {
        this.text = text;
        this.action = action;
        needsKeyInput = false;
        needsMouseInput = false;
        
    }

    @Override
    public void clicked(MouseEvent me) {
        action.buttonPressed(me);
    }
    
    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return initialized;
    } 
   
    
}
