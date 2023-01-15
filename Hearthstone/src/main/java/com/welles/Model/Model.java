package com.welles.Model;

import com.welles.Controller.Client;
import javafx.scene.control.TextFormatter;

import javax.smartcardio.CardPermission;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Model {
    private Player player = new Player();
    private Opponent opponent = new Opponent();
    private GameState game_state = new GameState();

    public Player GetPlayer() {
        return player;
    }

    public Opponent GetOpponent() {
        return opponent;
    }

    public GameState GetGameState() {
        return game_state;
    }

    private int round_count; // 当前回合数，双方都计算在内

    public void ChangeGamePhase(GamePhase next_game_phase) {
        game_state.SetGamePhase(next_game_phase);

        game_state.processEvent(new ActionEvent(game_state, ActionEvent.ACTION_PERFORMED, "ChangeGamePhase"));
    }

    public void ChangeRound(Round next_round) {
        Round last_round = game_state.GetCurrentRound();
        game_state.SetCurrentRound(next_round);
        game_state.processEvent(new ActionEvent(game_state, ActionEvent.ACTION_PERFORMED, "ChangeRound"));
        if (last_round == Round.Player && game_state.GetCurrentRound() == Round.Opponent) {
            Client.getClient().SendAndWaitAck("EndRound");
            // 进入Listen状态
            new Thread(() -> {
                PlayerRound();
            }
            ).start();
        }
    }

    public void ChangeRoundPhase(RoundPhase next_round_phase) {
        game_state.SetRoundPhase(next_round_phase);
        game_state.processEvent(new ActionEvent(game_state, ActionEvent.ACTION_PERFORMED, "ChangeRoundPhase"));
    }

    public void StartGame(Round init_round) {
        ChangeGamePhase(GamePhase.Undergoing);
        ChangeRound(init_round);
        // 初始化
        String msg;
        while (!((msg = Client.getClient().ReceiveAndAck()).equals("End"))) {
            ProcessServerMsg(msg);
        }
        ChangeRoundPhase(RoundPhase.WaitForSelect);

        // 进入正式双方对战
        new Thread(() -> {
            PlayerRound();
        }
        ).start();
    }

    public void PlayerRound() {
        /**
         * 对于Player：抽卡并获得法力水晶，之后就由用户进行操作
         * 对于Opponent：等待客户端发来的所有请求
         */
        round_count++;
        String msg;
        while (!((msg = Client.getClient().ReceiveAndAck()).equals("End"))) {
            ProcessServerMsg(msg);
            if (msg.equals("PlayerRound")) break;
        }
        if (msg != null && msg.equals("PlayerRound")) {
            // 抽卡
            new Thread(() -> {
                player.RefreshAttackState();
                PlayerRound();
            }
            ).start();
        }
    }

    public void ProcessServerMsg(String msg) {
        /**
         * 所有的服务器端消息：
         * PlayerHeroHp [Hp], PlayerHeroMp [Mp], OpponentHeroHp [Hp], OpponentHeroMp [Mp]
         * PlayerDrawCard [NAME], OpponentDrawCard [NUM]
         * ConsumePlayerHeroHp [Hp], ConsumePlayerHeroMp [Mp],
         * RestorePlayerHeroHp [Hp], RestorePlayerHeroMp [Mp],
         * ConsumeOpponentHeroHp [Hp], ConsumeOpponentHeroMp [Mp],
         * RestoreOpponentHeroHp [Hp], RestoreOpponentHeroMp [Mp],
         * IncreasePlayerHeroMp, IncreaseOpponentHeroMp,
         * PlayerRound
         * OpponentAddServant [NAME] [ID]
         * OpponentUseCard
         * ServantConsumeHp [ID] [NUM], ServantRestoreHp [ID] [NUM]
         * PlayerLoseServant [ID], OpponentLoseServant [ID]
         * End
         */
        ArrayList<String> msg_phase = Client.Parse(msg);
        String command = msg_phase.get(0);
        String arg1 = null, arg2 = null;
        if (msg_phase.size() > 1) arg1 = msg_phase.get(1);
        if (msg_phase.size() > 2) arg2 = msg_phase.get(2);

        if (command.equals("PlayerHeroHp")) {
            player.SetHp(Integer.valueOf(arg1));
        } else if (command.equals("PlayerHeroMp")) {
            player.SetMp(Integer.valueOf(arg1));
        } else if (command.equals("OpponentHeroHp")) {
            opponent.SetHp(Integer.valueOf(arg1));
        } else if (command.equals("OpponentHeroMp")) {
            opponent.SetMp(Integer.valueOf(arg1));
        } else if (command.equals("PlayerDrawCard")) {
            player.DrawCard(arg1);
        } else if (command.equals("OpponentDrawCard")) {
            int draw_num = Integer.valueOf(arg1);
            for (int i = 0; i < draw_num; i++)
                opponent.DrawCard();
        } else if (command.equals("RestorePlayerHeroHp")) {
            player.RestoreHp(Integer.valueOf(arg1));
        } else if (command.equals("ConsumePlayerHeroHp")) {
            player.ConsumeHp(Integer.valueOf(arg1));
        } else if (command.equals("RestoreOpponentHeroHp")) {
            opponent.RestoreHp(Integer.valueOf(arg1));
        } else if (command.equals("ConsumeOpponentHeroHp")) {
            opponent.ConsumeHp(Integer.valueOf(arg1));
        } else if (command.equals("RestorePlayerHeroMp")) {
            player.RestoreMp(Integer.valueOf(arg1));
        } else if (command.equals("ConsumePlayerHeroMp")) {
            player.ConsumeMp(Integer.valueOf(arg1));
        } else if (command.equals("RestoreOpponentHeroMp")) {
            opponent.RestoreMp(Integer.valueOf(arg1));
        } else if (command.equals("ConsumeOpponentHeroMp")) {
            opponent.ConsumeMp(Integer.valueOf(arg1));
        } else if (command.equals("PlayerRound")) {
            ChangeRound(Round.Player);
        } else if (command.equals("OpponentAddServant")) {
            Card card = Card.CardPool.get(arg1);
            if (card instanceof ServantCard) {
                opponent.AddServant(new Servant(
                        TargetClass.OpponentServant, arg1, ((ServantCard) card).GetHp(),
                        ((ServantCard) card).GetAttack(), Integer.valueOf(arg2)));
            }
        } else if (command.equals("OpponentUseCard")) {
            opponent.UseCard();
        } else if (command.equals("IncreasePlayerHeroMp")) {
            player.SetMp((round_count + 1) / 2);
        } else if (command.equals("IncreaseOpponentHeroMp")) {
            opponent.SetMp((round_count + 1) / 2);
        } else if (command.equals("ServantConsumeHp")) {
            int id = Integer.valueOf(arg1);
            int consume_num = Integer.valueOf(arg2);
            for (Servant s : player.GetServants()) {
                if (s.GetPrivateId() == id) {
                    s.ConsumeHp(consume_num);
                }
            }
            for (Servant s : opponent.GetServants()) {
                if (s.GetPrivateId() == id) {
                    s.ConsumeHp(consume_num);
                }
            }
        } else if (command.equals("ServantRestoreHp")) {
            int id = Integer.valueOf(arg1);
            int restore_num = Integer.valueOf(arg2);
            for (Servant s : player.GetServants()) {
                if (s.GetPrivateId() == id) {
                    s.RestoreHp(restore_num);
                }
            }
            for (Servant s : opponent.GetServants()) {
                if (s.GetPrivateId() == id) {
                    s.RestoreHp(restore_num);
                }
            }
        } else if (command.equals("PlayerLoseServant")) {
            player.LoseServant(Integer.valueOf(arg1));
        } else if (command.equals("OpponentLoseServant")) {
            opponent.LoseServant(Integer.valueOf(arg1));
        } else if (command.equals("PlayerWin")) {
            ChangeGamePhase(GamePhase.PlayerWin);
        } else if (command.equals("OpponentWin")) {
            ChangeGamePhase(GamePhase.OpponentWin);
        }
    }
}
