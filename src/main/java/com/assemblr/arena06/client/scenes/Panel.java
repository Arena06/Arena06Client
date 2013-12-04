/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assemblr.arena06.client.scenes;

import javax.swing.JPanel;

public abstract class Panel extends JPanel {
    /*
    This is called whenever a panel is about to be displayed to the user.
    */
    public abstract void enteringView();
    
    /*
    This is called whenever a panel is about to be removed from the user's view.
    The panel should still keep its variables allocated and remain in connection with any servers because the panel may return to view later on.
    */
    public abstract void leavingView();
    
    /*
    This is called before a panel is perminetly distroyed by garbage collection.
    This method should be used to close any streams and disconnect from any servers.
    */
    public abstract void dispose();
}
