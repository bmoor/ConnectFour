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
    private State[][] field;
    private boolean myTurn = false;
    public GameState(int y, int x)
    {
        field = new State[y][x];
        for(int i=0 ; i<x ; i++)
        {
            for(int j=0 ; j<y ; j++)
                field[j][i]=State.EMPTY;
        }
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
