/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ramble_on_events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ramble_on.RambleOn;
import ramble_on.RambleOnDataModel;

/**
 *
 * @author Leixiang
 */
public class GoForwardHandler implements ActionListener {

    private RambleOnDataModel data;
    private RambleOn game;

    public GoForwardHandler(RambleOnDataModel initData, RambleOn game) {
        data = initData;
        this.game = game;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        data.goForwardOneScreen(game);
    }
}
