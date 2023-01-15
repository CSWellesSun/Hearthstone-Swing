package com.welles.Model;

public class TargetEffectPhase {
    private TargetClass target_class;
    private Effect effect;

    /**
     * 如果RoundPhase是：
     * 1. RoundPhase.Null说明是持续效果
     * 2. RoundPhase.Select表示立刻执行
     * 3. RoundPhase.WaitFor表示需要用户制定目标
     */
    private RoundPhase round_phase;

    public TargetEffectPhase(TargetClass target_class, Effect effect, RoundPhase round_phase) {
        this.target_class = target_class;
        this.effect = effect;
        this.round_phase = round_phase;
    }

    public TargetClass GetTargetClass() { return target_class; }
    public Effect GetEffect() { return effect; }
    public RoundPhase GetRoundPhase() { return round_phase; }
}
