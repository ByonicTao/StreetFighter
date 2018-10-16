/* Author: Savion on StackExchange.com
 * Name: Sprite.class
 * Purpose: This class can either load an image file or it can load a sprite from a sprite sheet.
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {

    //holds the image of the sprite sheet to take subImages off of
    private static BufferedImage spriteSheet;
    private static BufferedImage backGround;

    //size of the sprite tiles
    private static final int TILE_SIZE_X = 86;
    private static final int TILE_SIZE_Y = 166;

    //loads the sprite sheet
    public static BufferedImage loadSprite(String file) {

        BufferedImage sprite = null;

        try {
            sprite = ImageIO.read(new File(file + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }//try catch
        return sprite;
    }//loadSprite

    //loads the background sheet
    public static BufferedImage loadBackGround(String file) {

        BufferedImage sprite = null;

        try {
            sprite = ImageIO.read(new File(file + ".gif"));
        } catch (IOException e) {
          e.printStackTrace();
        }//try catch
        return sprite;
    }//loadSprite

    //returns the sprite in the specified position
    public static BufferedImage getSprite(int xGrid, int yGrid, String file,
      int xSize, int ySize) {
        spriteSheet = loadSprite(file);
        return spriteSheet.getSubimage((xGrid * xSize),
            (yGrid * ySize), xSize, ySize);
    }//getSprite

    //returns the entire image of the background
    public static BufferedImage getBackGround(String file) {
        backGround = loadBackGround(file);
        return backGround.getSubimage(0, 0, 1200, 551);
    }//getSprite
}//Sprite
