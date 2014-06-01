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
public class TestDoublePatternX extends GhostBase
{

    public TestDoublePatternX()
    {
        super();
    }

    
    /**
     * Check are two stones of the same owner in a fourth-pattern (x-direction)
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @param stone owner
     * @return DataTransoprt object with the x-value, if the pattern was found,
     * otherwise a null pointer will be returned
     */
    @Override
    public DataTransport doTest(GameState field, final GameState.State stone)
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
                int stonePlayer = 0;
                int stoneEmpty = 0;
                for (int xx = x; xx < x + 4; xx++)
                {
                    //x-axis pattern loop
                    if (isThisStone(field, stone, y, xx))
                    {
                        // count all human stone
                        stonePlayer++;
                    }
                    else if (isEmptyStone(field, y, xx))
                    {
                        // count all human stone
                        stoneEmpty++;
                    }
                }
                // here are quantity off all stone in pattern known
                if ((stonePlayer == 2) && (stoneEmpty == 2))
                {
                    // in pattern are just two player-stones and none of the AI
                    // here should the human be blocked
                    int first = -1, second = -1;
                    for (int xx = x; xx < x + 4; xx++)
                    {
                        if (isEmptyStone(field, y, xx))
                        {
                            if (first == -1)
                            {
                                first = xx;
                            }
                            else
                            {
                                second = xx;
                            }
                        }
                    }
                    if (y == 0)
                    {
                        return createRandomTurn(field, first, second);
                    }
                    else
                    {
                        if (isEmptyStone(field, y - 1, first))
                        {
                            if (isEmptyStone(field, y - 1, second))
                            {
                                //do nothing both possibility are not available
                            }
                            else
                            {
                                if (checkNumberIsIntelligence(field, second))
                                {
                                    return new DataTransport(second);
                                }
                                else
                                {
                                    return null;
                                }
                            }
                        }
                        else
                        {
                            if (isEmptyStone(field, y - 1, second))
                            {
                                if (checkNumberIsIntelligence(field, first))
                                {
                                    return new DataTransport(first);
                                }
                                else
                                {
                                    return null;
                                }
                            }
                            else
                            {
                                return createRandomTurn(field, first, second);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
