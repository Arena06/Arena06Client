/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assemblr.arena06.client.navigation;

import com.assemblr.arena06.client.scenes.Panel;

public interface NavigationControler {
    public void pushPanel(Panel panel);
    public void popPanel();
    public void swapCurrentPanel(Panel newPanel);
}
