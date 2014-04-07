/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ramble_on_sprites;

import mini_game.Sprite;
import mini_game.SpriteType;

/**
 *
 * @author Leixiang
 */
public class RegionSprite extends Sprite {

    private String regionName;

    public RegionSprite(
            String initRegionName,
            SpriteType initSpriteType,
            float initX, float initY,
            float initVx, float initVy,
            String initState) {
        super(initSpriteType, initX, initY, initVx, initVy, initState);
        regionName = initRegionName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String initRegionName) {
        regionName = initRegionName;
    }
}
