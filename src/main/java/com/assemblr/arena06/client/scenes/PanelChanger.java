/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assemblr.arena06.client.scenes;

/**
 *
 * @author Henry
 */
public class PanelChanger {
    private Panel pannel;
    public void changePannel(Panel newPannel) {
        this.pannel = newPannel;
    }
    public Panel getPannel() {
        return pannel;
    }
}
