/* Robbie Meyer
 * June 15, 2015
 * Eel
 * Plays the game of Eel (snake)
 */

package eel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This class contains the main game
 */

public class Game implements KeyListener{
    //These components must be accessed by the drawing inner-class and the main game class

    private static JPanel canvas; //The panel that everything is drawn on
    private static int score; //The score for the game
    //These are initialized to nothing to prevent the drawing layer to try draw them before the game has begun
    private CharacterEel player = new CharacterEel(new Point(0,0)); //The player's eel
    private Rectangle fruit = new Rectangle(0,0,0,0); //The fruit for the snake to eat
    
    private boolean displayRules, displayStart, displayEnd; //If the rules, start screen and end screen should be displayed
    private boolean restartOption; //If the player has chosen to restart the game

    private Color playerColor = Color.RED, fruitColor = Color.YELLOW; //The player and fruit's colors
    private int colorCount = 1; //Used to cycle through the colors

    //This is a separate variable to prevent multiple inputs occurring at once causing the eel to turn onto itself
    //It needs to be used by the KeyPressed method and the setMovements method 
    private char pressedKey; 

    /**
     * Creates the window
     * Pre: n/a
     * Post: The window is created with icon, title and panel (for drawing)
     * 
     * Algorithm
     * 1. Create the JFrame
     * 2. Create the icon image
     * 3. Set the window to exit on close
     * 4. Set the size of the window to 635x685 (playing space will be 600x600)
     * 5. Create a new panel from the inner class DrawingPart called canvas
     * 6. Make the canvas focusable
     * 7. Add canvas to the frame
     * 8. Set the title and icon of the frame
     * 9. Make the frame visible
     * 10. Set the rules to display
     */
    public void go() {
        JFrame frame = new JFrame();
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/ISUicon.png"));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(635, 685);
        frame.setResizable(false);

        canvas = new DrawingPart();
        canvas.setFocusable(true);

        frame.getContentPane().add(canvas);
        canvas.addKeyListener(this);

        frame.setTitle("Eel");
        frame.setIconImage(icon);
        frame.setVisible(true);

        displayRules = true;
    }


    /**
     * Plays the game
     * Pre: the window has already been created
     * Post: the game has been played 
     */

    public void beginGame(){
        //Set the start screen to appear and the the end screen to disappear
        displayStart = true;
        displayEnd = false;
        //Set the core to 0;
        score = 0;

        //Create the board of points for the eel to move to
        Point[][] board = null;
        board = createBoard(board);

        //To record if the eel is still alive
        boolean stillAlive = true;

        //Sets the starting location of the eel, and creates the eel
        int locationX = 15, locationY = 6;
        player = new CharacterEel(board[locationX][locationY]);

        //Creates the fruit
        moveFruit(board);

        //The program will do nothing until the rules are hidden
        while (displayRules){
            System.out.print("");
        }

        //Redraws the screen after the rules are hidden
        canvas.repaint();

        //Makes sure the eel is not moving before the game begins
        pressedKey = 0;

        //Until the eel moves, only checks to see if movement is desired 
        while ((player.getMoveX() == 0 && player.getMoveY() == 0)){
            setMovements();
        }

        //Hides the start screen after the eel has moved 
        displayStart = false;

        //The main game, continues until the eel dies
        while (stillAlive){
            //Sets the location for the eel based on its movements
            locationX += player.getMoveX();
            locationY += player.getMoveY();    		

            //Moves the eel
            player.moveEel(board[locationX][locationY]); 

            //Checks if the eel has died, also checks if the fruit is under the eel
            stillAlive = checkCollisions(locationX, locationY, board);

            //If the head of the eel touches the fruit, the length of the eel is increased and the fruit is moved
            if (player.getTail().get(0).intersects(fruit)){
                player.increaseLength();
                moveFruit(board);
            }

            //Sets the movements for the next loop
            setMovements();
        }

        //Once the game is over, display the end screen
        displayEnd = true;

        //Until the user specifies they want to restart or they close the window the game will wait
        restartOption = false; //Resets the option
        while (!restartOption){
            System.out.print("");
        }

    }

    /**
     * Checks for collisions on the eel
     * Pre: the window and eel have been created
     * Post: returns true if the eel is alive, false if the eel has died and will move the fruit if it is under the eel's tail
     */

