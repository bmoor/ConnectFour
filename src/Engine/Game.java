/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.GameState.State;
import Gui.Field;
import Gui.Lobby;
import Network.Player;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Pattern;

/**
 *
 * @author Yves Studer
 */
public class Game
{

    private Field ui;
    private GameState field;
    private Ghost ai = null;
    private boolean gameDecided;
    private Player opponent = null;
    private Lobby lobby;

    /**
     * Constructor for a game against AI
     *
     * @author Yves Studer
     * @param aLobby the poiter to the lobby
     */
    public Game(Lobby aLobby)
    {
        ai = new Ghost();
        lobby = aLobby;
        init(true);
    }

    /**
     * Constructor for a game against human
     *
     * @author Yves Studer
     * @param aPlayer Handle network
     * @param aLobby pointer to the lobby
     */
    public Game(Player aPlayer, Lobby aLobby)
    {
        opponent = aPlayer;
        lobby = aLobby;
        lobby.setVisible(false);
        //Check for beginner
        boolean myTurn = false;
        if (opponent.isHost())
        {
            myTurn = true;
        }
        init(myTurn);
            }
    
    /**
     * Common initialization method
     *
     * @author Yves Studer
     */
    private void init(boolean myTurn)
    {
        gameDecided = false;
        field = new GameState(6, 7);
        field.setMyTurn(myTurn);
        ui = new Field(this, myTurn);
        //ui.setStone(field);
    }

    public boolean againstAi()
    {
        return ai != null;
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
        if (opponent != null)
        {
            DataTransport configMob = new DataTransport(0);
            configMob.setxSize(newX);
            configMob.setySize(newY);
            opponent.sendMessage(configMob);
        }
    }

    public void finish()
    {
        lobby.destroyOldGame();
    }

    /**
     * Method is used to store the current game
     *
     * @author Yves Studer
     * @param path Path to soring
     */
    public void storeGame(String path)
    {
        if (ai == null) //disable storing mechanism during a game against humans
        {
            return;
        }
        String[] pathPice = path.split(Pattern.quote("."));
        String newPath = pathPice[0];
        for (int i = 1; i < pathPice.length; i++)
        {
            if (!pathPice[i].contains("c4"))
            {
                newPath += "." + pathPice[i];
            }
        }

        try (FileOutputStream aFileOutputStream = new FileOutputStream(newPath + ".c4");
                ObjectOutputStream aObjectOutputStream = new ObjectOutputStream(aFileOutputStream))
        {
            aObjectOutputStream.writeObject(field);
            aObjectOutputStream.close();
        } catch (Exception e)
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
        if (ai == null) //disable storing mechanism during a game against humans
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
        } catch (Exception e)
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
                ui.drawn();
                break;
            case -1:
                gameDecided = true;
                ui.lost();
                break;
            case 1:
                gameDecided = true;
                ui.won();
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
        if ((ai != null) && (gameDecided == false))
        {
            final DataTransport tmp = ai.DoTurn(field);
            TurnPreformed(tmp, State.OTHER);
            field.setMyTurn(true);
            ui.setStone(field);
        }
        if (opponent != null)
        {
            opponent.sendMessage(uiTurn);
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
        if (tcpTurn.getxSize() != 0)
        {
            field = new GameState(tcpTurn.getySize(), tcpTurn.getxSize());
            field.setMyTurn(false);
            ui.resizeBoard(tcpTurn.getySize(), tcpTurn.getxSize());
        }
        else
        {
            field.setMyTurn(true);
            TurnPreformed(tcpTurn, State.OTHER);
            ui.setStone(field);
        }
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
        int mySuccessCounter = 0;
        int otherSuccessCounter = 0;
        for (int y = 0; y < field.getYsize(); y++)
        {
            otherSuccessCounter = 0;
            mySuccessCounter = 0;
            for (int x = 0; x < field.getXsize(); x++)
            {
                State currentFieldPart = field.getStone(y, x);
                if (currentFieldPart == State.OTHER)
                {
                    otherSuccessCounter++;
                    mySuccessCounter = 0;
                }
                else if (currentFieldPart == State.MINE)
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
     * @return returns -1 if we lost, 1 if we won and otherwise 0
     */
    private int TestWinOnYaxis()
    {
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
                }
                else if (currentFieldPart == State.MINE)
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
     * @return returns -1 if we lost, 1 if we won and otherwise 0
     */
    private int TestWinOnDiagAxis()
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
                        State currentFieldPart = field.getStone(yy, xx);
                        if (currentFieldPart == State.OTHER)
                        {
                            otherSuccessCounter++;
                            mySuccessCounter = 0;
                        }
                        else if (currentFieldPart == State.MINE)
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
