package com.welles.Model;

import java.util.ArrayList;

public class ServantCard extends Card {
    private int attack;
    private int hp;

    public ServantCard(String card_name, int consume_mp, String resource_addr, ArrayList<TargetEffectPhase> targets, int attack, int hp) {
        super(card_name, consume_mp, resource_addr, targets);
        this.attack = attack;
        this.hp = hp;
    }
    public int GetHp() { return hp; }
    public int GetAttack() { return attack; }
}
