package com.welles.View;

import com.welles.Model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class View extends JPanel {
    private Image bg_closed = new ImageIcon(View.class.getClassLoader().getResource("board/bg_closed.png")).getImage();
    private Image bg_waiting = new ImageIcon(View.class.getClassLoader().getResource("board/bg_waiting.png")).getImage();
    private Image bg_undergoing = new ImageIcon(View.class.getClassLoader().getResource("board/bg_undergoing.png")).getImage();
    private Image bg_player_win = new ImageIcon(View.class.getClassLoader().getResource("board/bg_player_win.png")).getImage();
    private Image bg_opponent_win = new ImageIcon(View.class.getClassLoader().getResource("board/bg_opponent_win.png")).getImage();
    private Model model;
    private MyPanel opponent_card = new MyPanel();
    private MyPanel opponent_hero = new MyPanel();
    private MyPanel opponent_servant = new MyPanel();
    private MyPanel player_card = new MyPanel();
    private MyPanel player_hero = new MyPanel();
    private MyPanel player_servant = new MyPanel();
    private MyPanel concede = new MyPanel();
    private MyPanel end_round = new MyPanel();

    public MyPanel GetOpponentCardView() {
        return opponent_card;
    }

    public MyPanel GetOpponentHeroView() {
        return opponent_hero;
    }

    public MyPanel GetOpponentServantView() {
        return opponent_servant;
    }

    public MyPanel GetPlayerHeroView() {
        return player_hero;
    }

    public MyPanel GetPlayerCardView() {
        return player_card;
    }

    public MyPanel GetPlayerServantView() {
        return player_servant;
    }

    public MyPanel GetConcedeView() {
        return concede;
    }

    public MyPanel GetEndRoundView() {
        return end_round;
    }

    public View() {
        // add all the components
        add(opponent_hero);
        add(opponent_servant);
        add(opponent_card);
        add(player_card);
        add(player_hero);
        add(player_servant);
        add(concede);
        add(end_round);
        // set overview GridBag layout
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        // create GridBagConstrains
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Add Stuff
        MyPanel s00 = new MyPanel();
        add(s00);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbl.setConstraints(s00, gbc);

        // set Opponent Card
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.weightx = 3;
        gbc.weighty = 1;
        gbl.setConstraints(opponent_card, gbc);

        // Add Stuff
        MyPanel s40 = new MyPanel();
        add(s40);
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbl.setConstraints(s40, gbc);

        // Add Stuff
        MyPanel s02 = new MyPanel();
        add(s02);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 1;
        gbc.weighty = 2;
        gbl.setConstraints(s02, gbc);

        // Set Opponent Hero
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 5;
        gbc.gridheight = 1;
        gbc.weightx = 5;
        gbc.weighty = 1;
        gbl.setConstraints(opponent_hero, gbc);

        // Set Opponent Servant
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.weightx = 3;
        gbc.weighty = 1;
        gbl.setConstraints(opponent_servant, gbc);

        // Add Stuff
        MyPanel s05 = new MyPanel();
        add(s05);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbl.setConstraints(s05, gbc);

        // set Player Card
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.weightx = 3;
        gbc.weighty = 1;
        gbl.setConstraints(player_card, gbc);

        // Set Player Hero
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 5;
        gbc.gridheight = 1;
        gbc.weightx = 5;
        gbc.weighty = 1;
        gbl.setConstraints(player_hero, gbc);

        // Set Player Servant
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.weightx = 3;
        gbc.weighty = 1;
        gbl.setConstraints(player_servant, gbc);

        // Set Concede
        gbc.gridx = 4;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbl.setConstraints(concede, gbc);

        // Set EndRound
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 1;
        gbc.weighty = 2;
        gbl.setConstraints(end_round, gbc);
    }

    public void SetModel(Model model) {
        this.model = model;
        if (this.model != null) {
            // 开始绑定model事件处理函数
            model.GetPlayer().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (e.getActionCommand().equals("DrawCard") || e.getActionCommand().equals("UseCard")) {
                                UpdatePlayerCard();
                            } else if (e.getActionCommand().equals("SetHp") || e.getActionCommand().equals("SetMp")) {
                                UpdatePlayerHpMp();
                            } else if (e.getActionCommand().equals("AddServant") || e.getActionCommand().equals("LoseServant")) {
                                UpdatePlayerServants();
                            }
                        }
                    });

                }
            });
            model.GetOpponent().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (e.getActionCommand().equals("SetOpponentHp") || e.getActionCommand().equals("SetOpponentMp")) {
                                UpdateOpponentHpMp();
                            } else if (e.getActionCommand().equals("OpponentDrawCard") || e.getActionCommand().equals("OpponentUseCard")) {
                                UpdateOpponentCard();
                            } else if (e.getActionCommand().equals("OpponentAddServant") || e.getActionCommand().equals("OpponentLoseServant")) {
                                UpdateOpponentServants();
                            }
                        }
                    });
                }
            });
            model.GetGameState().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (e.getActionCommand().equals("ChangeGamePhase")) {
                                paintImmediately(0, 0, getWidth(), getHeight());
                            } else if (e.getActionCommand().equals("ChangeRound")) {
                                UpdateEndRound();
                                paintImmediately(0, 0, getWidth(), getHeight());
                            }
                        }
                    });
                }
            });
            Servant.SetActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (e.getActionCommand().equals("PlayerServantConsumeHp") || e.getActionCommand().equals("PlayerServantConsumeMp")) {
                                UpdatePlayerServants();
                            } else if (e.getActionCommand().equals("OpponentServantConsumeHp") || e.getActionCommand().equals("OpponentServantConsumeMp")) {
                                UpdateOpponentServants();
                            }
                        }
                    });
                }
            });
        }
    }

    public Model GetModel() {
        return model;
    }

    private void UpdatePlayerCard() {
        player_card.removeAll();
        FlowLayout fl = new FlowLayout();
        fl.setHgap(0);
        player_card.setLayout(fl);
        for (Card card : GetModel().GetPlayer().GetCardsHand()) {
            ImageIcon icon = new ImageIcon(View.class.getClassLoader().getResource("cards/" + card.GetResourceAddr()));
            icon.setImage(icon.getImage().getScaledInstance(-1, player_card.getHeight(), Image.SCALE_DEFAULT));
            CardView card_hand = new CardView(icon);
            card_hand.SetCardName(card.GetCardName());
            if (player_card.mouse_listener != null) {
                card_hand.addMouseListener(player_card.mouse_listener);
            }
            player_card.add(card_hand);
        }
        player_card.validate();
        player_card.repaint();
    }

    private void UpdateOpponentCard() {
        opponent_card.removeAll();
        FlowLayout fl = new FlowLayout();
        fl.setHgap(0);
        opponent_card.setLayout(fl);
        for (int i = 0; i < GetModel().GetOpponent().GetCardsHand(); i++) {
            ImageIcon icon = new ImageIcon(View.class.getClassLoader().getResource("cards/Legendary.png"));
            icon.setImage(icon.getImage().getScaledInstance(-1, opponent_card.getHeight(), Image.SCALE_DEFAULT));
            CardView card_hand = new CardView(icon);
            opponent_card.add(card_hand);
        }
        opponent_card.validate();
        opponent_card.repaint();
    }

    private void UpdatePlayerHpMp() {
        player_hero.removeAll();
        player_hero.setBounds(this.getWidth() / 5 * 2, this.getHeight() / 3 * 2, this.getWidth() / 5, this.getHeight() / 6);
        player_hero.setLayout(new BorderLayout());
        JTextField tf = new JTextField("HP: " + GetModel().GetPlayer().GetHp() + " MP: " + GetModel().GetPlayer().GetMp());
        tf.setHorizontalAlignment(JTextField.CENTER);
        player_hero.add(tf, BorderLayout.NORTH);
        player_hero.add(new JButton("英雄技能"), BorderLayout.SOUTH);
        player_hero.validate();
        player_hero.repaint();
    }

    private void UpdateOpponentHpMp() {
        opponent_hero.removeAll();
        opponent_hero.setBounds(this.getWidth() / 5 * 2, this.getHeight() / 6, this.getWidth() / 5, this.getHeight() / 6);
        opponent_hero.setLayout(new BorderLayout());
        JTextField tf = new JTextField("HP: " + GetModel().GetOpponent().GetHp() + " MP: " + GetModel().GetOpponent().GetMp());
        tf.setHorizontalAlignment(JTextField.CENTER);
        opponent_hero.add(tf, BorderLayout.SOUTH);
        opponent_hero.validate();
        opponent_hero.repaint();
    }

    private void UpdatePlayerServants() {
        player_servant.removeAll();
        for (Servant s : GetModel().GetPlayer().GetServants()) {
            String icon_path = Card.CardPool.get(s.GetName()).GetResourceAddr();
            ImageIcon icon = new ImageIcon(View.class.getClassLoader().getResource("cards/" + icon_path));
            icon.setImage(icon.getImage().getScaledInstance(-1, player_servant.getHeight() / 3 * 2, Image.SCALE_DEFAULT));
            CardView servant_view = new CardView(icon);
            servant_view.SetCardName(s.GetName());
            servant_view.SetId(s.GetPrivateId());
            servant_view.setText("HP: " + s.GetHp() + " Attack: " + s.GetAttack());
            servant_view.setVerticalTextPosition(JLabel.BOTTOM);
            servant_view.setHorizontalTextPosition(JLabel.CENTER);
            if (player_servant.mouse_listener != null) {
                servant_view.addMouseListener(player_servant.mouse_listener);
            }
            player_servant.add(servant_view);
        }
        player_servant.validate();
        player_servant.repaint();
    }

    private void UpdateOpponentServants() {
        opponent_servant.removeAll();
        for (Servant s : GetModel().GetOpponent().GetServants()) {
            String icon_path = Card.CardPool.get(s.GetName()).GetResourceAddr();
            ImageIcon icon = new ImageIcon(View.class.getClassLoader().getResource("cards/" + icon_path));
            icon.setImage(icon.getImage().getScaledInstance(-1, opponent_servant.getHeight() / 3 * 2, Image.SCALE_DEFAULT));
            CardView servant_view = new CardView(icon);
            servant_view.SetCardName(s.GetName());
            servant_view.SetId(s.GetPrivateId());
            servant_view.setText("HP: " + s.GetHp() + " Attack: " + s.GetAttack());
            servant_view.setVerticalTextPosition(JLabel.BOTTOM);
            servant_view.setHorizontalTextPosition(JLabel.CENTER);
            if (opponent_servant.mouse_listener != null) {
                servant_view.addMouseListener(opponent_servant.mouse_listener);
            }
            opponent_servant.add(servant_view);
        }
        opponent_servant.validate();
        opponent_servant.repaint();
    }

    private void UpdateEndRound() {
        end_round.removeAll();
        end_round.setLayout(new BorderLayout());
        ImageIcon icon = null;
        if (GetModel().GetGameState().GetCurrentRound() == Round.Player) {
            icon = new ImageIcon(View.class.getClassLoader().getResource("button/EndTurn.png"));
        } else if (GetModel().GetGameState().GetCurrentRound() == Round.Opponent) {
            icon = new ImageIcon(View.class.getClassLoader().getResource("button/OpponentTurn.png"));
        }
        if (icon != null) {
            icon.setImage(icon.getImage().getScaledInstance(-1, end_round.getHeight() / 3, Image.SCALE_DEFAULT));
            CardView end_round_view = new CardView(icon);
            end_round.add(end_round_view, BorderLayout.CENTER);
        }
        end_round.validate();
        end_round.repaint();
    }

    public class MyPanel extends JPanel {
        MouseListener mouse_listener;

        MyPanel() {
            setOpaque(false);
        }

        public void SetMouseListener(MouseListener mouse_listener) {
            this.mouse_listener = mouse_listener;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (GetModel().GetGameState().GetGamePhase() == GamePhase.Closed) {
            g.drawImage(bg_closed, 0, 0, this.getWidth(), this.getHeight(), this);
        } else if (GetModel().GetGameState().GetGamePhase() == GamePhase.WaitForOpponent) {
            g.drawImage(bg_waiting, 0, 0, this.getWidth(), this.getHeight(), this);
        } else if (GetModel().GetGameState().GetGamePhase() == GamePhase.Undergoing) {
            if (GetModel().GetGameState().GetCurrentRound() == Round.Player) {
                g.drawImage(bg_undergoing, 0, 0, this.getWidth(), this.getHeight(), this);
            } else {
                g.drawImage(bg_waiting, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        } else if (GetModel().GetGameState().GetGamePhase() == GamePhase.PlayerWin) {
            g.drawImage(bg_player_win, 0, 0, this.getWidth(), this.getHeight(), this);
        } else if (GetModel().GetGameState().GetGamePhase() == GamePhase.OpponentWin) {
            g.drawImage(bg_opponent_win, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}