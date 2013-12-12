package com.assemblr.arena06.client.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import org.javatuples.Pair;


public class FormView extends View {
    
    private static final int ITEM_PADDING = 12;
    
    private int labelWidth;
    private int fieldWidth;
    
    private final List<Pair<Label, TextField>> formEntries = new ArrayList<Pair<Label, TextField>>();
    private final List<Button> buttons = new ArrayList<Button>();
    
    public FormView() {
        this(100, 100);
    }
    
    public FormView(int labelWidth, int fieldWidth) {
        this.labelWidth = labelWidth;
        this.fieldWidth = fieldWidth;
    }
    
    public void addEntry(Label l, TextField tf) {
        formEntries.add(new Pair<Label, TextField>(l, tf));
        addChild(l);
        addChild(tf);
    }
    
    public void addButton(Button b) {
        buttons.add(b);
        addChild(b);
    }
    
    @Override
    public void layout() {
        super.layout();
        
        int width = labelWidth + ITEM_PADDING + fieldWidth;
        int height = 0;
        for (int i = 0; i < formEntries.size(); i++) {
            Pair<Label, TextField> entry = formEntries.get(i);
            Label l = entry.getValue0();
            TextField tf = entry.getValue1();
            
            l.setWidth(labelWidth);
            tf.setWidth(fieldWidth);
            
            l.setX(0);
            tf.setX(labelWidth + ITEM_PADDING);
            
            l.setY(height + ((tf.getHeight() - l.getHeight()) / 2));
            tf.setY(height);
            
            l.setAlignment(Label.Alignment.RIGHT);
            
            height += tf.getHeight() + ITEM_PADDING;
        }
        
        int buttonWidth = (width - ITEM_PADDING * (buttons.size() - 1)) / buttons.size();
        for (int i = buttons.size() - 1; i >= 0; i--) {
            Button b = buttons.get(i);
            b.setWidth(buttonWidth);
            b.setX((buttonWidth + ITEM_PADDING) * i);
            b.setY(height);
        }
        height += 50;
        
        setWidth(width);
        setHeight(height);
    }
    
    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_TAB) {
            int focused = -1;
            for (int i = 0; i < formEntries.size(); i++) {
                if (formEntries.get(i).getValue1().isFocused()) {
                    focused = i;
                    break;
                }
            }
            if (focused != -1) {
                int index = (focused + (ke.isShiftDown() ? formEntries.size() - 1 : 1)) % formEntries.size();
                formEntries.get(index).getValue1().requestFocus();
            }
        } else if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            buttons.get(0).getAction().act();
        } else {
            super.keyPressed(ke);
        }
    }
    
    @Override
    public void keyTyped(KeyEvent ke) {
        if (ke.getKeyChar() != '\t' && ke.getKeyChar() != '\n' && ke.getKeyChar() != '\r')
            super.keyTyped(ke);
    }
    
    public int getLabelWidth() {
        return labelWidth;
    }
    
    public void setLabelWidth(int labelWidth) {
        this.labelWidth = labelWidth;
    }
    
    public int getFieldWidth() {
        return fieldWidth;
    }
    
    public void setFieldWidth(int fieldWidth) {
        this.fieldWidth = fieldWidth;
    }
    
}
