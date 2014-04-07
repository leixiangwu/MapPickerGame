package ramble_on_events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import mini_game.Sprite;
import ramble_on.RambleOn;
import ramble_on.RambleOnDataModel;
import static ramble_on.RambleOnSettings.*;

/**
 *
 * @author Leixiang
 */
public class ModeClickHandler implements ActionListener {

    private RambleOnDataModel data;

    public ModeClickHandler(RambleOnDataModel initData) {
        data = initData;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Sprite s : RambleOn.getRambleOnApp().getGUIButtons().values()) {
            if (s.containsPoint(data.getLastMouseX(), data.getLastMouseY())) {
                String type = s.getSpriteType().getSpriteTypeID();
                switch (type) {
                    case SUB_REGION_MODE_TYPE:
                        data.startSubRegionMode();
                        break;
                    case FLAG_MODE_TYPE:
                        data.startFlagMode();
                        break;
                    case LEADER_MODE_TYPE:
                        data.startLeaderMode();
                        break;
                    case CAPITAL_MODE_TYPE:
                        data.startCapitalMode();
                        break;
                    case EXIT_GAME_TYPE:
                        RambleOn.getRambleOnApp().killApplication();
                    default:
                    break;
                }
            }
        }
    }
}