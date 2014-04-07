/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ramble_on_events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import ramble_on.RambleOn;
import ramble_on.RambleOnDataModel;

/**
 *
 * @author Leixiang
 */
public class CreateNewAccountHandler implements ActionListener {

    private RambleOnDataModel data;
    private RambleOn game;
    private JTextField accountName;

    public CreateNewAccountHandler(RambleOn initGame, RambleOnDataModel initData, JTextField initAccountName) {
        data = initData;
        game = initGame;
        accountName = initAccountName;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        data.createAccount(game,accountName.getText());
    }
}
