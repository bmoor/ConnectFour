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

    GameState(int i)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public enum State
    {
        EMPTY, MINE, OTHER
    }
    private State[][] field;
    private boolean myTurn = false;
    private int x;
    private int y;
    public GameState(int y, int x)
    {
        this.x=x;
        this.y=y;
        field = new State[y][x];
        for(int i=0 ; i<x ; i++)
        {
            for(int j=0 ; j<y ; j++)
                field[j][i]=State.EMPTY;
        }
    }
    
    public int getXsize()
    {
        return x;
    }
    
    public int getYsize()
    {
        return y;
    }
    
    public State getStone(int y, int x)
    {
        return field[y][x];
    }

    public void setStone(int y, int x, State state)
    {
        field[y][x]=state;
    }
    
    public boolean isMyTurn()
    {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn)
    {
        this.myTurn = myTurn;
    }
}
