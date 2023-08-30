import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Map extends JPanel{
    private ImageIcon grass;
    private ImageIcon tallGrass;
    private ImageIcon pokmnCenter;
    private ImageIcon player;
    private ImageIcon rival;
    private ImageIcon rival2;
    private ImageIcon rival3;
    private ImageIcon water;
    private Client client;
    private InputListener l;
    private GridLayout gl;
    private Dimension playerPos;
    private int maxWidth;
    private int maxHeight;
    
    public Map(Client client) {
        this.addMouseListener(new MouseInputListener() {

            @Override
            public void mouseClicked(MouseEvent me) {
                // TODO Auto-generated method stub
                
            }

            // keep focus for the key listener
            @Override
            public void mouseEntered(MouseEvent me) {
                requestFocus();
            }

            @Override
            public void mouseExited(MouseEvent me) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mousePressed(MouseEvent me) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                // TODO Auto-generated method stub
                
            }
        });

        gl = new GridLayout();
        this.setLayout(gl);
        playerPos = new Dimension(0, 0);
        maxWidth = 15;
        maxHeight = 15;
        gl.setColumns(maxWidth);
        gl.setRows(maxHeight);
        this.client = client;
        client.setMap(this);
        // size of sprites
        int caseDim = 45;

        try {
            grass = new ImageIcon(ImageIO.read(new File("assets/grass.png")));
            Image image = grass.getImage();
            Image rescaled = image.getScaledInstance(caseDim, caseDim, Image.SCALE_SMOOTH);
            grass = new ImageIcon(rescaled);
                        
            tallGrass = new ImageIcon(ImageIO.read(new File("assets/tallGrass.png")));
            image = tallGrass.getImage();
            rescaled = image.getScaledInstance(caseDim, caseDim, Image.SCALE_SMOOTH);
            tallGrass = new ImageIcon(rescaled);

            pokmnCenter = new ImageIcon(ImageIO.read(new File("assets/pokmnCenter.png")));
            image = pokmnCenter.getImage();
            rescaled = image.getScaledInstance(caseDim, caseDim, Image.SCALE_SMOOTH);
            pokmnCenter = new ImageIcon(rescaled);

            player = new ImageIcon(ImageIO.read(new File("assets/player.png")));
            image = player.getImage();
            rescaled = image.getScaledInstance(caseDim, caseDim, Image.SCALE_SMOOTH);
            player = new ImageIcon(rescaled);

            rival = new ImageIcon(ImageIO.read(new File("assets/rival.png")));
            image = rival.getImage();
            rescaled = image.getScaledInstance(caseDim, caseDim, Image.SCALE_SMOOTH);
            rival = new ImageIcon(rescaled);

            rival2 = new ImageIcon(ImageIO.read(new File("assets/rival2.png")));
            image = rival2.getImage();
            rescaled = image.getScaledInstance(caseDim, caseDim, Image.SCALE_SMOOTH);
            rival2 = new ImageIcon(rescaled);

            rival3 = new ImageIcon(ImageIO.read(new File("assets/rival3.png")));
            image = rival3.getImage();
            rescaled = image.getScaledInstance(caseDim, caseDim, Image.SCALE_SMOOTH);
            rival3 = new ImageIcon(rescaled);

            water = new ImageIcon(ImageIO.read(new File("assets/water.png")));
            image = water.getImage();
            rescaled = image.getScaledInstance(caseDim, caseDim, Image.SCALE_SMOOTH);
            water = new ImageIcon(rescaled);

        } catch (IOException e) {
            e.printStackTrace();
        }

        l = new InputListener(client);
    }

    public void locatePlayer(List<String> map, int width) {
        // cannot be used properly because of a bug from the teacher's server
        if (!playerPos.equals(new Dimension(0, 0))) {
            System.out.println("playerPos exist");
            double widthPlayer = playerPos.getWidth();
            double heightPlayer = playerPos.getHeight();
            // width is x and height is y
            playerPos.setSize(widthPlayer > 0 ? widthPlayer - 1 : widthPlayer, heightPlayer > 0 ? heightPlayer - 1 : heightPlayer);
        }
        playerPos = new Dimension(0, 0); 
        String mapLine;
        int heightP = (int) playerPos.getHeight() > 0 ? (int) playerPos.getHeight() - 1 : 0;
        int widthP = (int) playerPos.getWidth() > 0 ? (int) playerPos.getWidth() - 1 : 0;
        for (int j = heightP; j < map.size(); j++) {
            mapLine = map.get(j);
            for(int i = widthP; i < width; i++) {
                // System.out.println(i + " " + j);
                if ((mapLine.charAt(i)) == '0') {
                    playerPos = new Dimension(i, j);
                    return;
                }
            }
        }
    }

    public void drawMap(int width) {
        // is getting called too many times i think, by the teacher's server
        removeAll();
        if (getKeyListeners().length > 0) removeKeyListener(l);
        this.addKeyListener(l);
        this.requestFocus();
        System.out.println("drawMap");
        List<String> map = client.getServerOutput();
        locatePlayer(map, width);
        int height = map.size();
        JLabel picLabel;
        String mapLine;

        int y = 1;
        int x = 1;
        int factorX = (int) (playerPos.getWidth() / maxWidth); // count of screen passed on the X axis
        int factorY = (int) (playerPos.getHeight() / maxHeight); // count of screen passed on the Y axis

        while (y % (maxHeight + 1) != 0) {

            //System.out.println((y - 1) + maxHeight * factorY);
            x = 1;
            mapLine = map.get((y - 1) + maxHeight * factorY < height ? (y - 1) + maxHeight * factorY : height - 1); // check if we are out of bounds
            y++;

            while (x % (maxWidth + 1) != 0) {
                // System.out.println((x - 1) + maxWidth * factorX);

                //if we are out of bounds we draw water
                if ((x - 1) + maxWidth * factorX >= width || (y - 1) + maxHeight * factorY > height) picLabel = new JLabel(water);
                // otherwise we draw the map received
                else if ((mapLine.charAt((x - 1) + maxWidth * factorX)) == ' ') picLabel = new JLabel(grass);
                else if ((mapLine.charAt((x - 1) + maxWidth * factorX)) == '*') picLabel = new JLabel(tallGrass);
                else if ((mapLine.charAt((x - 1) + maxWidth * factorX)) == '+') picLabel = new JLabel(pokmnCenter);
                else if ((mapLine.charAt((x - 1) + maxWidth * factorX)) == '0') picLabel = new JLabel(player);
                else if ((mapLine.charAt((x - 1) + maxWidth * factorX)) == '1') picLabel = new JLabel(rival);
                else if ((mapLine.charAt((x - 1) + maxWidth * factorX)) == '2') picLabel = new JLabel(rival2);
                else picLabel = new JLabel(rival3);

                add(picLabel);
                x++;
            }
        }
        revalidate();
        repaint();
    }
}
