package com.welles.Model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Target {
    private int hp; // 生命值
    private TargetClass target_class;
    /** Utility field used by event firing mechanism. */
    private ArrayList<ActionListener> actionListenerList;

    public void SetHp(int hp) { this.hp = hp; }
    public int GetHp() { return hp; }
    public void RestoreHp(int restore_num) {
        SetHp(GetHp() + restore_num);
    }
    public void ConsumeHp(int consume_num) {
        SetHp(GetHp() - consume_num);
    }

    public TargetClass GetTargetClass() {
        return target_class;
    }

    Target(TargetClass target_class) {
        this.target_class = target_class;
    }

    /** Register an action event listener */
    public synchronized void addActionListener(ActionListener l) {
        if (actionListenerList == null)
            actionListenerList = new ArrayList<ActionListener>();

        actionListenerList.add(l);
    }

    /** Remove an action event listener */
    public synchronized void removeActionListener(ActionListener l) {
        if (actionListenerList != null && actionListenerList.contains(l))
            actionListenerList.remove(l);
    }

    /** Fire TickEvent */
    protected void processEvent(ActionEvent e) {
        ArrayList<ActionListener> list;

        synchronized (this) {
            if (actionListenerList == null) return;
            list = (ArrayList<ActionListener>)(actionListenerList.clone());
        }

        for (int i = 0; i < list.size(); i++) {
            ActionListener listener = list.get(i);
            listener.actionPerformed(e);
        }
    }
}
