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

    private int random(final GameState field, final int from, final int to)
    {
        int z;
        do
        {
            z = (int) (Math.random() * (to - from + 1) + from);
        }
        while (field.getStone(field.getYsize() - 1, z) != State.EMPTY);
        System.out.println("Random " + z);
        return z;
    }

    private int random(final int first, final int second)
    {
        int z = (int) (Math.random() * 2 + 1);
        System.out.println("Random spez");
        if (z == 1)
        {
            return first;
        }
        else
        {
            return second;
        }
    }

    private boolean isRowFull(final GameState field, final int y)
    {
        for (int x = 0; x < field.getXsize(); x++)
        {
            if (field.getStone(y, x) == State.EMPTY)
            {
                return false;
            }
        }
        return true;
    }

    private boolean isColumnFull(final GameState field, final int x)
    {
        for (int y = 0; y < field.getYsize(); y++)
        {
            if (field.getStone(y, x) == State.EMPTY)
            {
                return false;
            }
        }
        return true;
    }

    private boolean isThisStone(final GameState field, final State stone, final int y, final int x)
    {
        if (field.getStone(y, x) == stone)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isEmptyStone(final GameState field, final int y, final int x)
    {
        if (field.getStone(y, x) == State.EMPTY)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private DataTransport checkDoublePatternY(final GameState field, final State stone)
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
                            return new DataTransport(x);
                        }
                    }
                }
            }
        }
        return null;        
    }
    
    private DataTransport checkDoublePatternX(final GameState field, final State stone)
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
                        return new DataTransport(random(first, second));
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
                                return new DataTransport(second);
                            }
                        }
                        else
                        {
                            if (isEmptyStone(field, y - 1, second))
                            {
                                return new DataTransport(first);                                
                            }
                            else
                            {
                                return new DataTransport(random(first,second));
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private DataTransport checkTripplePatternX(final GameState field, final State stone)
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

    private DataTransport checkTripplePatternY(final GameState field, final State stone)
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

    private DataTransport checkTripplePatternXY(final GameState field, final State stone)
    {
        for (int i = 0; i < 2; i++)
        {
            for (int y = 0; y < field.getYsize() - 3; y++)
            {
                for (int x = 0; x < field.getXsize() - 3; x++)
                {
                    int c = 0;
                    for (int dy = y, dx = x; dx < x + 4; dy++, dx++)
                    {
                        int yy = dy;
                        int xx;
                        if (i == 0)
                        {
                            xx = dx;
                        }
                        else
                        {
                            xx = (field.getXsize() - 1) - dx;
                        }
                        if (dy > (field.getYsize() - 1))
                        {
                            continue;
                        }

                        if (isThisStone(field, stone, yy, xx))
                        {
                            // count all human stone
                            c++;
                        }
                    }
                    if (c == 3)
                    {
                        for (int dx = x, dy = y; dx < x + 4; dx++, dy++)
                        {
                            int yy = dy;
                            int xx;
                            if (i == 0)
                            {
                                xx = dx;
                            }
                            else
                            {
                                xx = (field.getXsize() - 1) - dx;
                            }
                            if (isEmptyStone(field, yy, xx))
                            {
                                int offset = 1;
                                if (dy == 0)
                                {
                                    offset = 0;
                                }
                                if (isEmptyStone(field, yy - offset, xx) == false)
                                {
                                    return new DataTransport(xx);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public DataTransport DoTurn(final GameState field)
    {
        DataTransport tmp;
        tmp = checkTripplePatternX(field, State.OTHER);
         if (tmp != null)
         {
         return tmp;
         }
         tmp = checkTripplePatternY(field, State.OTHER);
         if (tmp != null)
         {
         return tmp;
         }
         tmp = checkTripplePatternXY(field, State.OTHER);
         if (tmp != null)
         {
         return tmp;
         }
         tmp = checkTripplePatternX(field, State.MINE);
         if (tmp != null)
         {
         return tmp;
         }
         tmp = checkTripplePatternY(field, State.MINE);
         if (tmp != null)
         {
         return tmp;
         }
         tmp = checkTripplePatternXY(field, State.MINE);
         if (tmp != null)
         {
         return tmp;
         }
        tmp = checkDoublePatternX(field, State.MINE);
        if (tmp != null)
        {
            return tmp;
        }
        tmp = checkDoublePatternY(field, State.MINE);
        if (tmp != null)
        {
            return tmp;
        }
        return new DataTransport(random(field, 0, field.getYsize()));
    }
}
