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
    private JMenuItem menuItemSize1;
    private JMenuItem menuItemSize2;
    private JMenuItem menuItemSize3;
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
    private final int BOARD_WIDTH = 600;
    private final int BOARD_HEIGHT = 500;
    private int rows = 6;
    private int columns = 7;
    private final Game game;
    private boolean won;
    private boolean lost;
    private boolean drawn;
    private boolean isMyTurn;
    private boolean running = false;
    private String textChat = "";

    public Field(Game game, boolean myTurn)
    {
        this.game = game;
        isMyTurn = myTurn;
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
        createGUI();
        createBoard(rows, columns);
        buttonController();
        setLabelText();
    }

    private void createGUI()
    {
        //Create frame
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
        menuGame = new JMenu("Game");
        menuHelp = new JMenu("Help");
        menuBar.add(menuFile);
        menuBar.add(menuGame);
        menuBar.add(menuHelp);

        //Create items in menu "File"
        //Create save-item if game against AI   
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

        //Separate exit from the other items
        menuFile.addSeparator();

        //Exit
        menuItemExit = new JMenuItem("Exit");
        menuItemExit.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        System.exit(0);
                    }
                });
        menuFile.add(menuItemExit);

        //Create menu "Game"
        //New
        menuItemNew = new JMenuItem("New");
        menuGame.add(menuItemNew);

        menuItemNew.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        running = false;
                        createNewGame(rows, columns);
                    }
                });

        //Resize
        menuResize = new JMenu("Resize Field");
        menuGame.add(menuResize);

        //Create submenus in "Resize"
        menuItemSize1 = new JMenuItem("6 x 7");
        menuItemSize1.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e
                    )
                    {
                        resizePressed(6, 7);
                    }
                });
        menuResize.add(menuItemSize1);

        menuItemSize2 = new JMenuItem("9 x 10");
        menuItemSize2.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e
                    )
                    {
                        resizePressed(9, 10);
                    }
                });
        menuResize.add(menuItemSize2);

        menuItemSize3 = new JMenuItem("12 x 13");
        menuItemSize3.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e
                    )
                    {
                        resizePressed(12, 13);
                    }
                });
        menuResize.add(menuItemSize3);

        //Create items in menu "Help"
        //Info
        menuItemInfo = new JMenuItem("Info");
        menuHelp.add(menuItemInfo);
        menuItemInfo.addActionListener(
                new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e
                    )
                    {
                        createInfoDialog();
                    }
                });

        //Create the label, that indicates who has to play
        labelTurn = new JLabel();
        labelTurn.setBounds(680, 100, 300, 35);
        Font schrift = new Font("Serif", Font.BOLD + Font.ITALIC, 25);
        labelTurn.setFont(schrift);
        setLabelText();
        frame.add(labelTurn);

        //Shows the player his own color
        labelMyColor = new JLabel("My color:");
        labelMyColor.setFont(new Font("Arial", 0, 20));
        labelMyColor.setBounds(680, 35, 90, 30);
        panelMyColor = new JPanel();
        panelMyColor.setBackground(myColor);
        panelMyColor.setBounds(775, 35, 30, 30);
        frame.add(labelMyColor);
        frame.add(panelMyColor);

        //Create the buttons on the frame
        createButtons();

        //Set visibility
        frame.setVisible(true);
    }

    //Create dialog when "Save" was pressed in "File"
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

    //Create dialog when "Info" was pressed in "Help"
    private void createInfoDialog()
    {
        dialogInfo = new JDialog();
        dialogInfo.setTitle("Information");
        dialogInfo.setSize(450, 400);
        dialogInfo.setLayout(null);
        dialogInfo.setLocationRelativeTo(frame);

        infoText = new JTextArea();
        infoText.setEditable(false);
        infoText.setBackground(null);
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
        infoText.setBounds(20, 20, 430, 380);
        dialogInfo.setVisible(true);
    }

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
            final int j = i;
            buttonSelectList[i] = new JButton("\u21E9");
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
            if (isColumnFull(col))
            {
                buttonSelectList[col].setEnabled(false);
            }
        }
        while (!done && !isColumnFull(col));
        updateBoard();
        sendMyStone(col);
    }

    /**
     * Method to check if a column is full.
     *
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

    private void createBoard(int ro, int co)
    {
        //create a blue panel
        panelBoard = new JPanel();
        panelBoard.setBounds(50, 100, BOARD_WIDTH, BOARD_HEIGHT);
        panelBoard.setBackground(Color.BLUE);
        panelBoard.setLayout(new GridLayout(ro, co, 0, 0));
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

    private void resizePressed(int ro, int co)
    {
        resizeBoard(ro, co);
        sendNewBoardsizeToOther(ro, co);
    }

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

    private void sendNewBoardsizeToOther(int ro, int co)
    {
        game.resizeField(ro, co);
    }

    private void createNewGame(int ro, int co)
    {
        resizeBoard(ro, co);
        sendNewBoardsizeToOther(ro, co);
    }

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

    private void sendMessage(String text)
    {
        textChat += "You:\n" + text + "\n\n";
        messageTextPane.setText(textChat);
        DataTransport dt = new DataTransport(text);
        game.UiTurnPreformed(dt);
    }

    public void receiveMessage(String text)
    {
        textChat += "Opponent:\n" + text + "\n\n";
        messageTextPane.setText(textChat);
    }

    public void setStone(GameState gs)
    {

        int myRow = rows - 1;
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < columns; c++)
            {
                board[myRow][c] = gs.getStone(r, c);
            }
            myRow--;// Wüescht i weiss
        }
        isMyTurn = gs.isMyTurn();
        updateBoard();
        buttonController();
        setLabelText();
        running = true;
        resizeMenuController();
    }

    private void resizeMenuController()
    {
        menuResize.setEnabled(!running || won || lost || drawn);
    }

    public void won()
    {
        won = true;
        buttonController();
        setLabelText();
    }

    public void lost()
    {
        lost = true;
        buttonController();
        setLabelText();
    }

    public void drawn()
    {
        drawn = true;
        buttonController();
        setLabelText();
    }

    //Check each entry in "board", create a new circle
    //in the specific color and add it to the board
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

    private class Stone extends JPanel
    {

        private Color color;

        public Stone(Color c)
        {
            color = c;
            this.setBackground(Color.BLUE);
        }

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

        public void setColor(Color col)
        {
            color = col;
            super.repaint();
        }
    }
}
