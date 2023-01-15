package com.welles.Controller;

import com.welles.Model.*;
import com.welles.View.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

public class Controller {
    private Model model;
    private View view;
    private Client client;

    public void SetModel(Model model) {
        this.model = model;
    }

    public void SetView(View view) {
        this.view = view;
    }

    public void AddViewListener() {
        // View 状态机转移
        view.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (model.GetGameState().GetGamePhase() == GamePhase.Closed) {
                    model.ChangeGamePhase(GamePhase.WaitForOpponent);
                    new Thread(() -> {
                        client = Client.getClient();
                        client.Init();
                        String response = client.ReceiveAndAck();
                        if (response == null) {
                            System.out.println("[ERROR] Response is Null!");
                        } else if (response.equals("First")) { // 先手
                            model.StartGame(Round.Player);
                        } else if (response.equals("Second")) { // 后手
                            model.StartGame(Round.Opponent);
                        }
                    }).start();
                } else if (GameState.GetGamePhase() == GamePhase.PlayerWin || GameState.GetGamePhase() == GamePhase.OpponentWin) {
                    model.ChangeGamePhase(GamePhase.Closed);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        // PlayerCardView 设置卡牌鼠标控制
        view.GetPlayerCardView().SetMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!(model.GetGameState().GetGamePhase() == GamePhase.Undergoing && model.GetGameState().GetCurrentRound() == Round.Player)) {
                    // 如果不是player行动阶段直接退出
                    return;
                }
                /**
                 * WaitForSelect 阶段选择了手牌
                 */
                if (model.GetGameState().GetRoundPhase() == RoundPhase.WaitForSelect) {
                    // 等待出牌阶段，此时选择了一张牌
                    String card_name = ((CardView) e.getSource()).GetCardName();
                    Card card = Card.CardPool.get(card_name);
                    int consume_mp = card.GetConsumeMp();
                    if (model.GetPlayer().GetMp() < consume_mp) {
                        return;
                    }
                    if (card instanceof ServantCard) { // 选择随从牌
                        // 添加到player拥有的随从中
                        ServantCard servant_card = (ServantCard) card;
                        Servant servant = new Servant(TargetClass.PlayerServant, servant_card.GetCardName(), servant_card.GetHp(), servant_card.GetAttack());
                        servant.SetHasAttack(true);
                        model.GetPlayer().AddServant(servant);
                        if (card.GetTargetEffectPhase() != null) {
                            // TargetEffectPhase中需要选定目标的effect放在最后面
                            for (TargetEffectPhase tep : card.GetTargetEffectPhase()) {
                                if (tep.GetRoundPhase() == RoundPhase.Null) {
                                    // TODO 持续效果，暂时还未实现
                                } else if (tep.GetRoundPhase() == RoundPhase.WaitForSelect) { // 立即执行
                                    // TODO 暂时只实现对双方英雄的效果
                                    if (tep.GetTargetClass() == TargetClass.PlayerHero) {
                                        tep.GetEffect().apply(model.GetPlayer());
                                    } else if (tep.GetTargetClass() == TargetClass.OpponentHero) {
                                        tep.GetEffect().apply(model.GetOpponent());
                                    }
                                } else if (tep.GetRoundPhase() == RoundPhase.WaitForCardTarget) { // 选择执行对象
                                    model.GetPlayer().SetCurrentCard(card);
                                    model.ChangeRoundPhase(RoundPhase.WaitForCardTarget);
                                }
                            }
                        }
                    } else if (card instanceof SpellCard) { // 选择法术牌
                        // TODO 法术牌暂时未实现
                    }

                    if (model.GetGameState().GetRoundPhase() == RoundPhase.WaitForSelect) {
                        // 该牌已经使用掉，可以从手牌中删除
                        model.GetPlayer().UseCard(card_name);
                        // 该牌已经被使用掉，可以消耗法力水晶
                        model.GetPlayer().ConsumeMp(consume_mp);
                    }
                }
                /**
                 * WaitForCardTarget阶段
                 */
                else if (model.GetGameState().GetRoundPhase() == RoundPhase.WaitForCardTarget) {
                    // TODO 选择了卡牌的对象
                    model.ChangeRoundPhase(RoundPhase.WaitForSelect);
                }
                // TODO 其他核心逻辑
            }


            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        // 对方英雄的鼠标处理
        view.GetOpponentHeroView().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!(model.GetGameState().GetGamePhase() == GamePhase.Undergoing && model.GetGameState().GetCurrentRound() == Round.Player)) {
                    // 如果不是player行动阶段直接退出
                    return;
                }
                if (model.GetGameState().GetRoundPhase() == RoundPhase.WaitForCardTarget) {
                    // 对方英雄被指定为卡牌的对象
                    Card card = model.GetPlayer().GetCurrentCard();
                    // 最后一个TEP是将要执行的TEP
                    TargetEffectPhase tep = card.GetTargetEffectPhase().get(card.GetTargetEffectPhase().size() - 1);
                    if (tep.GetTargetClass() != TargetClass.Opponent && tep.GetTargetClass() != TargetClass.OpponentHero) {
                        return;
                    }
                    // 对对方释放该效果
                    tep.GetEffect().apply(model.GetOpponent());
                    // 该牌已经使用掉，可以从手牌中删除
                    model.GetPlayer().UseCard(card.GetCardName());
                    // 该牌已经被使用掉，可以消耗法力水晶
                    model.GetPlayer().ConsumeMp(card.GetConsumeMp());
                    model.ChangeRoundPhase(RoundPhase.WaitForSelect);
                } else if (model.GetGameState().GetRoundPhase() == RoundPhase.WaitForServantTarget) {
                    // 对方英雄被指定为随从攻击的对象
                    Servant s = model.GetPlayer().GetCurrentServant();
                    model.GetOpponent().ConsumeHp(s.GetAttack());
                    model.ChangeRoundPhase(RoundPhase.WaitForSelect);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


        // View 中结束回合按钮的设置
        view.GetEndRoundView().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // End Round
                if (model.GetGameState().GetGamePhase() == GamePhase.Undergoing
                        && model.GetGameState().GetCurrentRound() == Round.Player) {
                    model.ChangeRound(Round.Opponent);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        // 友方随从牌的鼠标
        view.GetPlayerServantView().SetMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!(model.GetGameState().GetGamePhase() == GamePhase.Undergoing && model.GetGameState().GetCurrentRound() == Round.Player)) {
                    // 如果不是player行动阶段直接退出
                    return;
                }
                /**
                 * WaitForSelect 阶段选择了场上的一只随从
                 */
                if (GameState.GetRoundPhase() == RoundPhase.WaitForSelect) {
                    CardView card = (CardView) e.getSource();
                    int id = card.GetId();
                    Servant s = null;
                    ArrayList<Servant> servants = model.GetPlayer().GetServants();
                    for (int i = 0; i < servants.size(); i++) {
                        if (servants.get(i).GetPrivateId() == id) {
                            s = servants.get(i);
                            break;
                        }
                    }
                    if (s == null || s.GetHasAttack() == true) return;
                    model.GetPlayer().SetCurrentServant(s);
                    model.ChangeRoundPhase(RoundPhase.WaitForServantTarget);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        // 敌方随从牌的鼠标
        view.GetOpponentServantView().SetMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!(model.GetGameState().GetGamePhase() == GamePhase.Undergoing && model.GetGameState().GetCurrentRound() == Round.Player)) {
                    // 如果不是player行动阶段直接退出
                    return;
                }
                /**
                 * WaitForServantTarget阶段
                 */
                if (GameState.GetRoundPhase() == RoundPhase.WaitForServantTarget) {
                    // WaitForServantTarget 表示选择一只随从作为当前
                    CardView card = (CardView) e.getSource();
                    int id = card.GetId();
                    Servant opponent_servant = null;
                    ArrayList<Servant> servants = model.GetOpponent().GetServants();
                    for (int i = 0; i < servants.size(); i++) {
                        if (servants.get(i).GetPrivateId() == id) {
                            opponent_servant = servants.get(i);
                            break;
                        }
                    }
                    if (opponent_servant != null) {
                        Servant player_servant = model.GetPlayer().GetCurrentServant();
                        opponent_servant.ConsumeHp(player_servant.GetAttack());
                        player_servant.ConsumeHp(opponent_servant.GetAttack());

                        if (opponent_servant.GetHp() <= 0) {
                            model.GetOpponent().LoseServant(opponent_servant.GetPrivateId());
                        }
                        if (player_servant.GetHp() <= 0) {
                            model.GetPlayer().LoseServant(player_servant.GetPrivateId());
                        }
                    }
                    model.ChangeRoundPhase(RoundPhase.WaitForSelect);
                }
                else if (GameState.GetRoundPhase() == RoundPhase.WaitForCardTarget) {
                    CardView opponent_card = (CardView) e.getSource();
                    int id = opponent_card.GetId();
                    Servant opponent_servant = null;
                    ArrayList<Servant> servants = model.GetOpponent().GetServants();
                    for (int i = 0; i < servants.size(); i++) {
                        if (servants.get(i).GetPrivateId() == id) {
                            opponent_servant = servants.get(i);
                            break;
                        }
                    }
                    if (opponent_servant != null) {
                        // 对方英雄被指定为卡牌的对象
                        Card card = model.GetPlayer().GetCurrentCard();
                        // 最后一个TEP是将要执行的TEP
                        TargetEffectPhase tep = card.GetTargetEffectPhase().get(card.GetTargetEffectPhase().size() - 1);
                        if (tep.GetTargetClass() != TargetClass.Opponent && tep.GetTargetClass() != TargetClass.OpponentHero) {
                            return;
                        }
                        // 对对方释放该效果
                        tep.GetEffect().apply(opponent_servant);
                        if (opponent_servant.GetHp() <= 0) {
                            model.GetOpponent().LoseServant(opponent_servant.GetPrivateId());
                        }
                    }
                    model.ChangeRoundPhase(RoundPhase.WaitForSelect);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
}
