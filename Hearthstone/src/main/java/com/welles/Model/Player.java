package com.welles.Model;

import com.welles.Controller.Client;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Player extends Target {
    private int mp; // 剩余魔法水晶数
    private ArrayList<Card> cards_hand = new ArrayList<Card>();
    private ArrayList<Servant> servants = new ArrayList<Servant>();
    /**
     * 在Select阶段选择的牌，到WaitForCardTarget阶段使用
     */
    private Card current_card;
    private Servant current_servant;

    public Player() {
        super(TargetClass.PlayerHero);
    }

    @Override
    public void SetHp(int hp) {
        super.SetHp(hp);
        processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SetHp"));
    }
    public void SetMp(int mp) {
        this.mp = mp;
        processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SetMp"));
    }
    public int GetMp() { return mp; }
    public void RestoreMp(int restore_num) {
        SetMp(GetMp() + restore_num);
        if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player)
            Client.getClient().SendAndWaitAck("RestorePlayerHeroMp " + restore_num);
    }
    public void ConsumeMp(int consume_num) {
        SetMp(GetMp() - consume_num);
        if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player)
            Client.getClient().SendAndWaitAck("ConsumePlayerHeroMp " + consume_num);
    }

    public void RestoreHp(int restore_num) {
        super.RestoreHp(restore_num);
        if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player)
            Client.getClient().SendAndWaitAck("RestorePlayerHeroHp " + restore_num);
    }

    public void ConsumeHp(int consume_num) {
        super.ConsumeHp(consume_num);
        if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player) {
            Client.getClient().SendAndWaitAck("ConsumePlayerHeroHp " + consume_num);
            if (GetHp() <= 0) {
                Client.getClient().SendAndWaitAck("OpponentWin");
                GameState.SetGamePhase(GamePhase.OpponentWin);
            }
        }
    }

    public ArrayList<Servant> GetServants() { return servants; }
    public ArrayList<Card> GetCardsHand() {
        return cards_hand;
    }
    public void AddServant(Servant s) {
        servants.add(s);
        processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AddServant"));

        if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player)
            Client.getClient().SendAndWaitAck("PlayerAddServant " + s.GetName() + " " + s.GetPrivateId());
    }
    public void LoseServant(int id) {
        for (int i = 0; i < servants.size(); i++) {
            if (servants.get(i).GetPrivateId() == id) {
                servants.remove(i);
                processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "LoseServant"));
                if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player)
                    Client.getClient().SendAndWaitAck("PlayerLoseServant " + id);
                break;
            }
        }
    }
    public void DrawCard(String name) {
        if (cards_hand.size() < 9) {
            cards_hand.add(Card.CardPool.get(name));
            // 触发获取牌的事件
            processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "DrawCard"));
            // TODO 用户获得卡
        }
    }
    public void UseCard(String name) {
        for (Card card : cards_hand) {
            if (card.GetCardName().equals(name)) {
                cards_hand.remove(card);
                break;
            }
        }
        processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "UseCard"));
        if (GameState.GetGamePhase() == GamePhase.Undergoing && GameState.GetCurrentRound() == Round.Player) {
            Client.getClient().SendAndWaitAck("PlayerUseCard");
        }
    }

    public Card GetCurrentCard() { return current_card; }
    public void SetCurrentCard(Card card) { this.current_card = card; }

    public Servant GetCurrentServant() { return current_servant; }
    public void SetCurrentServant(Servant servant) { this.current_servant = servant; }

    public void RefreshAttackState() {
        for (Servant s : servants) s.SetHasAttack(false);
    }
}
