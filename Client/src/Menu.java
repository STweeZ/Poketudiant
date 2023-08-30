import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.Graphics;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Menu extends JPanel {
    
    public Menu() {

        BoxLayout bl = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        this.setLayout(bl);
        JButton serverList = new JButton("Server list");
        serverList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerPanel servPane;
                try {
                    servPane = new ServerPanel(new Client("255.255.255.255"));
                    GameFrame.getInstance().setContentPane(servPane);                    
                    servPane.revalidate();
                    servPane.repaint();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JButton quit = new JButton("Quit");
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        quit.setAlignmentX(CENTER_ALIGNMENT);
        serverList.setAlignmentX(CENTER_ALIGNMENT);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        this.add(Box.createRigidArea(new Dimension(0, (screensize.height / 2) - 50 * 2)));
        // set buttons size even though it's not its true purpose
        serverList.add(Box.createRigidArea(new Dimension(200, 50)));
        quit.add(Box.createRigidArea(new Dimension(200, 50)));
        this.add(serverList);
        // create a gap between the buttons
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        this.add(quit);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
            ImageIcon imageIcon = new ImageIcon(ImageIO.read(new File("assets/background.png")));
            Image image = imageIcon.getImage();
            Image rescaled = image.getScaledInstance(screensize.width, screensize.height, Image.SCALE_SMOOTH);
            g.drawImage(rescaled, 0, 0, screensize.width, screensize.height, this);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}