/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.io.Serializable;

/**
 *
 * @author Yves
 */
public class GameState implements Serializable
{

    /**
     * Possible states of a Stone
     */
    public enum State
    {
        EMPTY, MINE, OTHER
    }
    private final State[][] field;
    private boolean myTurn;
    private final int x;
    private final int y;
    private int turnsCounter;

    /**
     * Default constructor.
     * 
     * @param y y-size of the game field
     * @param x x-size of the game field
     */
    public GameState(int y, int x)
    {
        this.x=x;
        this.y=y;
        turnsCounter = 0;
        myTurn = false;
        field = new State[y][x];
        for(int i=0 ; i<x ; i++)
        {
            for(int j=0 ; j<y ; j++)
                field[j][i]=State.EMPTY;
        }
    }
    
    /**
     * Returns the number of remaining turns in a game.
     * 
     * @return count of remaining turns
     */
    public int getRemainingTurns()
    {
        return y*x-turnsCounter;
    }
    
    /**
     * Returns the number of column in the game field.
     * 
     * @return the nomber of column in the game field
     */
    public int getXsize()
    {
        return x;
    }
    
    /**
     * Returns the number of rows in the game field.
     * 
     * @return the nomber of rows in the game field
     */
    public int getYsize()
    {
        return y;
    }
    
    /**
     * Return the stone state on the speified place.
     * 
     * @param y y-position
     * @param x x-position
     * @return the stone state on the speified place
     */
    public State getStone(final int y, final int x)
    {
        return field[y][x];
    }

    /**
     * Set the given stone on the given place
     * 
     * @param y y-position
     * @param x x-position
     * @param state owner of the stone
     */
    public void setStone(final int y, final int x, final State state)
    {
        field[y][x]=state;
        turnsCounter++;
    }
    
    /**
     * 
     * @return true if it's the humans turn
     */
    public boolean isMyTurn()
    {
        return myTurn;
    }

    /**
     * Set the turn-state
     * 
     * @param myTurn should be true if it's humans turn
     */
    public void setMyTurn(final boolean myTurn)
    {
        this.myTurn = myTurn;
    }
}
