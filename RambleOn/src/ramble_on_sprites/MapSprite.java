/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ramble_on_sprites;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import mini_game.Sprite;
import mini_game.SpriteType;

/**
 *
 * @author Leixiang
 */
public class MapSprite extends Sprite {

    private String textToRender;
    // THE CUSTOM EVENT HANDLER FOR THIS Sprite WHOSE actionPerformed
    // METHOD IS TO INVOKED WHEN THE PLAYER CLICKS ON THIS Sprite
    private MouseListener mouseListener;

    public MapSprite(SpriteType initSpriteType,
            float initX, float initY,
            float initVx, float initVy,
            String initState) {
        super(initSpriteType, initX, initY, initVx, initVy, initState);
    }
}
