/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.GameState.State;
import java.lang.reflect.InvocationTargetException;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *
 * @author Yves
 */
public class Ghost
{

    private int counter = 0;
    private final Random randomGenerator;
    private final ArrayList<String> methodList = new ArrayList<>();
    private final ArrayList<State> methodArgs = new ArrayList<>();

    public Ghost()
    {
        randomGenerator = new Random();
        counter = 0;
        methodList.add("checkTripplePatternX");
        methodArgs.add(State.OTHER);
        methodList.add("checkTripplePatternY");
        methodArgs.add(State.OTHER);
        methodList.add("checkTripplePatternXY");
        methodArgs.add(State.OTHER);
        methodList.add("checkTripplePatternX");
        methodArgs.add(State.MINE);
        methodList.add("checkTripplePatternY");
        methodArgs.add(State.MINE);
        methodList.add("checkTripplePatternXY");
        methodArgs.add(State.MINE);
        methodList.add("checkDoublePatternX");
        methodArgs.add(State.MINE);
        methodList.add("checkDoublePatternY");
        methodArgs.add(State.MINE);
        methodList.add("checkDoublePatternX");
        methodArgs.add(State.OTHER);
        methodList.add("checkDoublePatternY");
        methodArgs.add(State.OTHER);
    }

    /**
     * Create a random uniformly distributed number
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @return A random number between the given boraders
     */
    private int random(final GameState field)
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
        while (field.getStone(field.getYsize() - 1, z) != State.EMPTY);
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
    private int random(final int first, final int second)
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

    /**
     * Check if the given column empty
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @param x specified row number
     * @return true if the colomn is empty
     */
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

    /**
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @param stone owner of the stone
     * @param y y-position
     * @param x x-position
     * @return true if the stone matchs with the given stone argument
     */
    private boolean isThisStone(final GameState field, final State stone, final int y, final int x)
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
    private boolean isEmptyStone(final GameState field, final int y, final int x)
    {
        return field.getStone(y, x) == State.EMPTY;
    }

    /**
     * Check the position, if the human can win on the next turn, the method
     * will return false
     *
     * @param field representation of the current field
     * @param x specified x position
     * @return true if the turn is good otherwise false
     */
    private boolean checkNumberIsIntelligence(final GameState field, final int x)
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
            if (isThisStone(field, State.MINE, y + 1, xx))
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
            if (isThisStone(field, State.MINE, y + 1, xx))
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
                if (isThisStone(field, State.MINE, yy, xx))
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
                if (isThisStone(field, State.MINE, yy, xx))
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
    private DataTransport createRandomTurn(final GameState field, final int first, final int second)
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
    private DataTransport createRandomTurn(final GameState field)
    {
        return createRandomTurn(field, -1, -1);
    }

    /**
     * Check are two stones of the same owner in a fourth-pattern (y-direction)
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @param stone owner
     * @return a DataTransoprt object with the x-value, if the pattern was
     * found, otherwise a null pointer will be returned
     */
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

    /**
     * Check are two stones of the same owner in a fourth-pattern (x-direction)
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @param stone owner
     * @return DataTransoprt object with the x-value, if the pattern was found,
     * otherwise a null pointer will be returned
     */
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

    /**
     * Check are three stones of the same owner in a fourth-pattern (both
     * diagonals)
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @param stone owner
     * @return DataTransoprt object with the x-value, if the pattern was found,
     * otherwise a null pointer will be returned
     */
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

    private DataTransport methodCaller(final Method method, final GameState field, final State stone)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return (DataTransport) method.invoke(this, field, stone);
    }

    /**
     * Method to call the AI. The order of calculation for a turn is: - AI try
     * to win in x-, y- and both diagonal-directions - AI tries to prevent the
     * gain of the player - If only two stones of the player is in a foursome
     * pattern, a stone is set in. So the player is blocked - If only two stones
     * of the AI is in a foursome pattern, a stone is set in to try to win
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @return DataTransoprt object with the x-value
     */
    public DataTransport DoTurn(final GameState field)
    {
        try
        {
            for (int i = 0; i < methodList.size(); i++)
            {
                final Method method = Ghost.class.getDeclaredMethod(methodList.get(i), GameState.class, GameState.State.class);
                DataTransport tmp = (DataTransport) methodCaller(method, field, methodArgs.get(i));
                if (tmp != null)
                {
                    return tmp;
                }
            }
        }
        catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex)
        {
            Logger.getLogger(Ghost.class.getName()).log(Level.SEVERE, null, ex);
        }
        return createRandomTurn(field);
    }
}
