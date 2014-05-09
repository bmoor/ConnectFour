/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.GameState.State;

/**
 *
 * @author Yves
 */
public class Ghost
{

    public Ghost()
    {
    }

    public DataTransport DoTurn(GameState field)
    {
        int z;
        do
        {
            z = (int) (Math.random() * field.getXsize());
        }
        while (field.getStone(field.getYsize() - 1, z) != State.EMPTY);

        System.out.println("AI-generierte Zahl = " + z+"-----------------\n---------------------------------------");
        return new DataTransport(z);
    }
}
