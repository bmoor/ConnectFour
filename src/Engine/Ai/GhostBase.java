/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Engine.Ai;

import Engine.DataTransport;
import Engine.GameState;
import java.util.Random;

/**
 *
 * @author Yves
 */
public abstract class GhostBase
{
    private final Random randomGenerator;
    
    protected GhostBase()
    {
        randomGenerator = new Random();        
    }
    
    protected int random(final GameState field)
    {
        int z;
        do
        {
            z = randomGenerator.nextInt(field.getYsize() + 1);
//            if (z == 0)
//            {
//                // this code is needed to pimp the random-generator. 
//                // The uniformly distribution is not so nice and with this 
//                // modification it's better
//                if (counter % 2 != 0)
//                {
//                    do
//                    {
//                        z =randomGenerator.nextInt(field.getYsize()+1);
//                    }
//                    while (z == 0);
//                }
//                counter++;
//            }
        }
        while (field.getStone(field.getYsize() - 1, z) != GameState.State.EMPTY);
//        String eingabe = "";
//        try
//        {
//            InputStreamReader isr = new InputStreamReader(System.in);
//            BufferedReader br = new BufferedReader(isr);
//            eingabe = br.readLine();
//            z = Integer.parseInt(eingabe);
//        }
//        catch (IOException e)
//        {
//        }
        System.out.println("Random-Zahl = " + z);
        return z;
    }

    /**
     * Create a random number with the value of the first or second argument
     *
     * @author Yves Studer
     * @param first first number
     * @param second second number
     * @return Randomly the first or the second number
     */
    protected int random(final int first, final int second)
    {
        if (randomGenerator.nextBoolean())
        {
            return first;
        }
        else
        {
            return second;
        }
    }

    /**
     * Check if the given row empty
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @param y specified row number
     * @return true if the row is empty
     */
    protected boolean isRowFull(final GameState field, final int y)
    {
        for (int x = 0; x < field.getXsize(); x++)
        {
            if (field.getStone(y, x) == GameState.State.EMPTY)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the given column empty
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @param x specified row number
     * @return true if the colomn is empty
     */
    protected boolean isColumnFull(final GameState field, final int x)
    {
        for (int y = 0; y < field.getYsize(); y++)
        {
            if (field.getStone(y, x) == GameState.State.EMPTY)
            {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @param stone owner of the stone
     * @param y y-position
     * @param x x-position
     * @return true if the stone matchs with the given stone argument
     */
    protected boolean isThisStone(final GameState field, final GameState.State stone, final int y, final int x)
    {
        return field.getStone(y, x) == stone;
    }

    /**
     * Check if a stone place is empty
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @param y y-position
     * @param x x-position
     * @return true if specified stone place is empty
     */
    protected boolean isEmptyStone(final GameState field, final int y, final int x)
    {
        return field.getStone(y, x) == GameState.State.EMPTY;
    }

    /**
     * Check the position, if the human can win on the next turn, the method
     * will return false
     *
     * @param field representation of the current field
     * @param x specified x position
     * @return true if the turn is good otherwise false
     */
    protected boolean checkNumberIsIntelligence(final GameState field, final int x)
    {
        int yt = 0;
        for (; yt < field.getYsize(); yt++)
        {
            if (isEmptyStone(field, yt, x))
            {
                break;
            }
        }
        if (yt == field.getYsize() - 1)
        {
            // we are on top of the game field. There is nothing to check
            return true;
        }
        final int y = yt;// we have to check on the next y-level

        int c1 = 0;
        for (int xx = x - 1; (xx > x - 4) && (xx >= 0); xx--)
        {
            if (isThisStone(field, GameState.State.MINE, y + 1, xx))
            {
                c1++;
            }
            else
            {
                break;
            }
        }

        int c2 = 0;
        for (int xx = x + 1; (xx < field.getXsize()) && (xx < x + 4 - c1); xx++)
        {
            if (isThisStone(field, GameState.State.MINE, y + 1, xx))
            {
                c2++;
            }
            else
            {
                break;
            }
        }
        if (c1 + c2 + 1 == 4)
        {
            // random number is bad, because the human can win in the next turn
            return false;
        }

        for (int i = 0; i < 2; i++)
        {
            // i = 0 means positive gradient 
            // i = 1 means negative gradient
            c1 = 0;
            c2 = 0;

            int xx;
            if (i == 0)
            {
                xx = x - 1;
            }
            else
            {
                xx = x + 1;
            }
            for (int yy = y; (yy > y - 4) && (yy >= 0) && (xx > 0) && (xx < field.getXsize()); yy--)
            {
                if (isThisStone(field, GameState.State.MINE, yy, xx))
                {
                    c1++;
                }
                else
                {
                    break;
                }
                if (i == 0)
                {
                    xx--;
                }
                else
                {
                    xx++;
                }
            }

            if (i == 0)
            {
                xx = x + 1;
            }
            else
            {
                xx = x - 1;
            }
            for (int yy = y + 2; (yy < field.getYsize()) && (yy < y + 5 - c1) && (xx > 0) && (xx < field.getXsize()); yy++)
            {
                if (isThisStone(field, GameState.State.MINE, yy, xx))
                {
                    c2++;
                }
                else
                {
                    break;
                }
                if (i == 0)
                {
                    xx++;
                }
                else
                {
                    xx--;
                }
            }
            if (c1 + c2 + 1 == 4)
            {
                // random number is bad, because the human can win in the next turn
                return false;
            }
        }
        return true;
    }

    /**
     * Create a random number from two given possibilities. When the random turn
     * provide a win for the human in the next turn, it will calculate an other
     * random number.
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @param first first number
     * @param second second number
     * @return a random number
     */
    protected DataTransport createRandomTurn(final GameState field, final int first, final int second)
    {
        if (first < 0)
        {
            int r;
            int i = 0;
            boolean b = false;
            do
            {
                r = random(field);
                b = checkNumberIsIntelligence(field, r);
                i++;
            }
            while ((i < 50) && !b);
            return new DataTransport(r);
        }
        else
        {
            if (checkNumberIsIntelligence(field, first))
            {
                if (checkNumberIsIntelligence(field, second))
                {
                    return new DataTransport(random(first, second));
                }
                else
                {
                    return new DataTransport(first);
                }
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
    }
    
    /**
     * Create a random number. When the random turn provide a win for the human
     * in the next turn, it will calculate an other random number.
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @return a random number
     */
    public DataTransport createRandomTurn(final GameState field)
    {
        return createRandomTurn(field, -1, -1);
    }
    
    /**
     * Abstract method to call all the ai-tests
     * 
     * @author Yves Studer
     * @param field representation of the current field
     * @param stone owner
     * @return a DataTransort object with the new turn or null
     */
    abstract public DataTransport doTest(GameState field,final GameState.State stone);
}
