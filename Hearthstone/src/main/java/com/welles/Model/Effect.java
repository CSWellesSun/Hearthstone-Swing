package com.welles.Model;

import java.util.function.BiConsumer;

public class Effect {
    private BiConsumer<Target, Integer> f;
    private int num;
    Effect(BiConsumer<Target, Integer> f, int num) {
        this.f = f;
        this.num = num;
    }

    public void apply(Target target) {
        f.accept(target, num);
    }

    public static final BiConsumer<Target, Integer> RestoreHp = (target, restore_num) -> {
        target.RestoreHp(restore_num);
    };

    public static final BiConsumer<Target, Integer> ConsumeHp = (target, consume_num) -> {
        target.ConsumeHp(consume_num);
    };
}
