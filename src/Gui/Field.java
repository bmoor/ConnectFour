package Gui;

import Engine.DataTransport;
import Engine.Game;
import Engine.GameState;
import Engine.GameState.State;
import static Engine.GameState.State.*;
import java.awt.Color;
import java.awt.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Mario
 */
public class Field
{

    private JFrame frame;
    private JPanel panelBoard;
    private JPanel panelButtons;
    private JPanel panelMyColor;
    private JScrollPane chatScrollPane;
    private JTextPane messageTextPane;
    private JTextField messageEnterField;
    private JButton buttonSend;
    private JButton[] buttonSelectList;
    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenu menuGame;
    private JMenu menuResize;
    private JMenu menuHelp;
    private JMenuItem menuItemNew;
    private JMenuItem menuItemExit;
    private JMenuItem menuItemBackToLobby;
    private JMenuItem menuItemSave;
    private JMenuItem menuItemSizeA;
    private JMenuItem menuItemSizeB;
    private JMenuItem menuItemSizeC;
    private JMenuItem menuItemInfo;
    private JDialog dialogSave;
    private JDialog dialogInfo;
    private JTextArea infoText;
    private JLabel labelTurn;
    private JLabel labelMyColor;

    private Stone[][] stones;
    private State[][] board;
    private final Color myColor;
    private final Color otherColor;
    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 500;
    private int rows = 6;
    private int columns = 7;
    private final Game game;
    private boolean won;
    private boolean lost;
    private boolean drawn;
    private boolean isMyTurn;
    private boolean running = false;
    private String textChat = "";