    public boolean checkCollisions(int locationX, int locationY, Point[][] board){
        //If the eel is out of bounds, return the eel has dies
        if (!(locationX > 0 && locationX < 31 && locationY > 0 && locationY < 31)){
            return false;
        }

        //For each rectangle in the eel, expect the head
        for (int i = 1 ; i < player.getTail().size(); i++){
            //If the fruit was generated under the eel moves the fruit
            /*
             * This check is performed here instead of in the moveFruit method
             * as the method is already checking the eel for collisions.
             * The program is faster when it is in here
             */
            if (player.getTail().get(i).contains(fruit))
                moveFruit(board);

            //If the tail hit the head, return the eel has dies
            if (player.getTail().get(i).contains(player.getTail().get(0))){
                return false;
            }

        }

        //If the eel has not collided with itself, return it is still alive
        return true;
    }

    /**
     * Fills the board of movement points
     * Pre: an initialized two dimensional points array
     * Post: the array is filled
     */

    public Point[][] createBoard (Point[][] grid){
        //Created the array
        grid = new Point[32][32];

        //Creates each point 20 pixels away from each other
        for (int i = 0; i < 32; i++){
            for (int k = 0; k < 32; k++){
                grid[i][k] = new Point(20 * i - 5,20 * k - 5);
            }
        }

        //Returns the filled array
        return grid;
    }

    /**
     * Moves the fruit to a random spot on the board
     * Pre: a board of points has been filled
     * Post: the fruit has moved
     */
    public void moveFruit(Point [][] grid){
        //The fruit is 20x20
        Dimension size = new Dimension(20,20);
        //Created the fruit in a random spot on the board
        fruit = new Rectangle (grid[(int) (Math.random()*30+1)][(int) (Math.random()*30+1)], size);  
    }

    /**
     * Adds points to the socre
     * Pre: there is a score and points
     * Post: the points are added to the score
     */
    public static void increaseScore (int points){
        //Adds the specified number of points to the score
        score += points;
    }

    /**
     * Returns the canvas to allow other classes to redraw
     * Pre: The canvas has been created
     * Post: The canvas is returned
     */
    public static JPanel getCanvas(){
        return canvas;
    }

    /**
     * Cycles through the color options
     * Pre: colorCount is greater than -1 and less then 6
     * Post: The colors of the eel and the fruit have changed
     */
    public void cycleColors(){
        //Selects the next color scheme
        colorCount += 1;

        //Sets the colors for the desired color scheme
        switch (colorCount){
        case 1: playerColor = Color.RED; fruitColor = Color.YELLOW; break;
        case 2: playerColor = Color.YELLOW; fruitColor = Color.RED; break;
        case 3: playerColor = Color.GREEN; fruitColor = Color.RED; break;
        case 4: playerColor = Color.CYAN; fruitColor = Color.WHITE; break;
        case 5: playerColor = Color.decode("#FF47B6"); fruitColor = Color.decode("#B05BFF"); break; //Pink and purple
        case 6: playerColor = Color.decode("#FF4700"); fruitColor = Color.decode("#004FFF"); colorCount = 0; break; //Blue and orange

        }
    }

    /**
     * The inner class for the DrawingPart (to create canavs)
     * Overrides the JPanel's paintComponents method to draw the game
     */
    @SuppressWarnings("serial") //To stop eclipse complaining about he lack of a serial id
    class DrawingPart extends JPanel {

        /**
         * Creates the rules image, waits for it to load beofre moving on
         * Pre: There is a rules image in the resources folder
         * Post: The rules image is returned
         */
        public Image createRules(){
            //The mediatracker is required to wait for the image
            //Sometimes the program would load before the image had fully loaded
            MediaTracker tracker = new MediaTracker( this );

            //Loads the image
            Image rules  = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/rules.png"));

            //Adds it to the mediatracker
            tracker.addImage ( rules, 0);

            //Wait for the image to load
            try {
                tracker.waitForAll();
            } catch (InterruptedException ie) {
                System.out.println("Image not loaded");
            }

            //Return the image once it is loaded
            return rules;
        }

        //Creates the rules
        Image rules  = createRules();

        //Creates fonts to use while drawing
        Font font = new Font("Arial", Font.CENTER_BASELINE, 24);
        Font smallFont = new Font("Arial", Font.PLAIN, 14);

