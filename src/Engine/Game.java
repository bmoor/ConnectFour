/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.GameState.State;
import Gui.Field;
import Gui.Lobby;
import Network.Player;
//import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
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
    private final Lobby lobby;

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
//
//        String st = "\"rand\"\n";
//        int n = 10;
//        for (int i = 0; i < n; i++)
//        {
//            st += ai.random(field);
//            if (i != n - 1)
//            {
//                st += "\n";
//            }
//        }
//
//        File file = new File("C:\\tmp\\random.txt");
//        try (FileWriter writer = new FileWriter(file))
//        {
//            writer.write(st);
//            writer.flush();
//            writer.close();
//        }
//        catch (IOException e)
//        {
//        }
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
    }

    /**
     * Common initialization method
     *
     * @author Yves Studer
     * @return true, if we are playing against the artificial intelligence
     */
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
        resizeMyField(newY, newX);
        if (opponent != null)
        {
            DataTransport configMob = new DataTransport(newY, newX);
            opponent.sendMessage(configMob);
            opponent.sendMessage(new DataTransport("Opponent modified field"));
        }
    }

    /**
     * Method to resize the internal representation of the gaming-field
     *
     * @author Yves Studer
     * @param newY new y size
     * @param newX new x size
     */
    private void resizeMyField(final int newY, final int newX)
    {
        field = new GameState(newY, newX);
        gameDecided = false;
    }

    /**
     * Method is used to inform, the current gamining-field is closed
     */
    public void finish()
    {
        if (opponent != null)
        {
            opponent.sendMessage(new DataTransport("Opponent closed the game instance"));
        }
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
            field.setMyTurn(true);
            ui.resizeBoard(field.getYsize(), field.getXsize());
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
        final int decision = WinnerChecker.Run(field);// TestIfWon();
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

        final DataTransport.MobType type = uiTurn.GetMobType();
        switch (type)
        {
            case TURN:
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
                break;

            case CHAT:
                if (opponent != null)
                {
                    opponent.sendMessage(uiTurn);
                }
                break;

            default:
                break;
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
        final DataTransport.MobType type = tcpTurn.GetMobType();
        switch (type)
        {
            case CONFIG:
                resizeMyField(tcpTurn.getySize(), tcpTurn.getxSize());
                ui.resizeBoard(tcpTurn.getySize(), tcpTurn.getxSize());
                break;

            case TURN:
                field.setMyTurn(true);
                TurnPreformed(tcpTurn, State.OTHER);
                ui.setStone(field);
                break;

            case CHAT:
                ui.receiveMessage(tcpTurn.getChat());
                break;

            default:
                break;
        }
    }
}
