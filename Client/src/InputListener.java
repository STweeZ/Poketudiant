import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputListener implements KeyListener{
    private Client client;
    private final long threshold = 200;
    private long last = System.currentTimeMillis();

    public InputListener(Client client) {
        this.client = client;
        System.out.println("listener");
    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        // timerTask and keyBindings would have been better
        long now = System.currentTimeMillis();
        System.out.println(now - last);
        if (now - last > threshold) {
            last = now;
            switch(e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    client.move(Direction.UP);
                    break;
                case KeyEvent.VK_DOWN:
                    client.move(Direction.DOWN);
                    break;
                case KeyEvent.VK_LEFT:
                    client.move(Direction.LEFT);
                    break;
                case KeyEvent.VK_RIGHT:
                    client.move(Direction.RIGHT);
                    break;
            }
        }
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
    }

    @Override
    public synchronized void keyTyped(KeyEvent e) {}
    
}
