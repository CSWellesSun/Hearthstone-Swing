package com.welles;

import javax.script.ScriptEngine;
import java.awt.image.AreaAveragingScaleFilter;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

enum Round {
    Null, Client1, Client2, GameOver
}

public class Game {
    // TODO 检查双方卡牌和法力水晶上限值
    private Socket client1;
    private Socket client2;
    private Random random;
    private Round current_round = Round.Null;
    private Deck client1_deck;
    private Deck client2_deck;

    public Game(Session session) {
        this.client1 = session.GetClient1();
        this.client2 = session.GetClient2();
        random = new Random(System.currentTimeMillis()); // 设置随机数发生器
    }

    private void InitState() throws IOException {
        // 给双方设置先后手
        current_round = (random.nextBoolean()) ? Round.Client1 : Round.Client2;
        // 给双方随机生成手牌
        client1_deck = new Deck(random);
        client2_deck = new Deck(random);
        // 给双方发送开始游戏消息
        if (current_round == Round.Client1) {
            SendAndWaitAck(client1, "First");
            SendAndWaitAck(client2, "Second");
        } else {
            SendAndWaitAck(client2, "First");
            SendAndWaitAck(client1, "Second");
        }
        // 给双方发送血量和法力水晶
        SendAndWaitAck(client1, "PlayerHeroHp 30");
        SendAndWaitAck(client1, "PlayerHeroMp 0");
        SendAndWaitAck(client1, "OpponentHeroHp 30");
        SendAndWaitAck(client1, "OpponentHeroMp 0");
        SendAndWaitAck(client2, "PlayerHeroHp 30");
        SendAndWaitAck(client2, "PlayerHeroMp 0");
        SendAndWaitAck(client2, "OpponentHeroHp 30");
        SendAndWaitAck(client2, "OpponentHeroMp 0");

        // 让双方抽牌，先手三张，后手四张
        if (current_round == Round.Client1) {
            ArrayList<String> client1_draw = client1_deck.DrawCard(3);
            for (String card : client1_draw) {
                SendAndWaitAck(client1, "PlayerDrawCard " + card);
            }
            SendAndWaitAck(client1, "OpponentDrawCard 4");
            ArrayList<String> client2_draw = client2_deck.DrawCard(4);
            for (String card : client2_draw) {
                SendAndWaitAck(client2, "PlayerDrawCard " + card);
            }
            SendAndWaitAck(client2, "OpponentDrawCard 3");
        } else {
            ArrayList<String> client2_draw = client2_deck.DrawCard(3);
            for (String card : client2_draw) {
                SendAndWaitAck(client2, "PlayerDrawCard " + card);
            }
            SendAndWaitAck(client2, "OpponentDrawCard 4");
            ArrayList<String> client1_draw = client2_deck.DrawCard(4);
            for (String card : client1_draw) {
                SendAndWaitAck(client1, "PlayerDrawCard " + card);
            }
            SendAndWaitAck(client1, "OpponentDrawCard 3");
        }

        // 服务端结束
        SendAndWaitAck(client1, "End");
        SendAndWaitAck(client2, "End");

        System.out.println("InitState Success");
    }

    private void ProcessClient() throws IOException {
        Socket player_socket = (current_round == Round.Client1) ? client1 : client2;
        Socket opponent_socket = (current_round == Round.Client1) ? client2 : client1;
        Deck player_deck = (current_round == Round.Client1) ? client1_deck : client2_deck;
        Deck opponent_deck = (current_round == Round.Client1) ? client2_deck : client1_deck;

        // 让当前玩家抽两张牌，获得一个法力水晶
        ArrayList<String> draw_card = player_deck.DrawCard(2);
        for (String card : draw_card)
            SendAndWaitAck(player_socket, "PlayerDrawCard " + card);
        SendAndWaitAck(opponent_socket, "OpponentDrawCard 2");
        SendAndWaitAck(player_socket, "IncreasePlayerHeroMp");
        SendAndWaitAck(opponent_socket, "IncreaseOpponentHeroMp");
        SendAndWaitAck(player_socket, "End");

        // 等待client的信号，直到按下结束按钮
        String msg;
        while (!((msg = ReceiveAndAck(player_socket)).equals("EndRound"))) {
            ProcessClientMsg(msg);
        }
        current_round = (current_round == Round.Client1) ? Round.Client2 : Round.Client1;
        SendAndWaitAck(opponent_socket, "PlayerRound");
    }

