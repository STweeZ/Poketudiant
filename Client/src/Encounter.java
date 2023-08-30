import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;

public class Encounter extends JPanel {

    private JButton escape;
    private JButton capture;
    private JButton swap;
    private JButton attack1;
    private JButton attack2;
    private JLabel nbPokmn;
    private JLabel nbPokmnRival;
    private JLabel lvlRival;
    private JLabel lvl;
    private JLabel hpRival;
    private JLabel hp;
    private JLabel imageRival;
    private JLabel image;
    private boolean rival;

    public Encounter(Client client) {
        GridLayout gl = new GridLayout(7,2);
        gl.setHgap(10);
        this.setLayout(gl);

        attack1 = new JButton("attack1 \n type");
        attack2 = new JButton("attack2 \n type");
        nbPokmn = new JLabel("nbPokmn");
        nbPokmnRival = new JLabel("nbPokmnRival");
        lvl = new JLabel("lvl");
        lvlRival = new JLabel("lvlRival");
        hp = new JLabel("XX %");
        hpRival = new JLabel("XX %");
        image = new JLabel("image");
        imageRival = new JLabel("imageRival");
        rival = false;

        escape = new JButton("Escape");
        escape.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                waitNextTurn();
                client.escape();
            }
        });

        capture = new JButton("Catch");
        capture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                waitNextTurn();
                client.capture();
            }
        });

        swap = new JButton("Switch");
        swap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                waitNextTurn();
                client.swap();
            }
        });

        attack1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                waitNextTurn();
                client.attack("attack1");
            }
        });

        attack2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                waitNextTurn();
                client.attack("attack2");
            }
        });

        // the order of adding is important for the display
        add(nbPokmnRival);
        add(image);
        add(imageRival);
        add(lvl);
        add(lvlRival);
        add(hp);
        add(hpRival);
        add(nbPokmn);
        add(attack1);
        add(attack2);
        add(swap);
        add(capture);
        add(escape);

        //disable buttons 
        attack1.setEnabled(false);
        attack2.setEnabled(false);
        this.escape.setEnabled(false);
        this.capture.setEnabled(false);
        swap.setEnabled(false);
    }

    // disable buttons when an action is done
    public void waitNextTurn() {
        System.out.println("waitNextTurn");
        attack1.setEnabled(false);
        attack2.setEnabled(false);
        escape.setEnabled(false);
        capture.setEnabled(false);
        swap.setEnabled(false);
    }

    // initialize the encounter
    public void startFight(int nbPokmnRival, boolean rival) {
        this.setVisible(true);
        this.rival = rival;
        this.nbPokmnRival.setText("nombre de poketudiant : ".concat(Integer.toString(nbPokmnRival)));
    }

    //display the total number of pokemons
    public void setNbPokmn(int nbPokmn) {
        this.nbPokmn.setText("nombre de poketudiant : ".concat(Integer.toString(nbPokmn)));
    }

    // change the info for the player
    public void setInfo(String variety, String lvl, String hp, String attack1Name, String attack1Type, String attack2Name, String attack2Type) {
        image.setText(variety);
        this.lvl.setText("level : ".concat(lvl));
        this.hp.setText("HP : ".concat(hp));
        attack1.setText(attack1Name.concat(" \n").concat(attack1Type));
        attack2.setText(attack2Name.concat(" \n").concat(attack2Type));
    }

    //change info for the rival
    public void setInfo(String variety, String lvl, String hp) {
        imageRival.setText(variety);
        lvlRival.setText("level : ".concat(lvl));
        hpRival.setText("HP : ".concat(hp));
    }

    // called when the server is waiting for an action
    public void waitAction() {
        System.out.println("waitAction");
        this.attack2.setEnabled(true);
        this.attack1.setEnabled(true);
        this.swap.setEnabled(true);
        if (isRival()) {
            System.out.println("rival");
            this.escape.setEnabled(false);
            this.capture.setEnabled(false);
        } else {
            System.out.println("wild");
            this.escape.setEnabled(true);
            this.capture.setEnabled(true);
        }
    }

    // hide the encounter in order to replace it with the map
    public void endEncounter() {
        this.setVisible(false);
    }

    public boolean isRival() {
        return rival;
    }
}
