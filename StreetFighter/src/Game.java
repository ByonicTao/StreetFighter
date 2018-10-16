/* Author Joshua Piuti
 * Date 15 Oct 2018
 * Name Game.java
 * Purpose Street Fighter Java Game
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;

public class Game extends Canvas {


      	private BufferStrategy strategy;   // take advantage of accelerated graphics
        private boolean waitingForKeyPress = true;  // true if game held up until
                                                    // a key is pressed
        private boolean leftPressed = false;  // true if left arrow key currently pressed
        private boolean rightPressed = false; // true if right arrow key currently pressed
        private boolean firePressed = false; // true if firing
        private boolean upPressed = false; //true if up is pressed
        private boolean bPressed = false; // true if b is pressed

        private boolean gameRunning = true;
        private ArrayList entities = new ArrayList(); // list of entities
                                                      // in game
        private ArrayList removeEntities = new ArrayList(); // list of entities
                                                            // to remove this loop
        private Entity ship;  // the ship
        private double moveSpeed = 100; // hor. vel. of ship (px/s)
        private long lastFire = 0; // time last shot fired
        private long firingInterval = 300; // interval between shots (ms)
        private int alienCount; // # of aliens left on screen

        private String message = ""; // message to display while waiting
                                     // for a key press

        private boolean logicRequiredThisLoop = false; // true if logic
                                                       // needs to be
                                                       // applied this loop
        //Images for animated background
        private String backG1Str = "src/sprites/backG1-";
        private BufferedImage[] backG1 = {
            Sprite.getBackGround(backG1Str + "1"),
            Sprite.getBackGround(backG1Str + "2"),
            Sprite.getBackGround(backG1Str + "3"),
            Sprite.getBackGround(backG1Str + "4"),
            Sprite.getBackGround(backG1Str + "5"),
            Sprite.getBackGround(backG1Str + "6"),
            Sprite.getBackGround(backG1Str + "7"),
            Sprite.getBackGround(backG1Str + "8") };
        private Animation background = new Animation(backG1, 772, 0);

        //if true looking right, if false looking left
        private boolean side = true;

        // Images for each animation
        private String ryu = "src/sprites/ryuSpriteSheetTest";
        private String punch = "src/sprites/ryuPunchSprite";
        private BufferedImage[] walkingLeft = {
            Sprite.getSprite(5, 1, ryu, 86, 166),
            Sprite.getSprite(6, 1, ryu, 86, 166),
            Sprite.getSprite(7, 1, ryu, 86, 166),
            Sprite.getSprite(8, 1, ryu, 86, 166),
            Sprite.getSprite(9, 1, ryu, 86, 166)};
        private BufferedImage[] walkingRight = {
            Sprite.getSprite(5, 0, ryu, 86, 166),
            Sprite.getSprite(6, 0, ryu, 86, 166),
            Sprite.getSprite(7, 0, ryu, 86, 166),
            Sprite.getSprite(8, 0, ryu, 86, 166),
            Sprite.getSprite(9, 0, ryu, 86, 166)};
        private BufferedImage[] standingRight = {
            Sprite.getSprite(0, 0, ryu, 86, 166),
            Sprite.getSprite(1, 0, ryu, 86, 166),
            Sprite.getSprite(2, 0, ryu, 86, 166),
            Sprite.getSprite(3, 0, ryu, 86, 166)};
        private BufferedImage[] standingLeft = {
            Sprite.getSprite(0, 1, ryu, 86, 166),
            Sprite.getSprite(1, 1, ryu, 86, 166),
            Sprite.getSprite(2, 1, ryu, 86, 166),
            Sprite.getSprite(3, 1, ryu, 86, 166)};
        private BufferedImage[] punchingRight = {
            Sprite.getSprite(0, 0, punch, 142, 166),
            Sprite.getSprite(1, 0, punch, 142, 166),
            Sprite.getSprite(2, 0, punch, 142, 166)};
        private BufferedImage[] punchingLeft = {
            Sprite.getSprite(0, 1, punch, 142, 166),
            Sprite.getSprite(1, 1, punch, 142, 166),
            Sprite.getSprite(2, 1, punch, 142, 166)};

        // These are animation states
        private Animation walkLeft = new Animation(walkingLeft, 480, 86);
        private Animation walkRight = new Animation(walkingRight, 480, 86);
        private Animation standRight = new Animation(standingRight, 640, 86);
        private Animation standLeft = new Animation(standingLeft, 640, 86);
        private Animation punchRight = new Animation(punchingRight, 560, 142);
        private Animation punchLeft = new Animation(punchingLeft, 560, 142);

        // This is the actual animation
        private Animation animation = standRight;

    	/*
    	 * Construct our game and set it running.
    	 */
    	public Game() {
    		// create a frame to contain game
    		JFrame container = new JFrame("Commodore 64 Space Invaders");

    		// get hold the content of the frame
    		JPanel panel = (JPanel) container.getContentPane();

    		// set up the resolution of the game
    		panel.setPreferredSize(new Dimension(1200,537));
    		panel.setLayout(null);

    		// set up canvas size (this) and add to frame
    		setBounds(0,0,1280,537);
    		panel.add(this);

    		// Tell AWT not to bother repainting canvas since that will
        // be done using graphics acceleration
    		setIgnoreRepaint(true);


    		// make the window visible
    		container.pack();
    		container.setResizable(false);
    		container.setVisible(true);


        // if user closes window, shutdown game and jre
    		container.addWindowListener(new WindowAdapter() {
    			public void windowClosing(WindowEvent e) {
    				System.exit(0);
    			} // windowClosing
    		});

    		// add key listener to this canvas
    		addKeyListener(new KeyInputHandler());

    		// request focus so key events are handled by this canvas
    		requestFocus();

    		// create buffer strategy to take advantage of accelerated graphics
    		createBufferStrategy(2);
    		strategy = getBufferStrategy();

    		// initialize entities
    		initEntities();

    		// start the game
    		gameLoop();
        } // constructor


        /* initEntities
         * input: none
         * output: none
         * purpose: Initialise the starting state of the ship and alien entities.
         *          Each entity will be added to the array of entities in the game.
    	 */
    	private void initEntities() {
              // create the ship
              ship = new ShipEntity(this, "sprites/ryuGone.png", 50, 361, 86);
              entities.add(ship);
    	} // initEntities

        /* Notification from a game entity that the logic of the game
         * should be run at the next opportunity
         */
         public void updateLogic() {
           logicRequiredThisLoop = true;
         } // updateLogic

         /* Remove an entity from the game.  It will no longer be
          * moved or drawn.
          */
         public void removeEntity(Entity entity) {
           removeEntities.add(entity);
         } // removeEntity

         /* Notification that the player has died.
          */
         public void notifyDeath() {
           message = "LOSER";
           waitingForKeyPress = true;
         } // notifyDeath

         /* Notification that the play has killed all aliens
          */
         public void notifyWin(){
           message = "WINNER";
           waitingForKeyPress = true;
         } // notifyWin

        /* Attempt to fire. IMPORTANT USE FOR HADOUKEN
        public void tryToFire() {
          // check that we've waited long enough to fire
          if ((System.currentTimeMillis() - lastFire) < firingInterval){
            return;
          } // if

          // otherwise add a shot
          lastFire = System.currentTimeMillis();
          ShotEntity shot = new ShotEntity(this, "sprites/shot.gif",
                            ship.getX() + 10, ship.getY() - 30);
          entities.add(shot);
        } // tryToFire */


	/*
	 * gameLoop
         * input: none
         * output: none
         * purpose: Main game loop. Runs throughout game play.
         *          Responsible for the following activities:
	 *           - calculates speed of the game loop to update moves
	 *           - moves the game entities
	 *           - draws the screen contents (entities, text)
	 *           - updates game events
	 *           - checks input
	 */
	public void gameLoop() {
          long lastLoopTime = System.currentTimeMillis();
          background.start();

          // keep loop running until game ends
          while (gameRunning) {
            animation.update();
            background.update();

            // calc. time since last update, will be used to calculate
            // entities movement
            long delta = System.currentTimeMillis() - lastLoopTime;
            lastLoopTime = System.currentTimeMillis();

            // get graphics context for the accelerated surface and make it black
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.drawImage(background.getSprite(), 0, 0, null);
            g.drawImage(animation.getSprite(), animation.getMid(ship), ship.getY(), null);
            // move each entity
            if (!waitingForKeyPress) {
            	ship.move(delta);
              for (int i = 0; i < entities.size(); i++) {
                Entity entity = (Entity) entities.get(i);
                entity.move(delta);
              } // for
            } // if

            // brute force collisions, compare every entity
            // against every other entity.  If any collisions
            // are detected notify both entities that it has
            // occurred
           for (int i = 0; i < entities.size(); i++) {
             for (int j = i + 1; j < entities.size(); j++) {
                Entity me = (Entity)entities.get(i);
                Entity him = (Entity)entities.get(j);

                if (me.collidesWith(him)) {
                  me.collidedWith(him);
                  him.collidedWith(me);
                } // if

             } // inner for
           } // outer for

           // remove dead entities
           entities.removeAll(removeEntities);
           removeEntities.clear();

           ship.draw(g);

           // run logic if required
           if (logicRequiredThisLoop) {
             for (int i = 0; i < entities.size(); i++) {
               Entity entity = (Entity) entities.get(i);
               entity.doLogic();
             } // for
             logicRequiredThisLoop = false;
           } // if

            // clear graphics and flip buffer
            g.dispose();
            strategy.show();

            // ship should not move without user input
            ship.setHorizontalMovement(0);

            if(side) {
              animation = standRight;
              animation.start();
            } else {
              animation = standLeft;
              animation.start();
            }//else

            // respond to user moving ship
            if ((leftPressed) && (!rightPressed)) {
              ship.setHorizontalMovement(-moveSpeed);
              animation = walkRight;
              animation.start();
              side = false;
            } else if ((rightPressed) && (!leftPressed)) {
              ship.setHorizontalMovement(moveSpeed);
              side = true;
              animation = walkRight;
              animation.start();
            } // else

            if(firePressed && !leftPressed && !rightPressed) {
              if(side) {
                animation = punchRight;
              } else {
                animation = punchLeft;
              }//else
              animation.start();
            }//if
          } // while

	} // gameLoop


        /* startGame
         * input: none
         * output: none
         * purpose: start a fresh game, clear old data
         */
         private void startGame() {
            // clear out any existing entities and initalize a new set
            entities.clear();

            initEntities();

            // blank out any keyboard settings that might exist
            leftPressed = false;
            rightPressed = false;
            firePressed = false;
            upPressed = false;
            bPressed = false;
         } // startGame


        /* inner class KeyInputHandler
         * handles keyboard input from the user
         */
	private class KeyInputHandler extends KeyAdapter {

                 private int pressCount = 1;  // the number of key presses since
                                              // waiting for 'any' key press

                /* The following methods are required
                 * for any class that extends the abstract
                 * class KeyAdapter.  They handle keyPressed,
                 * keyReleased and keyTyped events.
                 */
		public void keyPressed(KeyEvent e) {

                  // if waiting for keypress to start game, do nothing
                  if (waitingForKeyPress) {
                    return;
                  } // if

                  // respond to move left, right or fire
                  if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    leftPressed = true;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    rightPressed = true;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    firePressed = true;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_UP) {
                	  upPressed = true;
                  }//if

                  if(e.getKeyCode() == KeyEvent.VK_B) {
                	  bPressed = true;
                  }//if

		} // keyPressed

		public void keyReleased(KeyEvent e) {
                  // if waiting for keypress to start game, do nothing
                  if (waitingForKeyPress) {
                    return;
                  } // if

                  // respond to move left, right or fire
                  if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    leftPressed = false;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    rightPressed = false;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    firePressed = false;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_UP) {
                	  upPressed = false;
                  }//if

                  if(e.getKeyCode() == KeyEvent.VK_B) {
                	  bPressed = false;
                  }//if

		} // keyReleased

 	        public void keyTyped(KeyEvent e) {

                   // if waiting for key press to start game
 	           if (waitingForKeyPress) {
                     if (pressCount == 1) {
                       waitingForKeyPress = false;
                       startGame();
                       pressCount = 0;
                     } else {
                       pressCount++;
                     } // else
                   } // if waitingForKeyPress

                   // if escape is pressed, end game
                   if (e.getKeyChar() == 27) {
                     System.exit(0);
                   } // if escape pressed

		} // keyTyped

	} // class KeyInputHandler


	/**
	 * Main Program
	 */
	public static void main(String [] args) {
        // instantiate this object
		new Game();
	} // main
} // Game
