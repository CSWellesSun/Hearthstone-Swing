package com.welles.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Card {
    private String card_name;
    private String resource_addr;
    /**
     * target_effect_phase需要按照顺序放置，其中需要选定目标的放在最后一个
     */
    private ArrayList<TargetEffectPhase> target_effect_phase;
    private int consume_mp;

    public Card(String card_name, int consume_mp, String resource_addr, ArrayList<TargetEffectPhase> target_effect_phase) {
        this.card_name = card_name;
        this.resource_addr = resource_addr;
        this.target_effect_phase = target_effect_phase;
        this.consume_mp = consume_mp;
    }
    public int GetConsumeMp() { return consume_mp; }
    public String GetResourceAddr() { return resource_addr; }
    public String GetCardName() { return card_name; }
    public ArrayList<TargetEffectPhase> GetTargetEffectPhase() { return target_effect_phase; }
    public static final HashMap<String, Card> CardPool = new HashMap<>();
    static {
        CardPool.put("精灵弓箭手", new ServantCard("精灵弓箭手", 1, "CORE_CS2_189.png", new ArrayList<TargetEffectPhase>(){
            {
                add(new TargetEffectPhase(TargetClass.Opponent, new Effect(Effect.ConsumeHp, 1), RoundPhase.WaitForCardTarget));
            }
        }, 1, 1));
        CardPool.put("冰风雪人", new ServantCard("冰风雪人", 4, "CORE_CS2_182.png", null, 4, 5));
        CardPool.put("团队领袖", new ServantCard("团队领袖", 3, "CORE_CS2_122.png", null, 2, 3));
        CardPool.put("森金持盾卫士", new ServantCard("森金持盾卫士", 4, "CORE_CS2_179.png", null, 3, 5));
        CardPool.put("暴风城勇士", new ServantCard("暴风城勇士", 7, "CORE_CS2_222.png", null, 7, 7));
        CardPool.put("巫医", new ServantCard("巫医", 1, "CORE_EX1_011.png", null, 2, 1));
        CardPool.put("丛林猎豹", new ServantCard("丛林猎豹", 3, "CORE_EX1_017.png", null, 4, 2));
        CardPool.put("荆棘谷猛虎", new ServantCard("荆棘谷猛虎", 5, "CORE_EX1_028.png", null, 5, 5));
        CardPool.put("暮光幼龙", new ServantCard("暮光幼龙", 4, "CORE_EX1_043.png", null, 4, 1));
        CardPool.put("阿古斯防御者", new ServantCard("阿古斯防御者", 4, "CORE_EX1_093.png", null, 3, 3));
        CardPool.put("战利品贮藏者", new ServantCard("战利品贮藏者", 2, "CORE_EX1_096.png", null, 2, 1));
        CardPool.put("碧蓝幼龙", new ServantCard("碧蓝幼龙", 5, "CORE_EX1_284.png", null, 4, 5));
    }
}

