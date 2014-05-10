/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.GameState.State;
import Gui.Field;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Yves Studer
 */
public class Game
{

    public enum Opponent
    {

        HUMAN, AI
    }
    private GameState field;
    private Opponent opponent;
    private Ghost ai;
    private Field ui;
    private boolean gameDecided;

    public Game(Opponent opponent)
    {
        field = new GameState(6, 7);
        this.opponent = opponent;
        gameDecided = false;
        if (opponent == Opponent.AI)
        {
            ai = new Ghost();
        }
        else
        {
            //ToDo create TCP-Socket
        }
        ui = new Field(this);
    }

    /**
     * Method is used to inform about a new game-size
     *
     * @author Yves Studer
     * @param newX new counts of x stones
     * @param newY new counts of y stones
     */
    public void resizeField(final int newY, final int newX)
    {
        field = new GameState(newY, newX);
        gameDecided = false;
    }

    /**
     * Method is used to store the current game
     *
     * @author Yves Studer
     * @param path Path to soring
     */
    public void storeGame(final String path)
    {
        if (opponent == Opponent.HUMAN) //disable storing mechanism during a game against humans
        {
            return;
        }

        try (FileOutputStream aFileOutputStream = new FileOutputStream(path + ".cofo");
                ObjectOutputStream aObjectOutputStream = new ObjectOutputStream(aFileOutputStream))
        {
            aObjectOutputStream.writeObject(field);
            aObjectOutputStream.close();
        }
        catch (Exception e)
        {
            System.out.println("Exception: " + e.getMessage() + "\nvoid storeGame(final String path)");
        }
    }

    /**
     * Method is used to load a old game
     *
     * @author Yves Studer
     * @param path Path to soring
     */
    public void restoreGame(final String path)
    {
        if (opponent == Opponent.HUMAN) //disable storing mechanism during a game against humans
        {
            return;
        }

        try (FileInputStream aFileInputStream = new FileInputStream(path);
                ObjectInputStream aObjectInputStream = new ObjectInputStream(aFileInputStream))
        {
            Object o = aObjectInputStream.readObject();
            aObjectInputStream.close();

            field = (GameState) o;
            ui.setStone(field);
        }
        catch (Exception e)
        {
            System.out.println("Exception: " + e.getMessage() + "\nvoid restoreGame(final String path)");
        }
    }

    /**
     * Method is used to inform about the new turn from UI
     *
     * @author Yves Studer
     * @param uiTurn DataTransport-Objet with the new turn
     * @param actor Source identification
     */
    private void TurnPreformed(final DataTransport uiTurn, final State actor)
    {
        final int x = uiTurn.getX();
        int y = 0;
        for (; y < field.getYsize(); y++)
        {
            if (field.getStone(y, x) == State.EMPTY)
            {
                break;
            }
        }
        field.setStone(y, x, actor);
        final int decision = TestIfWon();
        switch (decision)
        {
            case -2:
                gameDecided = true;
                //ToDo inform Field we made a draw
                break;
            case -1:
                gameDecided = true;
                //ToDo inform Field we lost
                break;
            case 1:
                gameDecided = true;
                //ToDo inform Field we won
                break;
            default:
                // do nothing
                break;
        }
    }

    /**
     * Method is used to inform about the new turn from UI
     *
     * @author Yves Studer
     * @param uiTurn DataTransport-Objet with the new turn
     */
    public void UiTurnPreformed(final DataTransport uiTurn)
    {
        if (gameDecided == true)
        {
            return;
        }

        field.setMyTurn(false);
        TurnPreformed(uiTurn, State.MINE);
        if (opponent == Opponent.AI)
        {
            final DataTransport tmp = ai.DoTurn(field);
            TurnPreformed(tmp, State.OTHER);
            field.setMyTurn(true);
            ui.setStone(field);
        }
        else
        {
            //ToDo send to TCP
        }
    }

    /**
     * Method is used to inform about the new turn from the other player
     *
     * @author Yves Studer
     * @param tcpTurn DataTransport-Objet with the new turn
     */
    public void TcpTurnPreformed(final DataTransport tcpTurn)
    {
        field.setMyTurn(true);
        TurnPreformed(tcpTurn, State.OTHER);
        ui.setStone(field);
    }

    /**
     * Method to detect if a player has won. This method tests all cobinations:
     * on X-Axis, on Y-Axis and both diagonal-directions
     *
     * @author Yves Studer
     * @return returns -1 if we lost, 1 if we won -2 if the game ended tie and
     * otherwise 0
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
     * @return returns -1 if we lost, 1 if we won and otherwise 0
     */
    private int TestWinOnXaxis()
    {
        System.out.println("x-Axis-Checker");
        int mySuccessCounter = 0;
        int otherSuccessCounter = 0;
        for (int y = 0; y < field.getYsize(); y++)
        {
            otherSuccessCounter = 0;
            mySuccessCounter = 0;
            for (int x = 0; x < field.getXsize(); x++)
            {
                //System.out.print("" + x + " " + y + "   ");
                State currentFieldPart = field.getStone(y, x);
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
     * @return returns -1 if we lost, 1 if we won and otherwise 0
     */
    private int TestWinOnYaxis()
    {
        System.out.println("y-Axis-Checker");
        int mySuccessCounter = 0;
        int otherSuccessCounter = 0;
        for (int x = 0; x < field.getXsize(); x++)
        {
            otherSuccessCounter = 0;
            mySuccessCounter = 0;
            for (int y = 0; y < field.getYsize(); y++)
            {
                State currentFieldPart = field.getStone(y, x);
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
     * @return returns -1 if we lost, 1 if we won and otherwise 0
     */
    public int TestWinOnDiagAxis()
    {
        System.out.println("xy-Axis-Checker");
        final int xSize = field.getXsize();
        final int ySize = field.getYsize();
        //ToDo optimise the mechanism by add -3 on y- and x-loop
        int mySuccessCounter = 0;
        int otherSuccessCounter = 0;
        for (int i = 0; i < 2; i++) //used for both diagonal directions
        {
            otherSuccessCounter = 0;
            mySuccessCounter = 0;
            for (int yt = 0; yt < ySize; yt++) // y-loop
            {
                otherSuccessCounter = 0;
                mySuccessCounter = 0;
                for (int xt = 0; xt < xSize; xt++) // x-loop
                {
                    otherSuccessCounter = 0;
                    mySuccessCounter = 0;
                    for (int y = yt, x = xt; x < xSize; x++, y++) // diagonal-loop
                    {
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

                        //System.out.print("" + xx + " " + yy + "   ");
                        State currentFieldPart = field.getStone(yy, xx);
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
                            //System.out.println("diag-Checker test x=" + xx + " y= " + yy + " EMPTY");
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
