package com.welles;

import com.welles.Controller.Controller;
import com.welles.Model.GamePhase;
import com.welles.Model.Model;
import com.welles.View.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Main {
    public static final String game_name = "炉石传说";
    private JFrame frame = new JFrame(game_name);
    private Model model = new Model();
    private View view = new View();
    private Controller controller = new Controller();

    public void Init() {
        // operation to do when the window is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        view.setPreferredSize(new Dimension(1536, 798));
        frame.setContentPane(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);

        controller.SetModel(model);
        controller.SetView(view);
        controller.AddViewListener(); // listen to mouse event

        view.SetModel(model);
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main main = new Main();
                main.Init();
            }
        });
    }
}
