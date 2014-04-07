/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ramble_on_events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JList;
import ramble_on.RambleOn;
import ramble_on.RambleOnDataModel;

/**
 *
 * @author Leixiang
 */
public class GoToAccountCreationHandler implements ActionListener {

    private RambleOnDataModel data;
    private RambleOn game;
    private JList list;

    public GoToAccountCreationHandler(RambleOnDataModel initData, RambleOn initGame, JList accountsList) {
        data = initData;
        game = initGame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        data.goToAccountCreationScreen(game);
    }
}
