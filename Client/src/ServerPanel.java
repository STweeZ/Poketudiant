import javax.swing.JPanel;
import java.awt.GridLayout;

public class ServerPanel extends JPanel {
    private GameListPanel gameList;
    
    public ServerPanel(Client client) {
        GridLayout gl = new GridLayout();
        gl.setHgap(5);
        setLayout(gl);
        JPanel serverList = new ServerListPanel(client);
        gameList = new GameListPanel(client);
        add(serverList);
        add(gameList);
    }

    public void showGameList() {
        gameList.showGameList();
        revalidate();
        repaint();
    } 

}
