package com.welles;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private CopyOnWriteArrayList<Socket> wait_clients = new CopyOnWriteArrayList<>(); // 等待队列中的socket, 线程安全
    private CopyOnWriteArrayList<Session> sessions = new CopyOnWriteArrayList<>(); // 线程安全
    public void listen(int port) {
        try {
            // 监听 port 端口
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                // 接受连接
                Socket client = serverSocket.accept();
                if (wait_clients.size() >= 1) {
                    // 匹配上双方，开始游戏
                    Socket client2 = wait_clients.remove(0);
                    Session session = new Session(client, client2);
                    sessions.add(session);
                    // 创建新线程，发送数据
                    new Thread(() -> {
                        new Game(session).Start();
                    }).start();
                } else {
                    wait_clients.add(client);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Server server = new Server();
        server.listen(80);
    }
}
