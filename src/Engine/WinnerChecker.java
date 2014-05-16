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
public class WinnerChecker
{
    
    /**
     * Method to detect if a player has won. This method tests all cobinations:
     * on X-Axis, on Y-Axis and both diagonal-directions
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @return returns -1 if we lost, 1 if we won -2 if the game ended tie and
     * otherwise 0
     */
    public static int Run(final GameState field)
    {
        int tmp = TestWinOnXaxis(field);
        if (tmp == 0)
        {
            tmp = TestWinOnYaxis(field);
            if (tmp == 0)
            {
                tmp = TestWinOnDiagAxis(field);
                if (tmp == 0)
                {
                    if (field.getRemainingTurns() <= 0)
                    {
                        tmp = -2;
                        System.out.println("------------------------------\nUnentschieden\n------------------------------------");
                    }
                }
            }
        }
        return tmp;
    }

    /**
     * Method to detect if a player has won with 4 in series on X-axis
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @return returns -1 if we lost, 1 if we won and otherwise 0
     */
    private static int TestWinOnXaxis(final GameState field)
    {
        int mySuccessCounter = 0;
        int otherSuccessCounter = 0;
        for (int y = 0; y < field.getYsize(); y++)
        {
            otherSuccessCounter = 0;
            mySuccessCounter = 0;
            for (int x = 0; x < field.getXsize(); x++)
            {
                GameState.State currentFieldPart = field.getStone(y, x);
                if (currentFieldPart == GameState.State.OTHER)
                {
                    otherSuccessCounter++;
                    mySuccessCounter = 0;
                }
                else if (currentFieldPart == GameState.State.MINE)
                {
                    otherSuccessCounter = 0;
                    mySuccessCounter++;
                }
                else
                {
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                }
                if (mySuccessCounter == 4)
                {
                    System.out.println("I won");
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                    return 1;
                }
                if (otherSuccessCounter == 4)
                {
                    System.out.println("I lost");
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                    return -1;
                }
            }
        }
        return 0;
    }

    /**
     * Method to detect if a player has won with 4 in series on Y-axis
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @return returns -1 if we lost, 1 if we won and otherwise 0
     */
    private static int TestWinOnYaxis(final GameState field)
    {
        int mySuccessCounter = 0;
        int otherSuccessCounter = 0;
        for (int x = 0; x < field.getXsize(); x++)
        {
            otherSuccessCounter = 0;
            mySuccessCounter = 0;
            for (int y = 0; y < field.getYsize(); y++)
            {
                GameState.State currentFieldPart = field.getStone(y, x);
                if (currentFieldPart == GameState.State.OTHER)
                {
                    otherSuccessCounter++;
                    mySuccessCounter = 0;
                }
                else if (currentFieldPart == GameState.State.MINE)
                {
                    otherSuccessCounter = 0;
                    mySuccessCounter++;
                }
                else
                {
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                }
                if (mySuccessCounter == 4)
                {
                    System.out.println("I won");
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                    return 1;
                }
                if (otherSuccessCounter == 4)
                {
                    System.out.println("I lost");
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                    return -1;
                }
            }
        }
        return 0;
    }

    /**
     * Method to detect if a player has won with 4 in diagonal direction. This
     * Method tests both diagonal directions.
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @return returns -1 if we lost, 1 if we won and otherwise 0
     */
    private static int TestWinOnDiagAxis(final GameState field)
    {
        final int xSize = field.getXsize();
        final int ySize = field.getYsize();
        int mySuccessCounter = 0;
        int otherSuccessCounter = 0;
        for (int i = 0; i < 2; i++)
        {
            //used for both diagonal directions
            otherSuccessCounter = 0;
            mySuccessCounter = 0;
            for (int yt = 0; yt < ySize - 3; yt++)
            {
                // y-loop
                otherSuccessCounter = 0;
                mySuccessCounter = 0;
                for (int xt = 0; xt < xSize - 3; xt++)
                {
                    // x-loop
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                    for (int y = yt, x = xt; x < xSize; x++, y++)
                    {
                        // diagonal-loop
                        int yy = y;
                        int xx;
                        if (i == 0)
                        {
                            xx = x;
                        }
                        else
                        {
                            xx = (xSize - 1) - x;
                        }

                        if (y > (ySize - 1))
                        {
                            continue;
                        }
                        GameState.State currentFieldPart = field.getStone(yy, xx);
                        if (currentFieldPart == GameState.State.OTHER)
                        {
                            otherSuccessCounter++;
                            mySuccessCounter = 0;
                        }
                        else if (currentFieldPart == GameState.State.MINE)
                        {
                            otherSuccessCounter = 0;
                            mySuccessCounter++;
                        }
                        else
                        {
                            otherSuccessCounter = 0;
                            mySuccessCounter = 0;
                        }
                        if (mySuccessCounter == 4)
                        {
                            System.out.println("I won");
                            otherSuccessCounter = 0;
                            mySuccessCounter = 0;
                            return 1;
                        }
                        if (otherSuccessCounter == 4)
                        {
                            System.out.println("I lost");
                            otherSuccessCounter = 0;
                            mySuccessCounter = 0;
                            return -1;
                        }
                    }
                }
            }
        }
        return 0;
    }
}
