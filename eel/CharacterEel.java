/* Robbie Meyer
 * June 15, 2015
 * Eel
 * Plays the game of Eel (snake)
 */

package eel;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * This class contains everything to do with the actual eel itself
 */

public class CharacterEel{

    //The current trajectory of the eel (1 is up/right, -1 is down/left)
    private int movementX = 0, movementY = 0;
    
    //The array list of rectangles that makes up the eel
    private ArrayList<Rectangle> tail = new ArrayList<Rectangle>(); 
    
    /**
     * The constructor that builds the eel
     * Pre: n/a
     * Post: An eel composed of twenty rectangle is created
     */
    public CharacterEel (Point location){
    	for (int i = 0; i < 20; i ++)
        	tail.add(new Rectangle((int)location.getX() ,(int)location.getY(),20,20));
    }
    
    /**
     * Returns the X trajectory of the eel
     * Pre: n/a
     * Post: Horizontal movement is returned
     */
    public int getMoveX (){
    	return movementX;
    }
    
    /**
     * Returns the Y trajectory of the eel
     * Pre: n/a
     * Post: Vertical movement is returned
     */
    public int getMoveY (){
    	return movementY;
    }
    
    /**
     * Sets the horizontal movement
     * Pre: n/a
     * Post: The movement is set to the desired number
     */
    public void setMoveX (int movement){
    	movementX = movement;
    }
    
    /**
     * Sets the vertical movement
     * Pre: n/a
     * Post: The movement is set to the desired number
     */
    public void setMoveY (int movement){
    	movementY = movement;
    }
    
    /**
     * Returns the eel's array
     * Pre: n/a
     * Post: The array of rectangles is returned
     */
    public ArrayList<Rectangle> getTail(){
        return tail;
    }
    
    /**
     * Moves the eel
     * Pre: n/a
     * Post: the head is moved to the next desired location, the tail shrinks away from the old location
     */
    public void moveEel (Point location){
        //The head of the eel is always the first element in the array
        Rectangle head = tail.get(0);

        //Find 1/5 of the difference between the current location and the desired location
        int differenceX =  (int)(location.getX() - head.getX()) / 5;
        int differenceY =  (int) (location.getY() - head.getY()) / 5;
        
        //For 5 times (until the current location is the desired location)
        for (int i = 0; i < 5; i++){          
            //Wait 16 milliseconds to be able to see the movement
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Move the head 1/5th of the way to the desired location
            head.setRect(head.getX() + (differenceX), head.getY() + (differenceY),20,20);
            //Add a new rectangle where the head was (newest rectangles towards start of head)
            getTail().add(new Rectangle( (int) head.getX() - (differenceX), (int) head.getY() - (differenceY), 20,20));
            
            //Remove the oldest rectangle
            getTail().remove(1);
            
            //Repaint the screen
            Game.getCanvas().repaint();
            
        }   
    }
                
    
    /**
     * Increases the length of the eel
     * Pre: n/a
     * Post: Increases the length of the eel by 12 rectangles and adds 100 points to the score
     */
    public void increaseLength (){
        //Add 12 rectangles to the end of the tail
        for (int i = 0; i < 12; i ++)
            tail.add(1, new Rectangle ((int)tail.get(0).getX(), (int)tail.get(0).getY(),20,20));
        //Increase points by 100
        Game.increaseScore(100);     
    }
    
    

}
