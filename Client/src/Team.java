import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class Team extends JPanel{

    private class MyActionListener implements ActionListener{
        private Direction direction;

        public MyActionListener(Direction direction) {
            this.direction = direction;
        }

        public void actionPerformed(ActionEvent e) {
            if (selectedButton != null) {
                if (direction == null) client.freePoketudiant(selectedButton.getPos());
                else
                    client.movePoketudiant(selectedButton.getPos(), direction);
            }
            select.setText("");
            selectedButton = null;
            up.setEnabled(false);
            down.setEnabled(false);
            free.setEnabled(false);
        }
    }

    private JLabel select;
    private PokeButton selectedButton;
    private Client client;
    private List<PokeButton> pokebuttons;
    private JButton up;
    private JButton down;
    private JButton free;

    public Team(Client client) {
        this.client = client;
        client.setTeam(this);
        GridLayout gl = new GridLayout();
        gl.setColumns(2);
        gl.setRows(4);
        gl.setHgap(10);
        gl.setVgap(10);
        this.setLayout(gl);
        select = new JLabel();
        pokebuttons = new ArrayList<PokeButton>();
        select.setForeground(Color.WHITE);
        setBackground(Color.decode("#1e3d59"));
        up = new JButton("\u25B2");
        down = new JButton("\u25BC");
        free = new JButton("free");
        up.addActionListener(new MyActionListener(Direction.UP));
        down.addActionListener(new MyActionListener(Direction.DOWN));
        free.addActionListener(new MyActionListener(null));
        add(select);
        add(up);
        add(free);
        add(down);
        selectedButton = null;
        up.setEnabled(false);
        down.setEnabled(false);
        free.setEnabled(false);
    }

    public void drawTeam(List<Poketudiant> poketudiants) {
        for (PokeButton pokebutton : pokebuttons) {
            remove(pokebutton);
        }
        pokebuttons.clear();
        PokeButton button;
        int c = 0;
        for (Poketudiant poketudiant : poketudiants) {
            button = new PokeButton(c++, poketudiant);
            button.setSize(100, 150);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    select.setText(poketudiant.getVariety().concat(
                    " ".concat(String.valueOf(poketudiant.getLvl()))));
                    selectedButton = (PokeButton) e.getSource();
                    if (selectedButton.getPos() <= 0) up.setEnabled(false);
                    else up.setEnabled(true);
                    if (selectedButton.getPos() >= pokebuttons.size() - 1) down.setEnabled(false);
                    else down.setEnabled(true);
                    if (poketudiant.getType().toLowerCase().equals("teacher")) free.setEnabled(false);
                    else free.setEnabled(true);
                    revalidate();
                    repaint();
                }
            });
            add(button);
            pokebuttons.add(button);
        }
        revalidate();
        repaint();
    }

    public int getNbPokmn() {
        return pokebuttons.size();
    }

    public int getSelectedPokmnIndex() {
        return selectedButton != null ? selectedButton.getPos() : -1;
    }

    public String getPokmnName(int index) {
        return pokebuttons.get(index).getPoketudiant().getVariety();
    }

    public void resetSelectedPokmn() {
        selectedButton = null;
    }
}
