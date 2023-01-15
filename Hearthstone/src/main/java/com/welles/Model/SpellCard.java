package com.welles.Model;

import java.util.ArrayList;

public class SpellCard extends Card {
    public SpellCard(String card_name, int consume_mp, String resource_addr, ArrayList<TargetEffectPhase> targets) {
        super(card_name, consume_mp, resource_addr, targets);
    }
}
