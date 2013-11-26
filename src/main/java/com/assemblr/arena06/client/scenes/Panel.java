/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assemblr.arena06.client.scenes;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

/**
 *
 * @author Henry
 */
public abstract class Panel extends JPanel implements KeyEventDispatcher, KeyListener {
    public abstract void enteringView();
    public abstract void leavingView();
}
