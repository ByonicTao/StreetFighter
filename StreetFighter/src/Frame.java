/*
Author: Savion on StackExchange
Name: Frame.Class
Pupose: The Frame class holds an image and a duration associated with that image.
*/

import java.awt.image.BufferedImage;

public class Frame {

    //holds the image that is displayed
    private BufferedImage frame;
    //the duration that the image is being displayed
    private int duration;

    //Frame constructor
    public Frame(BufferedImage frame, int duration) {
        this.frame = frame;
        this.duration = duration;
    }//Frame

    public BufferedImage getFrame() {
        return frame;
    }//getFrame

    public void setFrame(BufferedImage frame) {
        this.frame = frame;
    }//setFrame

    public int getDuration() {
        return duration;
    }//getDuration

    public void setDuration(int duration) {
        this.duration = duration;
    }//setDuration
}//Frame
