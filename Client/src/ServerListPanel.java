import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.net.InetAddress;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ServerListPanel extends JPanel{
    
    public ServerListPanel(Client client) {
        setBackground(Color.decode("#1e3d59"));
        BoxLayout bl = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        this.setLayout(bl);
        refreshServerList(client);
    }

    public void refreshServerList(Client client) {
        removeAll();
        JLabel jlabel = new JLabel("List of available servers :");
        jlabel.setAlignmentX(CENTER_ALIGNMENT);
        jlabel.setForeground(Color.WHITE);
        add(jlabel);
        JButton refresh = new JButton("refresh");
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshServerList(client);
            }
        });
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        add(refresh);
        List<InetAddress> list = client.searchServer(); // List of servers
        JButton button;
        for (InetAddress inetAddress : list) {
            this.add(Box.createRigidArea(new Dimension(0, 15)));
            button = new JButton(inetAddress.toString());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    client.connectServer(inetAddress.getHostAddress());
                    ((ServerPanel) getParent()).showGameList();
                }
            });
            button.setAlignmentX(CENTER_ALIGNMENT);
            add(button);
        }
        revalidate();
        repaint();
    }
}
