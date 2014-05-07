/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.GameState.State;

/**
 *
 * @author Yves Studer
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
    
    /**
     * Method is used to inform about a new game-size
     * @author Yves Studer
     * @param newX new counts of x stones
     * @param newY new counts of y stones
     */ 
    public void resizeField(int newX, int newY)
    {
        fieldXsize = newX;
        fieldYsize = newY;
        field = new GameState(fieldXsize, fieldYsize);
    }
    
    /**
     * Method is used to inform about the new turn from UI
     * @author Yves Studer
     * @param uiTurn DataTransport-Objet with the new turn
     */    
    public void UiTurnPreformed(DataTransport uiTurn)
    {
        int x = uiTurn.getX();
        int y=0;
        for( ; y<fieldYsize ; y++)
        {
            if(field.getStone(y, x) == State.EMPTY)
                break;
        }
        field.setStone(y, x, State.MINE);
        TestIfWon();
    }
      
    /**
     * Method is used to inform about the new turn from the other player
     * @author Yves Studer
     * @param tcpTurn DataTransport-Objet with the new turn
     */  
    public void TcpTurnPreformed(DataTransport tcpTurn)
    {
        int x = tcpTurn.getX();
        int y=0;
        for( ; y<fieldYsize ; y++)
        {
            if(field.getStone(y, x) == State.EMPTY)
                break;
        }
        field.setStone(y, x, State.OTHER);
        TestIfWon();
    }
    
    /**
     * Method to detect if a player has won. This method tests all cobinations:
     * on X-Axis, on Y-Axis and both diagonal-directions
     * @author Yves Studer
     * @return returns -1 if we lost, 1 if we won and otherwise 0
     */  
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

    /**
     * Method to detect if a player has won with 4 in series on X-axis
     * @author Yves Studer
     * @return returns -1 if we lost, 1 if we won and otherwise 0
     */ 
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

    
    /**
     * Method to detect if a player has won with 4 in series on Y-axis
     * @author Yves Studer
     * @return returns -1 if we lost, 1 if we won and otherwise 0
     */
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

    
    /**
     * Method to detect if a player has won with 4 in diagonal direction.
     * This Method tests both diagonal directions.
     * @author Yves Studer
     * @return returns -1 if we lost, 1 if we won and otherwise 0
     */
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
