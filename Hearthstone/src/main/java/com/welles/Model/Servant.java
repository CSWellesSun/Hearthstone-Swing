package com.welles.Model;

import com.welles.Controller.Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Servant extends Target {
    private String name;
    private int attack;
    private int private_id;
    private boolean has_attack;
    private static int global_id = 0; // 唯一标记servant的id

    static private ActionListener listener;
    static public void SetActionListener(ActionListener l) { listener = l; }
    static public void ProcessEvent(ActionEvent e) {
        listener.actionPerformed(e);
    }

    // TODO 效果：潜行、在场给其他加BUFF等效果暂时还没写
    public Servant(TargetClass target_class, String name, int hp, int attack) {
        super(target_class);
        SetHp(hp);
        this.name = name;
        this.attack = attack;
        private_id = global_id++;
    }

    public Servant(TargetClass target_class, String name, int hp, int attack, int id) {
        super(target_class);
        SetHp(hp);
        this.name = name;
        this.attack = attack;
        private_id = id;
    }

    public String GetName() {
        return name;
    }

    public int GetAttack() {
        return attack;
    }

    public int GetPrivateId() {
        return private_id;
    }

    public boolean GetHasAttack() {
        return has_attack;
    }

    public void SetHasAttack(boolean has_attack) {
        this.has_attack = has_attack;
    }

    public void ConsumeHp(int consume_num) {
        SetHp(GetHp() - consume_num);
        if (GetTargetClass() == TargetClass.OpponentServant) {
            processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "OpponentServantConsumeHp"));
        } else if (GetTargetClass() == TargetClass.PlayerServant) {
            processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "PlayerServantConsumeHp"));
        }

        if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player) {
            Client.getClient().SendAndWaitAck("ServantConsumeHp " + GetPrivateId() + " " + consume_num);
        }
    }

    public void RestoreHp(int restore_num) {
        SetHp(GetHp() + restore_num);
        if (GetTargetClass() == TargetClass.OpponentServant) {
            processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "OpponentServantRestoreHp"));
        } else if (GetTargetClass() == TargetClass.PlayerServant) {
            processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "PlayerServantRestoreHp"));
        }

        if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player) {
            Client.getClient().SendAndWaitAck("ServantRestoreHp " + GetPrivateId() + " " + restore_num);
        }
    }
}
