import java.io.IOException;
import javax.swing.JPanel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Game extends JPanel {
    
    public Game(Client client) {
        client.emptyList(); // clear the list
        // client.setGame(this);
        GameFrame.getInstance().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                client.close();
            }
        });
        Map map = new Map(client); 
        Chat chat = new Chat(client);
        Team team = new Team(client);
        Encounter encounter = new Encounter(client);
        encounter.setVisible(false);
        client.setEncounter(encounter);
        add(team); // add the team on the panel
        add(encounter); // add the encounter on the panel
        add(map); // add the map on the panel
        add(chat); // add the chat on the panel
        startGame(client);
    }

    public synchronized void startGame(Client client) {
        Thread daemon = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.listenToServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }     
            }
        }, "Demon");
        daemon.setDaemon(true);
        daemon.start();
    }
}