        /**
         * Draws the game
         * Pre: The start, end, and rules screens are created. The fruit and eel have been created
         * Post: The game is drawn
         */
        @Override
        public void paintComponent (Graphics g) {
            //Allows for nicer graphics (ex. anti-aliasing)
            Graphics2D g2d = (Graphics2D) g;

            //Creates a gray background
            g2d.setColor(Color.darkGray);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

            //Creates a black background for the board
            g2d.setColor(Color.black);
            g2d.fillRect(15, 15, 600, 600);

            //Draws a smaller rectangle within the fruit to represent the fruit
            //Smaller rectangles are drawn so the elements can touch each other without looking like they are touching
            g2d.setColor(fruitColor);
            g2d.fillRect((int)fruit.getX()+2,(int)fruit.getY()+2,16,16);

            //Draws the entire player (using the same small rectangles as above)
            g2d.setColor(playerColor);
            for (Rectangle element : player.getTail()){
                g2d.fillRect((int)element.getX()+2,(int)element.getY()+2,16,16);
            }

            //If the start is to be displayed draws the start text
            if (displayStart){
                g2d.setColor(Color.white);
                g2d.setFont(font);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawString("Press an arrow key to start", 155, 100);
            }

            //If the end is to be displayed draws the end text
            if (displayEnd){
                g2d.setColor(Color.white);
                g2d.setFont(font);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawString("Game Over", 250, 100);

                g2d.setFont(smallFont);
                g2d.drawString("Press 'r' to restart", 257, 120);
            }

            //Draws the borders around the game
            //Covers the head of the snake going out of bounds
            g2d.setColor(Color.darkGray);
            g2d.fillRect(0, 0, 15, this.getHeight());
            g2d.fillRect(615, 0, 30, this.getHeight());
            g2d.fillRect(0, 0, this.getWidth(), 15);
            g2d.fillRect(0, 615, this.getWidth(), 75);

            //Draws the score
            g2d.setFont(font);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.black);
            g2d.drawString("Score: " + score, 20, 643);

            //If the rules are to be displayed draws a black box over the board and draws the rules
            if (displayRules){
                g2d.setColor(Color.black);
                g2d.fillRect(15, 15, 600, 600);
                g2d.drawImage(rules,140,50,null);
            }
        }
    }

    /**
     * Handles key presses
     * Pre: there is a char called pressedKey
     * Post: The appropriate action is taken depending on the key press
     */
    @Override
    public void keyPressed(KeyEvent event) {
        //If the right arrow key is pressed set pressed key to r
        if (event.getKeyCode() == 39){ 
            pressedKey = 'r';
        }
        //If the left arrow key is pressed set pressed key to l
        else if (event.getKeyCode() == 37){ 
            pressedKey = 'l';
        }
        //If the up arrow key is pressed set pressed key to u
        else if (event.getKeyCode()== 40){
            pressedKey = 'u';
        }
        //If the down arrow key is pressed set pressed key to d
        else if (event.getKeyCode() == 38){
            pressedKey = 'd';
        }
        //If r is pressed tell the game to restart
        else if (event.getKeyChar() == 'r'){
            restartOption = true;
        }
        //If Enter is pressed hide the rules
        else if (event.getKeyCode() == 10){ //Enter
            displayRules = false;
        } 
        //If space is pressed and the rules are not shown
        else if (event.getKeyChar() == ' ' && displayRules == false){
            cycleColors();
            //If the game is not running (and being redrawn), redraw the screen 
            if (!(displayStart == false && displayEnd == false)){
                canvas.repaint();
            }

        }
    }

    /**
     * Sets the direction the eel will travel in
     * Pre: the is a pressedKey char and the eel is created
     * Post: The eel will begin moving in the indicated direction
     */
    public void setMovements(){

        //If the eel is traveling in the opposite direction than what was pressed,
        //change the indicated direction to the current direction
        //This is to stop the eel from going back on itself and eating itself

        if (player.getMoveX() == -1 && pressedKey == 'r'){
            pressedKey = 'l';
        }
        else if (player.getMoveX() == 1 && pressedKey == 'l'){
            pressedKey = 'r';
        }
        else if (player.getMoveY() == -1 && pressedKey == 'u'){
            pressedKey = 'd';
        }
        else if (player.getMoveY() == 1 && pressedKey == 'd'){
            pressedKey = 'u';
        }

        //Depending on which key is pressed, the x and y trajectory of the eel are set
        switch (pressedKey){
        case 'r': player.setMoveX(1); player.setMoveY(0);break;
        case 'l': player.setMoveX(-1); player.setMoveY(0);break;
        case 'u': player.setMoveY(1);player.setMoveX(0);break;
        case 'd': player.setMoveY(-1);player.setMoveX(0);break;
        }

    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {   
    }


}
