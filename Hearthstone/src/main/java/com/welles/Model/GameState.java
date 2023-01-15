package com.welles.Model;

import com.welles.Controller.Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GameState {
    private static GamePhase game_phase = GamePhase.Closed;
    private static Round current_round = Round.Null;
    private static RoundPhase round_phase = RoundPhase.Null;
    private ArrayList<ActionListener> actionListenerList;

    public static GamePhase GetGamePhase() {
        return game_phase;
    }

    public static RoundPhase GetRoundPhase() {
        return round_phase;
    }

    public static Round GetCurrentRound() {
        return current_round;
    }

    public static void SetGamePhase(GamePhase gp) {
        game_phase = gp;
    }

    public static void SetCurrentRound(Round r) {
        current_round = r;
    }

    public static void SetRoundPhase(RoundPhase rp) {
        round_phase = rp;
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
