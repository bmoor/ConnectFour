/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Engine.Ai;

import Engine.DataTransport;
import Engine.GameState;

/**
 *
 * @author Yves
 */
public class TestTripplePatternY extends GhostBase
{
    public TestTripplePatternY()
    {
        super();
    }
    
    /**
     * Check are three stones of the same owner in a fourth-pattern
     * (y-direction)
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @param stone owner
     * @return DataTransoprt object with the x-value, if the pattern was found,
     * otherwise a null pointer will be returned
     */
    @Override
    public DataTransport doTest(GameState field,final GameState.State stone)
    { 
        for (int x = 0; x < field.getXsize(); x++)
        {
            //x-axis loop
            if (isColumnFull(field, x))
            {
                continue;
            }
            for (int y = 0; y < field.getYsize() - 3; y++)
            {
                // y-axis loop 
                int c = 0;
                for (int yy = y; yy < y + 4; yy++)
                {
                    //x-axis pattern loop
                    if (isThisStone(field, stone, yy, x))
                    {
                        // count all human stone
                        c++;
                    }
                    else
                    {
                        c = 0;
                    }
                    if (c == 3)
                    {
                        // check are the three stones already blocked
                        if (isEmptyStone(field, yy + 1, x))
                        {
                            // the fourth stone is free
                            return new DataTransport(x);
                        }
                    }
                }
            }
        }
        return null;
    }
    
}
