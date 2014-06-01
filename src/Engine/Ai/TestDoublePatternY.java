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
public class TestDoublePatternY extends GhostBase
{

    public TestDoublePatternY()
    {
        super();
    }
    
    @Override
    public DataTransport doTest(GameState field, final GameState.State stone)
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
                int stonePlayer = 0;
                for (int yy = y; yy < y + 4; yy++)
                {
                    //x-axis pattern loop
                    if (isThisStone(field, stone, yy, x))
                    {
                        // count all human stone
                        stonePlayer++;
                    }
                    else
                    {
                        stonePlayer = 0;
                    }

                    if (stonePlayer == 2)
                    {
                        // check are the two stones already blocked
                        if (isEmptyStone(field, yy + 1, x))
                        {
                            // the third stone is free
                            if (checkNumberIsIntelligence(field, x))
                            {
                                return new DataTransport(x);
                            }
                            else
                            {
                                return null;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
