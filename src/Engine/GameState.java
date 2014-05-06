/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.util.Enumeration;

/**
 *
 * @author Yves
 */
public class GameState
{

    public enum State
    {
        EMPTY, MINE, OTHER
    }
    private State[][] field;
    private boolean myTurn = false;
    public GameState(int x, int y)
    {
        field = new State[x][y];
        for(int i=0 ; i<x ; i++)
        {
            for(int j=0 ; j<y ; j++)
                field[i][j]=State.EMPTY;
        }
    }
    
    public State getField(int x, int y)
    {
        return field[x][y];
    }

    public void setField(int x, int y, State state)
    {
        field[x][y]=state;
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
