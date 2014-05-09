/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Engine.GameState;
import Engine.GameState.State;
import static Engine.GameState.State.*;
import java.awt.Color;
import java.awt.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * @author Mario
 */
public class Field
{

    private JFrame frame;
    private JPanel panelBoard;
    private JPanel panelButtons;
    private JButton[] buttonSelectList;
    private Icon arrowRed;
    private Icon arrowGreen;
    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenu menuGame;
    private JMenu menuResize;
    private JMenu menuHelp;
    private JMenuItem menuItemExit;
    private JMenuItem menuItemOpen;
    private JMenuItem menuItemSave;
    private JMenuItem menuItemSize1;
    private JMenuItem menuItemSize2;
    private JMenuItem menuItemSize3;
    private JMenuItem menuItemInfo;
    private JDialog dialogOpen;
    private JDialog dialogInfo;
    private JLabel labelTurn;

    private Stone[][] stones;
    private State[][] board;
    private Color myColor = Color.RED;
    private Color otherColor = Color.YELLOW;
    private final int BOARD_WIDTH = 600;
    private final int BOARD_HEIGHT = 500;
    private int rows = 6;
    private int columns = 7;

    private boolean isMyTurn;

    public Field()
    {
        /**
        this.isMyTurn = isMyTurn;
        if(isMyTurn)
        {
            myColor = Color.RED;
            otherColor = Color.YELLOW;
        }
        else
        {
            myColor = Color.YELLOW;
            otherColor = Color.RED;
        }
        */
        
        stones = new Stone[rows][columns];
        
        //Nur zum testen
        board = new State[rows][columns];
        GameState gs = new GameState(rows, columns);
        gs.setMyTurn(true);
        gs.setStone(0, 0, State.OTHER);
        gs.setStone(0, 1, State.OTHER);
        gs.setStone(1, 1, State.OTHER);
        createGUI();
        createBoard(rows, columns);
        setStone(gs);
    }

    private void createGUI()
    {
        //Create frame
        frame = new JFrame("Connect four");
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false);

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
        //Open
        menuItemOpen = new JMenuItem("Open");
        menuFile.add(menuItemOpen);
        menuItemOpen.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                createOpenDialog();
            }
        });

        //Save
        menuItemSave = new JMenuItem("Save");
        menuFile.add(menuItemSave);
        menuItemSave.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                isMyTurn = true;
                buttonController();
                setTextTurn();
            }
        });

        //Separate exit from the other items
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

        //Create menu "Game"
        menuResize = new JMenu("Resize Field");
        menuGame.add(menuResize);
        //Create submenus in "Resize"
        menuItemSize1 = new JMenuItem("6 x 7");
        menuItemSize1.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                resizeBoard(6, 7);
            }
        });
        menuResize.add(menuItemSize1);
        menuItemSize2 = new JMenuItem("9 x 10");
        menuItemSize2.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                resizeBoard(9, 10);
            }
        });
        menuResize.add(menuItemSize2);
        menuItemSize3 = new JMenuItem("12 x 13");
        menuItemSize3.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                resizeBoard(12, 13);
            }
        });
        menuResize.add(menuItemSize3);

        //Create items in menu "Help"
        //Info
        menuItemInfo = new JMenuItem("Info");
        menuHelp.add(menuItemInfo);
        menuItemInfo.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                createInfoDialog();
            }
        });

        //Create the label, that indicates who has to play
        labelTurn = new JLabel();
        setTextTurn();
        labelTurn.setBounds(680, 100, 300, 35);
        Font schrift = new Font("Serif", Font.BOLD + Font.ITALIC, 25);
        labelTurn.setFont(schrift);
        frame.add(labelTurn);

        createButtons();

        //Set visibility
        frame.setVisible(true);

    }

    private void setTextTurn()
    {
        if (isMyTurn)
        {
            labelTurn.setForeground(Color.GREEN);
            labelTurn.setText("It's your turn!");
        }
        else
        {
            labelTurn.setForeground(Color.RED);
            labelTurn.setText("Waiting for other player...");
        }

    }

    //Create dialog when "Open" was pressed in "File"
    private void createOpenDialog()
    {
        dialogOpen = new JDialog();
        dialogOpen.setTitle("Open file");
        dialogOpen.setSize(200, 200);
        dialogOpen.setLocationRelativeTo(frame);
        dialogOpen.setVisible(true);
    }

    //Create dialog when "Info" was pressed in "Help"
    private void createInfoDialog()
    {
        dialogInfo = new JDialog();
        dialogInfo.setTitle("Information");
        dialogInfo.setSize(200, 200);
        dialogInfo.setLocationRelativeTo(frame);
        dialogInfo.setVisible(true);

    }

    private void createButtons()
    {
        //Create panel 
        panelButtons = new JPanel();
        panelButtons.setBounds(65, 35, BOARD_WIDTH - 30, 45);
        panelButtons.setLayout(new GridLayout(1, columns, 20, 0));

        //Create icons
        /**
         * arrowRed = new ImageIcon(getClass().getResource("PfeilRot.jpg"));
         * arrowRed. arrowGreen = new
         * ImageIcon(getClass().getResource("PfeilGrün.jpg"));
         */
        
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
            if (board[index][col] == EMPTY)
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
        updateBoard();
        panelBoard.setVisible(true);
    }

    public void resizeBoard(int ro, int co)
    {
        rows = ro;
        columns = co;
        sendNewBoardsizeToOther(rows,columns);
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
        createButtons();
        panelBoard.setVisible(false);
        createBoard(rows, columns);

        //Abfragen ob Änderung möglich
        //Info an Klasse Game (neues GUI)
    }
    
    private void sendNewBoardsizeToOther(int ro, int co)
    {
        System.out.println(ro + " " + co);
    }

    private void buttonController()
    {
        for (int i = 0; i < columns; i++)
        {
            if (!isColumnFull(i))
            {
                buttonSelectList[i].setEnabled(isMyTurn);
            }
            else
            {
                buttonSelectList[i].setEnabled(false);
            }
        }
    }

    private void sendMyStone(int col)
    {
        isMyTurn = false;
        buttonController();
        setTextTurn();
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
        setTextTurn();

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
