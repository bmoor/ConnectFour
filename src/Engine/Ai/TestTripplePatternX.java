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
public class TestTripplePatternX extends GhostBase
{
    public TestTripplePatternX()
    {
        super();
    }
    /**
     * Check are three stones of the same owner in a fourth-pattern
     * (x-direction)
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
        for (int y = 0; y < field.getYsize(); y++)
        {
            //y-axis loop
            if (isRowFull(field, y))
            {
                continue;
            }
            for (int x = 0; x < field.getXsize() - 3; x++)
            {
                // x-axis loop 
                int c = 0;
                for (int xx = x; xx < x + 4; xx++)
                {
                    //x-axis pattern loop
                    if (isThisStone(field, stone, y, xx))
                    {
                        // count all human stone
                        c++;
                    }
                }
                // c contains the count of human stones
                if (c == 3)
                {
                    for (int xx = x; xx < x + 4; xx++)
                    {
                        if (isEmptyStone(field, y, xx))
                        {
                            if (y == 0)
                            {
                                return new DataTransport(xx);
                            }
                            if (!isEmptyStone(field, y - 1, xx))
                            {
                                return new DataTransport(xx);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
