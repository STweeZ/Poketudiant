import javax.swing.JButton;

public class PokeButton extends JButton {
    // this class help keep tracks of the poketudiant everywhere
    private int pos;
    private Poketudiant poketudiant;

    public PokeButton(int pos, Poketudiant poketudiant) {
        super(poketudiant.getVariety().concat(
            " lvl : ".concat(String.valueOf(poketudiant.getLvl()))));
        this.pos = pos;
        this.poketudiant = poketudiant;
    }

    public int getPos() {
        return pos;
    }

    public Poketudiant getPoketudiant() {
        return poketudiant;
    }
}
