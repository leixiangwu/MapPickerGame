package ramble_on_events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import ramble_on.RambleOn;
import ramble_on.RambleOnDataModel;

/**
 *
 * @author McKillaGorilla
 */
public class HotKeyHandler implements KeyListener {

    private RambleOn game;

    public HotKeyHandler(RambleOn initGame) {
        game = initGame;
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        char key = ke.getKeyChar();
        if (key == 'c') {
            try {
                game.beginUsingData();
                RambleOnDataModel dataModel = (RambleOnDataModel) (game.getDataModel());
                dataModel.removeAllButOneFromeStack();
            } finally {
                game.endUsingData();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }
}
