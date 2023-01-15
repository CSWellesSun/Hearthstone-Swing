package com.welles;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class Deck {
    static ArrayList<String> card_names = new ArrayList<>();
    static {
        card_names.add("冰风雪人");
        card_names.add("团队领袖");
        card_names.add("森金持盾卫士");
        card_names.add("暴风城勇士");
        card_names.add("巫医");
        card_names.add("丛林猎豹");
        card_names.add("荆棘谷猛虎");
        card_names.add("暮光幼龙");
        card_names.add("阿古斯防御者");
        card_names.add("战利品贮藏者");
        card_names.add("碧蓝幼龙");
        card_names.add("精灵弓箭手");
    }

    private ArrayList<String> deck;

    public Deck(ArrayList<String> deck) {
        this.deck = deck;
    }
    public Deck(Random r) {
        deck = new ArrayList<>();
        while (deck.size() != 30) {
            int random_index = r.nextInt(card_names.size());
            String card_name = card_names.get(random_index);
            // TODO 去重
            deck.add(card_name);
        }
    }

    public ArrayList<String> DrawCard(int num) {
        ArrayList<String> draw_cards = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            if (deck != null && deck.size() != 0) {
                String card = deck.remove(0);
                draw_cards.add(card);
            }
        }
        return draw_cards;
    }
}
