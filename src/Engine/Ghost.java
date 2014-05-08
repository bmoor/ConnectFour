/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

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
        int z = (int) (Math.random()*field.getXsize());
        return new DataTransport(z);
    }
}
