package com.welles.View;

import javax.swing.*;

public class CardView extends JLabel {
    String card_name;
    int id; // 唯一标识在场servant的id
    public CardView(Icon image) {
        super(image);
    }

    public String GetCardName() { return card_name; }
    public void SetCardName(String card_name) {
        this.card_name = card_name;
    }

    public int GetId() {
        return id;
    }

    public void SetId(int id) {
        this.id = id;
    }
}
