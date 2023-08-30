import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

public class GameListPanel extends JPanel {

    private class MyActionListener implements ActionListener {

        private int counter;

        public MyActionListener(int i) {
            counter = i;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (client.joinGame(serverOutput.get(counter).split(" ")[1])) {
                    launchGame();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private Client client;
    private List<String> serverOutput;
    
    public GameListPanel(Client client) {
        this.client = client;
        serverOutput = client.getServerOutput();
        setBackground(Color.decode("#f5f0e1"));
        BoxLayout bl = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        this.setLayout(bl);
    }

    public void showGameList() {
        removeAll();
        serverOutput = client.getServerOutput();
        JLabel jlabel = new JLabel("Number of games : " + serverOutput.size());
        add(jlabel);
        JButton button;
        String gameName;
        String playerNb;
        for (int i = 0; i < serverOutput.size(); i++) {
            add(Box.createRigidArea(new Dimension(0, 15)));
            gameName = serverOutput.get(i).split(" ", 2)[1];
            System.out.println(gameName);
            playerNb = serverOutput.get(i).split(" ")[0];
            button = new JButton(gameName + " players : " + playerNb);
            button.addActionListener(new MyActionListener(i));
            add(button);
        }
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        JButton createGame = new JButton("Create a game");
        createGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String gameName = JOptionPane.showInputDialog("Enter the game's name : ");
                try {
                    if(!(gameName == null || (gameName != null && ("".equals(gameName)))))   
                    {
                        if (client.createGame(gameName)) {
                            launchGame();
                    }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }     
            }
        });
        add(createGame);
        revalidate();
        repaint();
    }

    public void launchGame() {
        JPanel game = new Game(client);
        GameFrame.getInstance().setContentPane(game);
        game.repaint();
        game.revalidate();
    }

}
