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
    public enum State
    {
        EMPTY, MINE, OTHER
    }
    private final State[][] field;
    private boolean myTurn;
    private final int x;
    private final int y;
    private int turnsCounter;

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
    
    public int getRemainingTurns()
    {
        return y*x-turnsCounter;
    }
    
    public int getXsize()
    {
        return x;
    }
    
    public int getYsize()
    {
        return y;
    }
    
    public State getStone(final int y, final int x)
    {
        return field[y][x];
    }

    public void setStone(final int y, final int x, final State state)
    {
        field[y][x]=state;
        turnsCounter++;
    }
    
    public boolean isMyTurn()
    {
        return myTurn;
    }

    public void setMyTurn(final boolean myTurn)
    {
        this.myTurn = myTurn;
    }
}
