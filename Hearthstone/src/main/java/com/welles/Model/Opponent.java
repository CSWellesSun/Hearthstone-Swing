package com.welles.Model;

import com.welles.Controller.Client;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Opponent extends Target {
    private int mp; // 剩余魔法水晶数
    private int cards_hand; // 对方手牌数量
    private ArrayList<Servant> servants = new ArrayList<Servant>();
    /**
     * 在Select阶段选择的牌，到WaitForCardTarget阶段使用
     */
    public Opponent() {
        super(TargetClass.OpponentHero);
    }

    @Override
    public void SetHp(int hp) {
        super.SetHp(hp);
        processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SetOpponentHp"));
    }
    public void SetMp(int mp) {
        this.mp = mp;
        processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SetOpponentMp"));
    }
    public int GetMp() { return mp; }
    public void RestoreMp(int restore_num) {
        SetMp(GetMp() + restore_num);

        if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player)
            Client.getClient().SendAndWaitAck("RestoreOpponentHeroMp " + restore_num);
    }
    public void ConsumeMp(int consume_num) {
        SetMp(GetMp() - consume_num);

        if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player)
            Client.getClient().SendAndWaitAck("ConsumeOpponentHeroMp " + consume_num);
    }

    public void RestoreHp(int restore_num) {
        super.RestoreHp(restore_num);
        if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player)
            Client.getClient().SendAndWaitAck("RestoreOpponentHeroHp " + restore_num);
    }

    public void ConsumeHp(int consume_num) {
        super.ConsumeHp(consume_num);
        if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player) {
            Client.getClient().SendAndWaitAck("ConsumeOpponentHeroHp " + consume_num);
            if (GetHp() <= 0) {
                Client.getClient().SendAndWaitAck("PlayerWin");
                GameState.SetGamePhase(GamePhase.PlayerWin);
            }
        }
    }
    public ArrayList<Servant> GetServants() { return servants; }
    public int GetCardsHand() {
        return cards_hand;
    }
    public void AddServant(Servant s) {
        servants.add(s);
        processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "OpponentAddServant"));
    }
    public void LoseServant(int id) {
        for (int i = 0; i < servants.size(); i++) {
            if (servants.get(i).GetPrivateId() == id) {
                servants.remove(i);
                processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "OpponentLoseServant"));
                if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player)
                    Client.getClient().SendAndWaitAck("OpponentLoseServant " + id);
                break;
            }
        }
    }
    public void DrawCard() {
        if (cards_hand < 9) {
            cards_hand += 1;
            // 触发获取牌的事件
            processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "OpponentDrawCard"));
        }
    }
    public void UseCard() {
        cards_hand -= 1;
        processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "OpponentUseCard"));
    }
}