    private void ProcessClientMsg(String msg) {
        Socket player_socket = (current_round == Round.Client1) ? client1 : client2;
        Socket opponent_socket = (current_round == Round.Client1) ? client2 : client1;
        ArrayList<String> msg_phase = new ArrayList<String>(Arrays.asList(msg.split("\\s+")));
        String command = msg_phase.get(0);
        String arg1 = null, arg2 = null;
        if (msg_phase.size() > 1) arg1 = msg_phase.get(1);
        if (msg_phase.size() > 2) arg2 = msg_phase.get(2);
        /**
         * 所有的客户端消息
         * EndRound
         * PlayerAddServant [NAME] [ID]
         * ConsumePlayerHeroHp [Hp], ConsumePlayerHeroMp [Mp],
         * RestorePlayerHeroHp [Hp], RestorePlayerHeroMp [Mp],
         * ConsumeOpponentHeroHp [Hp], ConsumeOpponentHeroMp [Mp],
         * RestoreOpponentHeroHp [Hp], RestoreOpponentHeroMp [Mp],
         * ServantConsumeHp [ID] [NUM], ServantRestoreHp [ID] [NUM]
         * PlayerLoseServant [ID], OpponentLoseServant [ID]
         * PlayerWin, OpponentWin
         * PlayerUseCard
         */
        if (command.equals("PlayerAddServant")) {
            SendAndWaitAck(opponent_socket, "OpponentAddServant " + arg1 + " " + arg2);
        } else if (command.equals("ConsumePlayerHeroHp")) {
            SendAndWaitAck(opponent_socket, "ConsumeOpponentHeroHp " + arg1);
        } else if (command.equals("ConsumePlayerHeroMp")) {
            SendAndWaitAck(opponent_socket, "ConsumeOpponentHeroMp " + arg1);
        } else if (command.equals("RestorePlayerHeroHp")) {
            SendAndWaitAck(opponent_socket, "RestoreOpponentHeroHp " + arg1);
        } else if (command.equals("RestorePlayerHeroMp")) {
            SendAndWaitAck(opponent_socket, "RestoreOpponentHeroMp " + arg1);
        } else if (command.equals("ConsumeOpponentHeroHp")) {
            SendAndWaitAck(opponent_socket, "ConsumePlayerHeroHp " + arg1);
        } else if (command.equals("ConsumeOpponentHeroMp")) {
            SendAndWaitAck(opponent_socket, "ConsumePlayerHeroMp " + arg1);
        } else if (command.equals("RestoreOpponentHeroHp")) {
            SendAndWaitAck(opponent_socket, "RestorePlayerHeroHp " + arg1);
        } else if (command.equals("RestoreOpponentHeroMp")) {
            SendAndWaitAck(opponent_socket, "RestorePlayerHeroMp " + arg1);
        } else if (command.equals("PlayerUseCard")) {
            SendAndWaitAck(opponent_socket, "OpponentUseCard");
        } else if (command.equals("ServantConsumeHp") || command.equals("ServantRestoreHp")) {
            SendAndWaitAck(opponent_socket, msg);
        } else if (command.equals("PlayerLoseServant")) {
            SendAndWaitAck(opponent_socket, "OpponentLoseServant " + arg1);
        } else if (command.equals("OpponentLoseServant")) {
            SendAndWaitAck(opponent_socket, "PlayerLoseServant " + arg1);
        } else if (command.equals("OpponentWin")) {
            SendAndWaitAck(opponent_socket, "PlayerWin");
        } else if (command.equals("PlayerWin")) {
            SendAndWaitAck(opponent_socket, "OpponentWin");
        }
    }

    private void SendAndWaitAck(Socket socket, String msg) {
        Send(socket, msg);
        Receive(socket);
    }

    private String ReceiveAndAck(Socket socket) {
        String response = Receive(socket);
        Send(socket, "OK");
        return response;
    }

    private String Receive(Socket socket) {
        try {
            if (socket != null) {
                InputStream socket_in = socket.getInputStream();
                int available = socket_in.available();
                while (available == 0) available = socket_in.available();
                byte buffer[] = new byte[available];
                socket_in.read(buffer);
                String response = new String(buffer, "utf-8");
                System.out.println("[RECEIVE] " + socket.getInetAddress() + ":" + socket.getPort() + " " + response);
                return response;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void Send(Socket socket, String msg) {
        try {
            if (socket != null) {
                OutputStream socket_out = socket.getOutputStream();
                socket_out.write(msg.getBytes("utf-8"));
                socket_out.flush();
                System.out.println("[SEND] " + socket.getInetAddress() + ":" + socket.getPort() + " " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Start() {
        try {
            InitState();
            while (current_round != Round.GameOver) {
                ProcessClient();
            }
        } catch (IOException e) {
            // TODO 发生错误双方停止对战
            e.printStackTrace();
        }
    }
}
