/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ramble_on_events;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import mini_game.MiniGameEventRelayer;
import ramble_on.GameModeState;
import ramble_on.RambleOn;
import ramble_on.RambleOnDataModel;

/**
 *
 * @author Leixiang
 */
public class RambleOnEventRelayer extends MiniGameEventRelayer {

    public RambleOnEventRelayer() {
        super(RambleOn.getRambleOnApp());
    }

    public void mouseMoved(MouseEvent me) {
        try {
            RambleOn.getRambleOnApp().beginUsingData();

            RambleOn.getRambleOnApp().getDataModel().setLastMouseX(me.getX());
            RambleOn.getRambleOnApp().getDataModel().setLastMouseY(me.getY());
            RambleOn.getRambleOnApp().getDataModel().updateDebugText(RambleOn.getRambleOnApp());
        } finally {
            // RELEASE THE DATA SO THAT THE TIMER THREAD MAY
            // APPROPRIATELY UPDATE AND RENDER THE GAME
            // WITHOUT INTERFERENCE
            RambleOn.getRambleOnApp().endUsingData();
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {

        try {
            RambleOn.getRambleOnApp().beginUsingData();
            // LOCK THE DATA
            int x = me.getX();
            int y = me.getY();
            if (((RambleOnDataModel) (RambleOn.getRambleOnApp().getDataModel())).isClickOnMap(x, y)) {
                if (((RambleOnDataModel) (RambleOn.getRambleOnApp().getDataModel())).getModeState() == null) {
                    //left click?
                    if (me.getButton() == MouseEvent.BUTTON1) {
                        ((RambleOnDataModel) (RambleOn.getRambleOnApp().getDataModel())).respondToLeftClick(x, y);
                    } //right click?
                    else if (me.getButton() == MouseEvent.BUTTON3) {
                        ((RambleOnDataModel) (RambleOn.getRambleOnApp().getDataModel())).respondToRightClick();
                    }
                } else {
                    ((RambleOnDataModel) (RambleOn.getRambleOnApp().getDataModel())).respondToMapClick(x, y);
                }

            }
        } finally {
            // RELEASE THE DATA SO THAT THE TIMER THREAD MAY
            // APPROPRIATELY UPDATE AND RENDER THE GAME
            // WITHOUT INTERFERENCE
            RambleOn.getRambleOnApp().endUsingData();
        }
    }
}
