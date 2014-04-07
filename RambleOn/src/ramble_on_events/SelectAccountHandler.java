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
public class SelectAccountHandler implements ActionListener {

    private RambleOnDataModel data;
    private RambleOn game;
    private JList list;

    public SelectAccountHandler(RambleOnDataModel initData, RambleOn initGame, JList initList) {
        data = initData;
        game = initGame;
        this.list = initList;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!list.isSelectionEmpty()) {
            String selectedAccount = (String) list.getSelectedValue();
            data.getAccountManager().setCurrentAccount(data.getAccountManager().getAccount(selectedAccount));
            data.goForwardOneScreen(game);
        }
    }
}
