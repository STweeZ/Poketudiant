import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import java.net.SocketTimeoutException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private DatagramSocket socket;
	private DatagramPacket receive;
	private DatagramPacket send;
	private Socket s;
    private InetAddress address;
	private PrintWriter writer;
	private BufferedReader reader;
	private List<InetAddress> listOfInetAddress;
	private List<String> serverOutput;
	private Chat chat;
	private Map map;
	private Team team;
	private Encounter encounter;
	private boolean inGame;
	private static final String SEARCH_SERVER = "looking for poketudiant servers";
	private static final String ANSWER_SEARCH_SERVER = "i'm a poketudiant server";
    private final static int PORTUDP = 9000;
    private final static int PORTTCP = 9001;
    private final static int SIZE = 500;

    public Client(String hostname) throws IOException {
		serverOutput = new ArrayList<>();
        socket = new DatagramSocket();
        address = InetAddress.getByName(hostname);
        socket.setBroadcast(true);
		listOfInetAddress = new ArrayList<InetAddress>();
		send = new DatagramPacket(SEARCH_SERVER.getBytes(), SEARCH_SERVER.getBytes().length, address, PORTUDP);
		receive = new DatagramPacket(new byte[SIZE], SIZE, address, PORTUDP);
    }

	// use UDP to find list of available servers
	public List<InetAddress> searchServer() {
		try {
			listOfInetAddress.clear();
			socket.send(send);
			socket.setSoTimeout(750);

			while (true) {
				socket.receive(receive);
				byte[] msgServer = receive.getData();
				String response = new String(msgServer, 0, ANSWER_SEARCH_SERVER.length());
				System.out.println(response);
				if (ANSWER_SEARCH_SERVER.equals(response)) listOfInetAddress.add(receive.getAddress());
			}

		} catch (SocketTimeoutException e) {
			return listOfInetAddress;
		} catch (IOException e) {
			if (listOfInetAddress.isEmpty()) {
				System.err.println("Pas de serveur");
			}
			socket.close();
		}
		return Collections.emptyList();
	}

	public void quit() throws IOException {
		s.close();
	}

	// open connection to server
	public void connectServer(String hostname) {
		try {
			if (s != null) quit();
			serverOutput.clear();
			s = new Socket(InetAddress.getByName(hostname), PORTTCP);
			OutputStream output = s.getOutputStream();
			InputStream input = s.getInputStream();

			writer = new PrintWriter(output, true);
			reader = new BufferedReader(new InputStreamReader(input));

			writer.println("require game list");
			writer.flush();

			String str = reader.readLine();
			System.out.println(str); // Print the number of games
			if(str.contains("number of games")) {
				int gamesNb = Integer.parseInt(str.split(" ")[3]);
				for (int i = 0; i < gamesNb; i++) {
					str = reader.readLine();
					serverOutput.add(str);
				}
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// listen input from server
	public void listenToServer() throws IOException {
		inGame = true;
		String str;
		String firstr;
		String rival;
		String[] lStrings;
		String msg;
		while (inGame) {
			map.requestFocus();
			System.out.println("listening");
			str = reader.readLine();
			System.out.println(str);
			firstr = str.split(" ", 2)[0];
			if (firstr.equals("map")) readMap(str);
			else if (str.contains("rival message")) {
				lStrings = str.split(" ", 4); 
				rival = lStrings[2];
				msg = lStrings[3];
				chat.receiveMessage(rival, msg);
			}
			else if (str.contains("team")) receiveTeam(str);
			else if (firstr.equals("encounter")) encounter(str.split(" ", 2)[1]); // remove encounter from the string 
		}
	}

	public void encounter(String str) {
		String next = str.split(" ")[0];
		if (str.split(" ").length > 1) str = str.split(" ", 2)[1];
		switch(next) {
			case "new":
				next = str.split(" ")[0];
				encounter.setNbPokmn(team.getNbPokmn());
				map.setVisible(false);
				switch(next) {
					case "wild":
						encounter.startFight(Integer.parseInt(str.split(" ")[1]), false);
						break;

					case "rival":
						encounter.startFight(Integer.parseInt(str.split(" ")[1]), true);
						break;
				}
				break;

			case "win":
				JOptionPane.showConfirmDialog(encounter, "You won the battle", "Victory!", JOptionPane.PLAIN_MESSAGE);
				map.setVisible(true);
				encounter.endEncounter();
				map.requestFocus();
				break;

			case "lose":
				JOptionPane.showConfirmDialog(encounter, "You lost the battle...", "Defeat", JOptionPane.PLAIN_MESSAGE);
				map.setVisible(true);
				encounter.endEncounter();
				map.requestFocus();
				break;

			case "escape":
				next = str.split(" ")[0];
				switch(next) {
					case "ok":
						JOptionPane.showConfirmDialog(encounter, "You successfully escaped!", "Escape", JOptionPane.PLAIN_MESSAGE);
						map.setVisible(true);
						encounter.endEncounter();
						map.requestFocus();
						break;
					
					case "fail":
						JOptionPane.showConfirmDialog(encounter, "You failed to escape...", "Escape", JOptionPane.PLAIN_MESSAGE);
						break;
				}
				break;

			case "enter":
				next = str.split(" ")[0];
				switch(next) {
					case "action":
						encounter.waitAction();
						break;
					
					case "poketudiant":
						JOptionPane.showConfirmDialog(encounter, "Choose a poketudiant in your team to send ", "Swap", JOptionPane.PLAIN_MESSAGE);
						team.resetSelectedPokmn();
						while (team.getSelectedPokmnIndex() == -1) System.out.println(team.getSelectedPokmnIndex());
						sendIndex(team.getSelectedPokmnIndex());
						team.resetSelectedPokmn();
						break;
				}
				break;

			case "poketudiant":
				next = str.split(" ")[0];
				String variety;
				switch(next) {
					case "xp":
						String xp = str.split(" ")[2];
						variety = team.getPokmnName(Integer.parseInt(str.split(" ")[1]));
						JOptionPane.showMessageDialog(encounter, "Your poketudiant ".concat(variety).concat(" gained ").concat(xp).concat(" xp"), "XP", JOptionPane.PLAIN_MESSAGE);
						break;

					case "level":
						String lvl = str.split(" ")[2];
						variety = team.getPokmnName(Integer.parseInt(str.split(" ")[1]));
						JOptionPane.showMessageDialog(encounter, "Your poketudiant ".concat(variety).concat(" gained ").concat(lvl).concat(" level(s)"), "Level up", JOptionPane.PLAIN_MESSAGE);
						break;

					case "evolution":
						String evo = str.split(" ")[2];
						variety = team.getPokmnName(Integer.parseInt(str.split(" ")[1]));
						JOptionPane.showMessageDialog(encounter, "Your poketudiant ".concat(variety).concat(" evolved in ").concat(evo), "Evolution", JOptionPane.PLAIN_MESSAGE);
						break;

					case "player":
						encounter.setInfo(str.split(" ")[1], str.split(" ")[2], str.split(" ")[3], str.split(" ")[4], str.split(" ")[5], str.split(" ")[6], str.split(" ")[7]);
						break;

					case "opponent":
						encounter.setInfo(str.split(" ")[1], str.split(" ")[2], str.split(" ")[3]);
						break;
				}
				break;

			case "catch":
				next = str.split(" ")[0];
				System.out.println(next);
				switch(next) {
					case "ok":
						JOptionPane.showConfirmDialog(encounter, "The poketudiant was captured successfully!", "Capture", JOptionPane.PLAIN_MESSAGE);
						map.setVisible(true);
						encounter.endEncounter();
						map.requestFocus();
						break;
					
					case "fail":
						JOptionPane.showConfirmDialog(encounter, "Capture failed...", "Capture", JOptionPane.PLAIN_MESSAGE);
						break;
				}
				break;

			default: 
				System.out.println("invalid");
				break;
		}
	}

	public boolean createGame(String gameName) throws IOException {
		writer.println("create game ".concat(gameName));
		writer.flush();
		return reader.readLine().equals("game created");
	}

	public boolean joinGame(String gameName) throws IOException {
		writer.println("join game ".concat(gameName));
		writer.flush();
		return reader.readLine().equals("game joined");
	}

	public void readMap(String str) throws IOException {
		emptyList();
		//System.out.println(str);
		int width = Integer.parseInt(str.split(" ")[1]);
		int height = Integer.parseInt(str.split(" ")[2]);
		for (int i = 0; i < height; i++) {
			serverOutput.add(reader.readLine());
			// System.out.println(serverOutput.get(i));
		}
		map.drawMap(width);
	}

	public void sendMessage(String msg) {
		//if (msg.equals("")) return;
		writer.println("send message ".concat(msg));
		writer.flush();
	}

	public void sendIndex(int index) {
		System.out.println(index);
		writer.println("encounter poketudiant index ".concat(Integer.toString(index)));
		writer.flush();
	}

	public void swap() {
		if (team.getNbPokmn() <= 1) {
			JOptionPane.showConfirmDialog(encounter, "You only have one poketudiant...", "Switch", JOptionPane.PLAIN_MESSAGE);
			encounter.waitAction();
			return;
		}
		System.out.println("switch");
		writer.println("encounter action switch");
		writer.flush();
	}

	public void receiveTeam(String str) throws IOException {
		ArrayList<Poketudiant> poketudiants = new ArrayList<Poketudiant>();
		String poke;
		String[] lStrings;
		for (int i = 0; i < Integer.parseInt(str.split(" ")[2]); i++) {
			poke = reader.readLine();
			System.out.println(poke);
			lStrings = poke.split(" ");
			poketudiants.add(new Poketudiant(lStrings[0], lStrings[1], Integer.parseInt(lStrings[2]), Integer.parseInt(lStrings[3]),
			Integer.parseInt(lStrings[4]), Integer.parseInt(lStrings[5]), Integer.parseInt(lStrings[6]), Integer.parseInt(lStrings[7]),
			Integer.parseInt(lStrings[8]), lStrings[9], lStrings[10], lStrings[11], lStrings[12]));
		}
		team.drawTeam(poketudiants);
	}

	public void attack(String attack) {
		writer.println("encounter action ".concat(attack));
		writer.flush();
	}

	public void movePoketudiant(int pos, Direction direction) {
		// System.out.println("poketudiant " + pos + " move ".concat(direction.label));
		writer.println("poketudiant " + pos + " move ".concat(direction.label));
		writer.flush();
	}

	public void freePoketudiant(int pos) {
		writer.println("poketudiant " + pos + " free");
		writer.flush();
	}

	public void move(Direction direction) {
		writer.println("map move ".concat(direction.label));
		System.out.println("map move ".concat(direction.label));
		writer.flush();
	}

	public void escape() {
		writer.println("encounter action leave");
		writer.flush();
	}

	public void capture() {
		if (team.getNbPokmn() >= 3) {
			encounter.waitAction();
			JOptionPane.showConfirmDialog(encounter, "You can't capture any more poketudiant, you need to free one poketudiant", "Capture", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		writer.println("encounter action catch");
		writer.flush();
	}

	public void emptyList() {
		serverOutput.clear();
	}

	public List<String> getServerOutput() {
		return this.serverOutput;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

    public void close() {
        socket.close();
    }
}