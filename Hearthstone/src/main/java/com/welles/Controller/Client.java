package com.welles.Controller;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {
    private Socket client_socket;
    private static Client client = new Client();

    private Client() {
    }

    public static Client getClient() {
        return client;
    }

    public void Init() {
        try {
            client_socket = new Socket(InetAddress.getLocalHost(), 80);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String Receive() {
        try {
            if (client_socket != null) {
                InputStream socket_in = client_socket.getInputStream();
                int available = socket_in.available();
                while (available == 0) available = socket_in.available();
                byte buffer[] = new byte[available];
                socket_in.read(buffer);
                String response = new String(buffer, "utf-8");
                System.out.println("[RECEIVE] " + response);
                return response;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void Send(String msg) {
        try {
            if (client_socket != null) {
                OutputStream socket_out = client_socket.getOutputStream();
                socket_out.write(msg.getBytes("utf-8"));
                socket_out.flush();
                System.out.println("[SEND] " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendAndWaitAck(String msg) {
        Send(msg);
        Receive();
    }

    public String ReceiveAndAck() {
        String response = Receive();
        Send("OK");
        return response;
    }

    /**
     * 从客户端接受的内容都是以属性加冒号开头的 之后跟上若干参数，以空格为分界
     * PlayerHeroHp 30 / Player
     * @param input
     */
    public static ArrayList<String> Parse(String input) {
        return new ArrayList<String>(Arrays.asList(input.split("\\s+")));
    }
}
