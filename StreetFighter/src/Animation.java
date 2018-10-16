/*
Author: Savion on StackExchange
Name: Animaion.class
Pupose: The animation class has start, stop, restart and reset to control the animation.
*/
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class Animation {

    // Counts ticks for change
    private int frameCount;
    // frame delay 1-> (You will have to play around with this)
    private int frameDelay;
    // animations current frame
    private int currentFrame;
    // animation direction (i.e counting forward or backward)
    private int animationDirection;
    // total amount of frames for your animation
    private int totalFrames;

    // has animations stopped
    private boolean stopped;

    //how wide the animation is
    private int width;

    // Arraylist of frames
    private List<Frame> frames = new ArrayList<Frame>();

    //Animation constructor
    public Animation(BufferedImage[] frames, int frameDelay, int width) {
        this.frameDelay = frameDelay;
        this.stopped = true;

        for (int i = 0; i < frames.length; i++) {
            addFrame(frames[i], frameDelay);
        }//for

        this.frameCount = 0;
        this.frameDelay = frameDelay;
        this.currentFrame = 0;
        this.animationDirection = 1;
        this.totalFrames = this.frames.size();
        this.width = width;

    }//Animation Constructor

    //starts running the animation
    public void start() {
        if (!stopped) {
            return;
        }//if

        if (frames.size() == 0) {
            return;
        }//if

        stopped = false;
    }//Start

    //stops running the animation
    public void stop() {
        if (frames.size() == 0) {
            return;
        }//if

        stopped = true;
    }//stop

    //restarts the animation
    public void restart() {
        if (frames.size() == 0) {
            return;
        }//if

        stopped = false;
        currentFrame = 0;
    }//restart

    //resets the animation
    public void reset() {
        this.stopped = true;
        this.frameCount = 0;
        this.currentFrame = 0;
    }//reset

    //adds another frame to the animation
    private void addFrame(BufferedImage frame, int duration) {
        if (duration <= 0) {
            System.err.println("Invalid duration: " + duration);
            throw new RuntimeException("Invalid duration: " + duration);
        }//if

        frames.add(new Frame(frame, duration));
        currentFrame = 0;
    }//addFrame

    //returns the current frame
    public BufferedImage getSprite() {
        return frames.get(currentFrame).getFrame();
    }//getSprite

    //updates the animation on game tick
    public void update() {
        if (!stopped) {
            frameCount++;

            if (frameCount > frameDelay) {
                frameCount = 0;
                currentFrame += animationDirection;

                if (currentFrame > totalFrames - 1) {
                    currentFrame = 0;
                }
                else if (currentFrame < 0) {
                    currentFrame = totalFrames - 1;
                }//else
            }//if
        }//if
    }//update

    public int getWidth() {
      return this.width;
    }

    public int getMid(Entity a) {
      int result = (a.getX() + (a.getWidth() / 2)) - (this.width / 2);
      return result;
    }//getMid
}//Animation