    /**
     * Constructor. 
     * @param game Reference to the Game class
     * @param myTurn True if it's my turn
     */
    public Field(Game game, boolean myTurn)
    {
        this.game = game;
        isMyTurn = myTurn;
        won = false;
        lost = false;
        drawn = false;
        stones = new Stone[rows][columns];
        board = new State[rows][columns];
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < columns; c++)
            {
                board[r][c] = EMPTY;
            }
        }
        //Set myColor red, if myTurn is true, else set myColor yellow
        if (isMyTurn)
        {
            myColor = Color.RED;
            otherColor = Color.YELLOW;
        }
        else
        {
            myColor = Color.YELLOW;
            otherColor = Color.RED;
        }
        createGUI();
        createBoard(rows, columns);
        buttonController();
        setLabelText();
    }

    /**
     * Method to create the whole GUI. The default size of the board is 6 x 7
     */
    private void createGUI()
    {
        //Create the mainframe
        frame = new JFrame("Connect four");
        frame.setSize(1010, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                frame.setVisible(false);
                game.finish();
            }
        });

        //Create ScrollPane with white TextPane, if game against AI
        if (!game.againstAi())
        {
            messageTextPane = new JTextPane();
            chatScrollPane = new JScrollPane(messageTextPane);
            chatScrollPane.setBounds(680, 150, 300, 370);
            messageTextPane.setBounds(0, 0, 250, 370);
            messageTextPane.setBackground(Color.WHITE);
            messageTextPane.setEditable(false);
            frame.add(chatScrollPane);

            //Create a text field to enter messages
            messageEnterField = new JTextField();
            messageEnterField.setBounds(680, 530, 250, 30);
            messageEnterField.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    String s = messageEnterField.getText();
                    messageEnterField.setText("");
                    sendMessage(s);
                }
            });
            frame.add(messageEnterField);

            //Create button to send a message
            buttonSend = new JButton("Send");
            buttonSend.setBounds(935, 530, 45, 30);
            buttonSend.setMargin(new Insets(1, 1, 1, 1));
            buttonSend.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    String s = messageEnterField.getText();
                    messageEnterField.setText("");
                    sendMessage(s);
                }
            });
            frame.add(buttonSend);
        }

        //Create menubar
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        //Create menus in the menubar
        menuFile = new JMenu("File");
        menuBar.add(menuFile);
        menuGame = new JMenu("Game");
        menuBar.add(menuGame);
        menuHelp = new JMenu("Help");
        menuBar.add(menuHelp);

        //Create items in menu "File"
        //Save, if game against AI   
        if (game.againstAi())
        {
            menuItemSave = new JMenuItem("Save");
            menuFile.add(menuItemSave);
            menuItemSave.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    createSaveDialog();
                }
            });
        }

        //Back to lobby
        menuItemBackToLobby = new JMenuItem("Back to lobby");
        menuItemBackToLobby.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.setVisible(false);
                game.finish();
            }
        });
        menuFile.add(menuItemBackToLobby);

        //Separate the exit item from the other items
        menuFile.addSeparator();

        //Exit
        menuItemExit = new JMenuItem("Exit");
        menuItemExit.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        System.exit(0);
                    }
                });
        menuFile.add(menuItemExit);

        //Create items in menu "Game"
        //New
        menuItemNew = new JMenuItem("New");
        menuItemNew.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                running = false;
                createNewGame(rows, columns);
            }
        });
        menuGame.add(menuItemNew);

        //Resize
        menuResize = new JMenu("Resize Field");
        menuGame.add(menuResize);

        //Create submenus in "Resize"
        menuItemSizeA = new JMenuItem("6 x 7");
        menuItemSizeA.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                resizePressed(6, 7);
            }
        });
        menuResize.add(menuItemSizeA);

        menuItemSizeB = new JMenuItem("9 x 10");
        menuItemSizeB.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                resizePressed(9, 10);
            }
        });
        menuResize.add(menuItemSizeB);

        menuItemSizeC = new JMenuItem("12 x 13");
        menuItemSizeC.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                resizePressed(12, 13);
            }
        });
        menuResize.add(menuItemSizeC);

        //Create items in menu "Help"
        //Info
        menuItemInfo = new JMenuItem("Info");
        menuItemInfo.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                createInfoDialog();
            }
        });
        menuHelp.add(menuItemInfo);

        //Create the label that indicates who has to play
        labelTurn = new JLabel();
        labelTurn.setBounds(680, 100, 300, 35);
        Font schrift = new Font("Serif", Font.BOLD + Font.ITALIC, 25);
        labelTurn.setFont(schrift);
        setLabelText();
        frame.add(labelTurn);

        //Create the label that shows the players color
        labelMyColor = new JLabel("My color:");
        labelMyColor.setFont(new Font("Arial", 0, 20));
        labelMyColor.setBounds(680, 35, 90, 30);
        frame.add(labelMyColor);
        panelMyColor = new JPanel();
        panelMyColor.setBackground(myColor);
        panelMyColor.setBounds(775, 35, 30, 30);
        frame.add(panelMyColor);

        //Create the buttons on the frame
        createButtons();

        //Set visibility
        frame.setVisible(true);
    }

    /**
     * Method to create the save dialog, when "Save" was pressed in "File"
     */
    private void createSaveDialog()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Connect four game (*.c4)", "c4"));
        int r = chooser.showSaveDialog(dialogSave);
        String s = "no File!";
        if (r == JFileChooser.APPROVE_OPTION)
        {
            s = chooser.getSelectedFile().getPath();
            System.out.println(s);
            game.storeGame(s);
        }
    }

    /**
     * Method to create the info dialog, when "Info" was pressed in "Help"
     */
    private void createInfoDialog()
    {
        //Create the info dialog
        dialogInfo = new JDialog();
        dialogInfo.setTitle("Information");
        dialogInfo.setSize(450, 400);
        dialogInfo.setLayout(null);
        dialogInfo.setLocationRelativeTo(frame);
        dialogInfo.setResizable(false);

        //Create the text on the info dialog
        infoText = new JTextArea();
        infoText.setEditable(false);
        infoText.setBackground(null);
        infoText.setBounds(20, 20, 430, 380);
        infoText.setText(
                "Regeln:\n"
                + "Die Spieler legen nacheinander Steine in das Spielfeld.\n"
                + "Hierfür müssen die Buttons oberhalb der entspechenden Kolonne\n"
                + "angecklickt werden. Ist eine Kolonne voll, kann kein Stein\n"
                + "mehr hineingelegt werden.\n"
                + "Wer zuerst vier Steine horizontal, vertikal oder diagonal\n"
                + "nebeneinander hat gewinnt das Spiel.\n\n"
                + "Hinweise:\n"
                + "An Anfang eines Spiels kann die Grösse des Spielfeldes unter\n"
                + "\"Game\" -> \"Resize\" festgelegt werden. Sobald der erste\n"
                + "Spielzug gemacht wurde, kann die Grösse nicht mehr verändert\n"
                + "werden.\n"
                + "Mit \"Game\" -> \"New\" kann jederzeit ein neues Spiel gestartet\n"
                + "werden.\n"
                + "Wird gengen den Computer gespielt, kann das Spiel jederzeit unter\n"
                + "\"File\" -> \"Save\" gespeichert werden.\n\n"
                + "Autoren:\n"
                + "Leonini Mario, Moor Boris, Studer Yves");
        dialogInfo.add(infoText);
        dialogInfo.setVisible(true);
    }

    /**
     * Mehtod to create the buttons to select a column.
     */
    private void createButtons()
    {
        //Create panel 
        panelButtons = new JPanel();
        panelButtons.setBounds(65, 35, BOARD_WIDTH - 30, 45);
        panelButtons.setLayout(new GridLayout(1, columns, 20, 0));

        //Create buttons and add them to panel
        buttonSelectList = new JButton[columns];
        for (int i = 0; i < columns; i++)
        {
            final int j = i; //satisfying the compiler
            buttonSelectList[i] = new JButton("\u21E9"); //Uni-Code for an arrow down
            buttonSelectList[i].setMargin(new Insets(1, 1, 1, 1));
            buttonSelectList[i].addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    columnSelected(j);
                }
            });
            panelButtons.add(buttonSelectList[i]);
        }
        frame.add(panelButtons);
        panelButtons.setVisible(true);
    }

    /**
     * Method to set a stone in the selected column.
     * @param col Index of the selectet column
     */
    private void columnSelected(int col)
    {
        int index = rows - 1;
        boolean done = false;
        do
        {
            if (board[index][col] == (EMPTY))
            {
                board[index][col] = MINE;
                done = true;
            }
            index--;
        }
        while (!done && !isColumnFull(col));
        updateBoard();
        buttonController();
        sendMyStone(col);
    }

    /**
     * Method to check if a column is full.
     * @param col Number of the selectet column.
     * @return true if the column is full.
     */
    private boolean isColumnFull(int col)
    {
        boolean isFull = true;
        for (int r = 0; r < rows; r++)
        {
            if (board[r][col] == EMPTY)
            {
                isFull = false;
            }
        }
        return isFull;
    }

    /**
     * Method to reate the play board.
     * @param ro Number of rows
     * @param co Number of columns
     */
    private void createBoard(int ro, int co)
    {
        //Create a blue panel
        panelBoard = new JPanel();
        panelBoard.setBounds(50, 100, BOARD_WIDTH, BOARD_HEIGHT);
        panelBoard.setBackground(Color.BLUE);
        panelBoard.setLayout(new GridLayout(ro, co));
        frame.add(panelBoard);

        //Add white circles to panel
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < columns; c++)
            {
                stones[r][c] = new Stone(Color.WHITE);
                panelBoard.add(stones[r][c]);
            }
        }
        panelBoard.setVisible(true);
    }

    /**
     * Method that is called, if "Resize" in menu "Game" was pressed.
     * @param ro New numbers of rows
     * @param co New numbers of columns
     */
    private void resizePressed(int ro, int co)
    {
        resizeBoard(ro, co);
        sendNewBoardsizeToOther(ro, co);
    }

    /**
     * Method to resize the board.
     * @param ro New numbers of rows
     * @param co New numbers of columns
     */
    public void resizeBoard(int ro, int co)
    {
        rows = ro;
        columns = co;
        won = false;
        lost = false;
        drawn = false;
        stones = new Stone[rows][columns];
        board = new State[rows][columns];
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < columns; c++)
            {
                board[r][c] = EMPTY;
            }
        }
        panelButtons.setVisible(false);
        resizeMenuController();
        createButtons();
        isMyTurn = myColor.equals(Color.RED);
        buttonController();
        setLabelText();
        panelBoard.setVisible(false);
        createBoard(rows, columns);
        //
        panelBoard.setVisible(false);
        panelBoard.setVisible(true);
    }

    /**
     * Method to send the new boardsize to the opponent.
     * @param ro New number of rows 
     * @param co New number of columns
     */
    private void sendNewBoardsizeToOther(int ro, int co)
    {
        game.resizeField(ro, co);
    }

    /**
     * Method to create a new game
     * @param ro Number of rows
     * @param co Number of columns
     */
    private void createNewGame(int ro, int co)
    {
        resizeBoard(ro, co);
        sendNewBoardsizeToOther(ro, co);
    }

    /**
     * Method to enable and disable the buttons.
     */
    private void buttonController()
    {
        boolean b = isMyTurn;
        if (won || lost || drawn)
        {
            b = false;
        }
        for (int i = 0; i < columns; i++)
        {
            if (!isColumnFull(i))
            {
                buttonSelectList[i].setEnabled(b);
            }
            else
            {
                buttonSelectList[i].setEnabled(false);
            }
        }
    }

    /**
     * Method to change the text
     */
    private void setLabelText()
    {
        if (won)
        {
            labelTurn.setText("You won!");
            labelTurn.setForeground(Color.GREEN);
        }
        else if (lost)
        {
            labelTurn.setText("You lost...");
            labelTurn.setForeground(Color.RED);
        }
        else if (drawn)
        {
            labelTurn.setText("Drawn!");
            labelTurn.setForeground(Color.BLACK);
        }
        else
        {
            if (isMyTurn)
            {
                labelTurn.setText("It's your turn!");
                labelTurn.setForeground(Color.GREEN);
            }
            else
            {
                labelTurn.setText("Waiting for other player...");
                labelTurn.setForeground(Color.RED);
            }
        }
    }

    /**
     * Method to send a DataTransport object with the number of the 
     * selected column
     * @param col Number of the selected column
     */
    private void sendMyStone(int col)
    {
        DataTransport dt = new DataTransport(col);
        isMyTurn = false;
        buttonController();
        setLabelText();
        running = true;
        menuResize.setEnabled(false);
        game.UiTurnPreformed(dt);
    }

    /**
     * Method to send a DataTransport object with a text message
     * @param text Text message
     */
    private void sendMessage(String text)
    {
        textChat += "You:\n" + text + "\n\n";
        messageTextPane.setText(textChat);
        DataTransport dt = new DataTransport(text);
        game.UiTurnPreformed(dt);
    }

    /**
     * Method to recieve and display a text message
     * @param text Text message
     */
    public void receiveMessage(String text)
    {
        textChat += "Opponent:\n" + text + "\n\n";
        messageTextPane.setText(textChat);
    }

    /**
     * Method to recieve a new stone from the opponent by a GameState object
     * @param gs Object containing the number of the selected column by the opponent
     */
    public void setStone(GameState gs)
    {
        int myRow = rows - 1;
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < columns; c++)
            {
                board[myRow][c] = gs.getStone(r, c);
            }
            myRow--;  // Wüescht i weiss
        }
        isMyTurn = gs.isMyTurn();
        updateBoard();
        buttonController();
        setLabelText();
        running = true;
        resizeMenuController();
    }

    /**
     * Method to enable and disable the menu item "Resize".
     */
    private void resizeMenuController()
    {
        menuResize.setEnabled(!running || won || lost || drawn);
    }

    /**
     * Methot which is called from the game class if player won
     */
    public void won()
    {
        won = true;
        buttonController();
        setLabelText();
    }

    /**
     * Methot which is called from the game class if player lost
     */
    public void lost()
    {
        lost = true;
        buttonController();
        setLabelText();
    }

    /**
     * Methot which is called from the game class if nobody won
     */
    public void drawn()
    {
        drawn = true;
        buttonController();
        setLabelText();
    }

    /**
     * Method to update the colors of the stones on the board.
     */
    private void updateBoard()
    {
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < columns; c++)
            {
                if (board[r][c] == EMPTY)
                {
                    stones[r][c].setColor(Color.WHITE);
                }
                else if (board[r][c] == MINE)
                {
                    stones[r][c].setColor(myColor);
                }
                else if (board[r][c] == OTHER)
                {
                    stones[r][c].setColor(otherColor);
                }
            }
        }
    }

    /**
     * Inner class. 
     * Each object of this class represents a stone on the board.
     */
    private class Stone extends JPanel
    {

        private Color color;

        /**
         * Constructor
         * @param color Color of the stone
         */
        public Stone(Color color)
        {
            this.color = color;
            this.setBackground(Color.BLUE);
        }

        /**
         * Inherithed method from JPanel to paint circles on the JPanel
         */
        @Override
        public void paint(Graphics g)
        {
            super.paint(g);
            g.setColor(color);
            g.fillOval((int) ((BOARD_WIDTH / columns) * 0.1),
                    (int) ((BOARD_HEIGHT / rows) * 0.1),
                    (int) ((BOARD_WIDTH / columns) * 0.75),
                    (int) ((BOARD_WIDTH / columns) * 0.75));
        }

        /**
         * Method to  chance the color of the stone.
         * @param color New color object
         */
        public void setColor(Color color)
        {
            this.color = color;
            super.repaint();
        }
    }
}