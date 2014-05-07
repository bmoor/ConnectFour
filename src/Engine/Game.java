/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.GameState.State;

/**
 *
 * @author bmoor
 */
public class Game
{

    GameState field;
    int fieldXsize = 7;
    int fieldYsize = 6;
    boolean won = false;
    boolean lost = false;

    public Game()
    {
        field = new GameState(fieldXsize, fieldYsize);
    }

    private int TestIfWon()
    {
        int tmp = TestWinOnXaxis();
        if (tmp == 0)
        {
            tmp = TestWinOnYaxis();
            if (tmp == 0)
            {
                tmp = TestWinOnDiagAxis();
            }
        }
        return tmp;
    }

    private int TestWinOnXaxis()
    {
        int mySuccessCounter = 0;
        int otherSuccessCounter = 0;
        for (int y = 0; y < fieldYsize; y++)
        {
            for (int x = 0; x < fieldXsize; x++)
            {
                System.out.print("" + x + " " + y + "   ");
                State currentFieldPart = field.getStone(x, y);
                if (currentFieldPart == State.OTHER)
                {
                    otherSuccessCounter++;
                    mySuccessCounter = 0;
                    System.out.println("x-Checker test x=" + x + " y= " + y + " OTHER");
                }
                else if (currentFieldPart == State.MINE)
                {
                    otherSuccessCounter = 0;
                    mySuccessCounter++;
                    System.out.println("x-Checker test x=" + x + " y= " + y + " MINE");
                }
                else
                {
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                    System.out.println("diag-Checker test x=" + x + " y= " + y + " EMPTY");
                }
                if (mySuccessCounter == 4)
                {
                    System.out.println("I won");
                    won = true;
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                    return 1;
                }
                if (otherSuccessCounter == 4)
                {
                    System.out.println("I lost");
                    lost = true;
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                    return -1;
                }
            }
        }
        return 0;
    }

    private int TestWinOnYaxis()
    {
        int mySuccessCounter = 0;
        int otherSuccessCounter = 0;
        // y-Richtungs-Checker
        for (int x = 0; x < fieldXsize; x++)
        {
            for (int y = 0; y < fieldYsize; y++)
            {
                State currentFieldPart = field.getStone(x, y);
                if (currentFieldPart == State.OTHER)
                {
                    otherSuccessCounter++;
                    mySuccessCounter = 0;
                    System.out.println("y-Checker test x=" + x + " y= " + y + " OTHER");
                }
                else if (currentFieldPart == State.MINE)
                {
                    otherSuccessCounter = 0;
                    mySuccessCounter++;
                    System.out.println("y-Checker test x=" + x + " y= " + y + " MINE");
                }
                else
                {
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                }
                if (mySuccessCounter == 4)
                {
                    System.out.println("I won");
                    won = true;
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                    return 1;
                }
                if (otherSuccessCounter == 4)
                {
                    System.out.println("I lost");
                    lost = true;
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                    return -1;
                }
            }
        }
        return 0;
    }

    public int TestWinOnDiagAxis()
    {
        int mySuccessCounter = 0;
        int otherSuccessCounter = 0;
        for (int i = 0; i < 2; i++) //used for both diagonal directions
        {
            for (int yt = 0; yt < fieldYsize; yt++)
            {
                for (int xt = 0; xt < fieldXsize; xt++)
                {
                    for (int y = yt, x = xt; x < fieldXsize; x++, y++)
                    {
                        int xx;
                        if(i==0)
                            xx=x;
                        else
                            xx=(fieldXsize-1)-x;
                        int yy=y;
                        
                        if ( (y > (fieldYsize-1) ) )
                            continue;
                        
                        System.out.print("" + xx + " " + yy + "   ");
                        State currentFieldPart = field.getStone(xx, yy);
                        if (currentFieldPart == State.OTHER)
                        {
                            otherSuccessCounter++;
                            mySuccessCounter = 0;
                            System.out.println("diag-Checker test x=" + xx + " y= " + yy + " OTHER");
                        }
                        else if (currentFieldPart == State.MINE)
                        {
                            otherSuccessCounter = 0;
                            mySuccessCounter++;
                            System.out.println("diag-Checker test x=" + xx + " y= " + yy + " MINE");
                        }
                        else
                        {
                            otherSuccessCounter = 0;
                            mySuccessCounter = 0;
                            System.out.println("diag-Checker test x=" + xx + " y= " + yy + " EMPTY");
                        }
                        if (mySuccessCounter == 4)
                        {
                            System.out.println("I won");
                            won = true;
                            otherSuccessCounter = 0;
                            mySuccessCounter = 0;
                            return 1;
                        }
                        if (otherSuccessCounter == 4)
                        {
                            System.out.println("I lost");
                            lost = true;
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
