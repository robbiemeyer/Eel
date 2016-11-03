/* Robbie Meyer
 * June 15, 2015
 * Eel
 * Plays the game of Eel (snake)
 */

package eel;

/**
 * This class runs the game
 */

public class Start {
    
    /**
     * Starts the game
     * Pre: n/a
     * Post: The game is run
     */
    public static void main(String[] args) {
        //Create the game object
        Game window = new Game();
        
        //Start the game window
        window.go();
        
        //Start the game, until the window is closed
		while (true){
        	window.beginGame();
		}
    }

    
}
